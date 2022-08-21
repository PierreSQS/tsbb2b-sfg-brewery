package guru.springframework.brewery.web.controllers;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BeerOrderService beerOrderSrvMock;

    BeerOrderDto beerOrderDto;

    @BeforeEach
    void setUp() {
        beerOrderDto = BeerOrderDto.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .orderStatus(OrderStatusEnum.READY)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getOrder() throws Exception {
        // Given
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerOrderSrvMock.getOrderById(any(),any())).willReturn(beerOrderDto);
        // When and Then
        mockMvc.perform(get("/api/v1/customers/{customerId}/orders/{orderId}",
                            beerOrderDto.getCustomerId(),beerOrderDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",not(isEmptyString())))
                .andExpect(jsonPath("$.orderStatus",equalTo(OrderStatusEnum.READY.toString())))
                .andExpect(jsonPath("$.createdDate",
                        is(dateTimeFormatter.format(beerOrderDto.getCreatedDate()))))
//                        equalTo(beerOrderDto.getCreatedDate()))) // Doesn't work because not formatted
                .andDo(print());
    }

    @DisplayName("List Orders Tests: ")
    @Nested
    class TestListOperations {

        @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        List<BeerOrderDto> beerOrderDtos;

        @BeforeEach
        void setUp() {
            beerOrderDtos = new ArrayList<>();
            beerOrderDtos.add(beerOrderDto);
            beerOrderDtos.add(BeerOrderDto.builder().id(UUID.randomUUID())
                    .version(1)
                    .orderStatus(OrderStatusEnum.PICKED_UP)
                    .createdDate(OffsetDateTime.now())
                    .lastModifiedDate(OffsetDateTime.now())
                    .build());

            BeerOrderPagedList beerOrderPagedList = new BeerOrderPagedList(beerOrderDtos, PageRequest.of(1, 1), 2L);

            given(beerOrderSrvMock.listOrders(any(),any()))
                    .willReturn(beerOrderPagedList);
        }

        @DisplayName("Test List Orders - no parameters")
        @Test
        void testListBeers() throws Exception {
            mockMvc.perform(get("/api/v1/customers/{customerId}/orders", UUID.randomUUID())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[1].orderStatus")
                            .value(equalTo(beerOrderDtos.get(1).getOrderStatus().toString())))
                    .andDo(print());
        }
    }

}