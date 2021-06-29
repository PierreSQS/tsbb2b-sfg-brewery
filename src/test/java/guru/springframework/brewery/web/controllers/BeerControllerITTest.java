package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerControllerITTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("integration test list Beers")
    void testListBeers() {
        BeerDto expectedBeerDto = new BeerDto();
        expectedBeerDto.setBeerName("Mango Bobs");
        expectedBeerDto.setBeerStyle(BeerStyleEnum.IPA);
        expectedBeerDto.setUpc(337010000001L);
        BeerPagedList beerPagedList = testRestTemplate.getForObject("/api/v1/beers/", BeerPagedList.class);
        assertThat(beerPagedList.stream().findFirst().get().getBeerName()).isEqualTo(expectedBeerDto.getBeerName());

        System.out.println(beerPagedList);
    }
}