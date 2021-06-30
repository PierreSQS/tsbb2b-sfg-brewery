package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.CustomerDto;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    private static final String BASEURL = "/api/v1/customers/";

    CustomerDto customerDtoMock;

    BeerOrderDto beerOrderDtoMock;

    BeerOrderDto beerOrderDtoMock1;

    BeerOrderDto beerOrderDtoMock2;

    DateTimeFormatter dateTimeFormatter;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @MockBean
    BeerOrderService beerOrderSrvMock;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
     customerDtoMock = CustomerDto.builder()
                .id(UUID.fromString("4793b3fc-0f52-485f-910c-7915a8a4e312"))
                .build();

     beerOrderDtoMock = BeerOrderDto.builder()
                .id(UUID.fromString("a6935dc7-9adb-421b-b9d7-eb76c6271f6e"))
                .customerId(customerDtoMock.getId())
                .orderStatus(OrderStatusEnum.NEW)
                .build();

     beerOrderDtoMock1 = BeerOrderDto.builder()
                .customerId(customerDtoMock.getId())
                .orderStatus(OrderStatusEnum.PICKED_UP)
                .createdDate(OffsetDateTime.now())
                .build();

     beerOrderDtoMock2 = BeerOrderDto.builder()
                .customerId(customerDtoMock.getId())
                .orderStatus(OrderStatusEnum.READY)
                .createdDate(OffsetDateTime.now())
                .build();



        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    }

    @Test
    void listOrders() throws Exception {
        // Given
        List<BeerOrderDto> beerOrderDtoList = new ArrayList<>(Arrays.asList(beerOrderDtoMock1,beerOrderDtoMock2));

        BeerOrderPagedList beerOrderPagedListMock =
                new BeerOrderPagedList(beerOrderDtoList, PageRequest.of(1,3),3);

        when(beerOrderSrvMock.listOrders(uuidArgumentCaptor.capture(), pageableArgumentCaptor.capture()))
                .thenReturn(beerOrderPagedListMock);

        // When, Then
        final String customerID = customerDtoMock.getId().toString();
        final String createDateItem2 = beerOrderDtoList.get(0).getCreatedDate().format(dateTimeFormatter);

        mockMvc.perform(get(BASEURL+ customerID +"/orders/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].orderStatus",equalTo(OrderStatusEnum.PICKED_UP.toString())))
                .andExpect(jsonPath("$.content[1].createdDate",is(createDateItem2)))
                .andExpect(jsonPath("$.pageable.sort.sorted",is(false)))
                .andDo(print());

        System.out.printf("%n####### $.content[1].createdDate: %s ######%n%n", createDateItem2);
    }

    @Test
    void placeOrder() {
    }

    @Test
    void getOrder() throws Exception {
        // Given
        when(beerOrderSrvMock.getOrderById(customerDtoMock.getId(),beerOrderDtoMock.getId()))
                .thenReturn(beerOrderDtoMock);

        // When, Then
        final String customerID = customerDtoMock.getId().toString();
        final String orderID = beerOrderDtoMock.getId().toString();
        ResultActions perform = mockMvc.perform(get(BASEURL+ customerID +"/orders/"+ orderID));
        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.customerId",equalTo(beerOrderDtoMock.getCustomerId().toString())))
                .andExpect(jsonPath("$.orderStatus",equalTo("NEW")))
                .andDo(print());
    }

    @Test
    void pickupOrder() throws Exception {
        final String customerId = customerDtoMock.getId().toString();
        final String beerOrderDtoID = beerOrderDtoMock.getId().toString();

        mockMvc.perform(put((BASEURL+ customerId +"/orders/"+ beerOrderDtoID +"/pickup")))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}