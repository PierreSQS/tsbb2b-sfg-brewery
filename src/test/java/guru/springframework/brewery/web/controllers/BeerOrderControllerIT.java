package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {
    @Autowired
    TestRestTemplate testRestTemp;

    @Test
    void testListOrders() {
        String testUrl = "/api/v1/customers/74bf4163-e2f7-405c-921c-b602f4a28f8a/orders";
        BeerOrderPagedList beerOrderPagedList = testRestTemp.getForObject(testUrl, BeerOrderPagedList.class);
        assertThat(beerOrderPagedList.getContent()).hasSize(1);
    }


}
