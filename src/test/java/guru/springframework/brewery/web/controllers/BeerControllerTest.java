package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    void listBeers() throws Exception {
        // Given
        String beerNameMock = "Beer Mock";
        BeerStyleEnum beerStyleEnumMock = BeerStyleEnum.PILSNER;
        Integer pageNumberMock = 1;
        Integer pageSizeMock = 3;

        BeerDto beerDtoMock1 = new BeerDto();
        beerDtoMock1.setBeerName("Beer Mock1");
        beerDtoMock1.setBeerStyle(BeerStyleEnum.PILSNER);

        BeerDto beerDtoMock2 = new BeerDto();
        beerDtoMock2.setBeerName("Beer Mock2");

        List<BeerDto> beerDtoListMock = new ArrayList<>(Arrays.asList(beerDtoMock1,beerDtoMock2));
        BeerPagedList beerPagedListMock = new BeerPagedList(beerDtoListMock,PageRequest.of(pageNumberMock,pageSizeMock),2);
//        Works also
//        BeerPagedList beerPagedListMock = new BeerPagedList(beerDtoListMock);

        when(beerServiceMock.listBeers(beerNameMock, BeerStyleEnum.PILSNER,
                PageRequest.of(pageNumberMock,pageSizeMock))).thenReturn(beerPagedListMock);

        mockMvc.perform(get("/api/v1/beers")
                    .param("pageNumber",Integer.toString(pageNumberMock))
                    .param("pageSize",Integer.toString(pageSizeMock))
                    .param("beerName",beerNameMock)
                    .param("beerStyle",BeerStyleEnum.PILSNER.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].beerStyle", equalTo("PILSNER")))
                .andExpect(jsonPath("$.pageable.sort.unsorted",equalTo(true)))
                .andExpect(jsonPath("$.pageable.offset",equalTo(3)))
                .andExpect(jsonPath("$.numberOfElements",equalTo(2)))
                .andDo(print());
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