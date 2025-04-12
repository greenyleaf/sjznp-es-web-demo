package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class PriceItemRepositoryTest {
    @Autowired
    PriceItemRepository priceItemRepository;
    @Autowired
    private ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @Test
    void countTest() {
        Long aLong = priceItemRepository.count().block();
        log.info("count: {}", aLong);
    }

    @Test
    void deleteAllTest() {
        priceItemRepository.deleteAll().block();
    }

    @Test
    void deleteIndexTest() {
        reactiveElasticsearchOperations.indexOps(PriceItem.class).delete()
                .block();
    }

    @Test
    void firstAllTest() {
        priceItemRepository.findAll()
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast();
    }

    @Test
    void firstTest() {
        PriceItem item = priceItemRepository.findAll()
                .blockFirst();

        log.info("first: {}", item);
    }

    @Test
    void findFirstByOrderByDateDescTest() {
        priceItemRepository.findFirstByOrderByDateDesc()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void findFirstByOrderByDateTest() {
        priceItemRepository.findFirstByOrderByDate()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void findTopByTest() {
        priceItemRepository.findTopBy()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void countByDateTest() {
        priceItemRepository.countByDate(LocalDate.parse("2024-06-02"))
                .doOnNext(count -> log.info("count: {}", count))
                .block();
    }

    @Test
    void countByDateTest2() {
        priceItemRepository.countByDate(LocalDate.parse("2024-07-15"))
                .doOnNext(count -> log.info("count: {}", count))
                .block();
    }
}
