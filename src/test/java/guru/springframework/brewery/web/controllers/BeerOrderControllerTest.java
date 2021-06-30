package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.CustomerDto;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    private static final String BASEURL = "/api/v1/customers";

    @MockBean
    BeerOrderService beerOrderSrvMock;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void listOrders() {
    }

    @Test
    void placeOrder() {
    }

    @Test
    void getOrder() throws Exception {
        // Given
        CustomerDto customerDtoMock = CustomerDto.builder()
                .id(UUID.fromString("4793b3fc-0f52-485f-910c-7915a8a4e312"))
                .build();

        BeerOrderDto beerOrderDtoMock = BeerOrderDto.builder()
                .id(UUID.fromString("a6935dc7-9adb-421b-b9d7-eb76c6271f6e"))
                .customerId(customerDtoMock.getId())
                .orderStatus(OrderStatusEnum.NEW)
                .build();

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
    void pickupOrder() {
    }
}