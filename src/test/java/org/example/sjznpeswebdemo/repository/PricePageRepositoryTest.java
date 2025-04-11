package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class PricePageRepositoryTest {
    @Autowired
    PricePageRepository pricePageRepository;
    @Autowired
    private ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @Test
    void countTest() {
        Long aLong = pricePageRepository.count().block();
        log.info("count: {}", aLong);
    }

    @Test
    void findAllTest() {
        pricePageRepository.findAll()
                .doOnNext(pricePage -> {
                    log.info("pricePage.getPageNo: {}", pricePage.getPageNo());
                })
                .count()
                .doOnNext(count -> log.info("findAll: {}", count))
                .block();
    }

    @Test
    void deleteAllTest() {
        pricePageRepository.deleteAll()
                .block();
    }

    @Test
    void deleteIndexTest() {
        reactiveElasticsearchOperations.indexOps(PricePage.class).delete()
                .block();
    }

    @Test
    void firstTest() {
        PricePage item = pricePageRepository.findAll()
                .blockFirst();

        log.info("first: {}", item);
    }
}
