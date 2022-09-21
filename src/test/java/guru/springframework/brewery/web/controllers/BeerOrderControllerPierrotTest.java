package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerPierrotTest {

    @MockBean
    BeerOrderService beerOrderSrvMock;

    @Autowired
    MockMvc mockMvc;

    BeerOrderDto validBeerOrder;

    @BeforeEach
    void setUp() {
        validBeerOrder = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .orderStatus(OrderStatusEnum.READY)
                .version(111)
                .customerId(UUID.randomUUID())
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
//        reset(beerOrderSrvMock);
    }

    @Test
    void  testGetOrderById() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        // Given
          given(beerOrderSrvMock.getOrderById(any(),any())).willReturn(validBeerOrder);

        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}",
                        validBeerOrder.getCustomerId(),validBeerOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value(is(equalTo(OrderStatusEnum.READY.toString()))))
                .andExpect(jsonPath("$.version").value(is(equalTo(111))))
                .andExpect(jsonPath("$.createdDate").value(
                        containsString(dateTimeFormatter.format(validBeerOrder.getCreatedDate()))))
                .andExpect(jsonPath("$.lastModifiedDate").value(
                        containsString(dateTimeFormatter.format(validBeerOrder.getCreatedDate()))))
                .andDo(print());
    }

    @DisplayName("Test List BeerOrders Ops - ")
    @Nested
    class TestListOperations {

        @Captor
        ArgumentCaptor<UUID> uuidCaptor;

        BeerOrderPagedList beerOrderPagedList;

        @Captor
        ArgumentCaptor<Pageable> pageableCaptor;

        @BeforeEach
        void setUp() {
            List<BeerOrderDto> beerOrderDtoList = new ArrayList<>();

            BeerOrderDto validBeerOrder2 = BeerOrderDto.builder()
                    .id(UUID.randomUUID())
                    .orderStatus(OrderStatusEnum.NEW)
                    .version(222)
                    .customerId(UUID.randomUUID())
                    .createdDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now())
                    .build();

            beerOrderDtoList.add(validBeerOrder);
            beerOrderDtoList.add(validBeerOrder2);

            beerOrderPagedList = new BeerOrderPagedList(beerOrderDtoList,
                    PageRequest.of(1,5),10);

        }

        @DisplayName("List BeerOrders no parameters")
        @Test
        void testListBeerOrders() throws Exception {
            // Given
            Customer custMock = Customer.builder()
                    .id(UUID.randomUUID())
                    .customerName("Pierrot")
                    .build();

            given(beerOrderSrvMock.listOrders(any(),any())).willReturn(beerOrderPagedList);

            mockMvc.perform(get("/api/v1/customers/{customerId}/orders", custMock.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[1].orderStatus").value(is(equalTo("NEW"))))
                    .andExpect(jsonPath("$.content").value(is(hasSize(2))))
                    .andDo(print());

            verify(beerOrderSrvMock).listOrders(uuidCaptor.capture(),pageableCaptor.capture());

            assertEquals(custMock.getId(),uuidCaptor.getValue());
            assertEquals(25,pageableCaptor.getValue().getPageSize());

        }
    }

    @Test
    void listOrders() {
    }

    @Test
    void placeOrder() {
    }

    @Test
    void getOrder() {
    }

    @Test
    void pickupOrder() {
    }
}