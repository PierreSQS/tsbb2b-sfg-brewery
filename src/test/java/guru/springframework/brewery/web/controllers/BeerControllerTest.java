package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Beer;
import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    void listBeers() {
    }

    @Test
    void getBeerById() throws Exception {
        BeerDto beerDtoMock = BeerDto.builder()
                .beerName("33 Export")
                .id(UUID.fromString("e83e0307-43c2-4e28-8131-21b23ffb4dd9"))
                .price(BigDecimal.TEN)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();


        when(beerServiceMock.findBeerById(beerDtoMock.getId())).thenReturn(beerDtoMock);

        ResultActions perform = mockMvc.perform(get("/api/v1/beers/"+beerDtoMock.getId().toString()));

        perform.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.beerName", equalTo("33 Export")))
                .andExpect(jsonPath("$.price", equalTo("10")))
                .andDo(print());

        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        System.out.printf("The response: %s",contentAsString);
    }
}