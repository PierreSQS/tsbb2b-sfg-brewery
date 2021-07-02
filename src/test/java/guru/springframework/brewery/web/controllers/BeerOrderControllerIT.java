package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerOrderControllerIT {
    @Autowired
    TestRestTemplate testRestTemp;

    @Autowired
    CustomerRepository customerRepo;

    Customer cust;

    @BeforeEach
    void setUp() {
        cust = customerRepo.findAll().get(0);
    }

    @Test
    void testListOrders() {
        String testUrl = "/api/v1/customers/"+cust.getId()+"/orders";
        System.out.printf("##### %nthe test URL %s%n%n####",testUrl);
        BeerOrderPagedList beerOrderPagedList = testRestTemp.getForObject(testUrl, BeerOrderPagedList.class);
        assertThat(beerOrderPagedList.getContent()).hasSize(1);
    }


}
