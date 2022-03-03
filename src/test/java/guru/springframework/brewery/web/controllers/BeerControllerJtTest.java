package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class BeerControllerJtTest {

    @Mock
    BeerService beerSrvMock;

    @InjectMocks
    BeerController beerController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(beerController).build();
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

        // Given
        given(beerSrvMock.findBeerById(any())).willReturn(beerDtoMock);

        // When and Then
        mockMvc.perform(get("/api/v1/beers/{beerId}",beerDtoMock.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.beerName",equalTo("33 Export")))
                .andDo(print());
    }
}