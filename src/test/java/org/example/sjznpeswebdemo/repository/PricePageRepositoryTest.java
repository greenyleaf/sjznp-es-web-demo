package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;

import java.time.LocalDate;

@Disabled
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
                    log.info("pricePage.getDate: {}", pricePage.getDate());
                    log.info("pricePage.getPageNo: {}", pricePage.getPageNo());
                    log.info("pricePage.getContent.length: {}", pricePage.getContent().length());
                })
                .count()
                .doOnNext(count -> log.info("count: {}", count))
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

    @Test
    void findFirstByOrderByDateDescTest() {
        PricePage block = pricePageRepository.findFirstByOrderByDateDesc()
                .doOnNext(pricePage -> {
                    log.info("pricePage.getDate: {}", pricePage.getDate());
                    log.info("pricePage.getPageNo: {}", pricePage.getPageNo());
                    log.info("pricePage.getContent.length: {}", pricePage.getContent().length());
                })
                .block();
    }

    @Test
    void findFirstByOrderByDateTest() {
        PricePage block = pricePageRepository.findFirstByOrderByDate()
                .doOnNext(pricePage -> {
                    log.info("pricePage.getDate: {}", pricePage.getDate());
                    log.info("pricePage.getPageNo: {}", pricePage.getPageNo());
                    log.info("pricePage.getContent.length: {}", pricePage.getContent().length());
                })
                .block();
    }

    @Test
    void findByOrderByDateAndPageNoTest() {
        pricePageRepository.findByOrderByDateAscPageNo(PageRequest.of(0, 20))
                .doOnNext(pricePage -> {
                    log.info("pricePage.getDate: {}", pricePage.getDate());
                    log.info("pricePage.getPageNo: {}", pricePage.getPageNo());
                    log.info("pricePage.getContent.length: {}", pricePage.getContent().length());
                })
                .blockLast();
    }

    @Test
    void countByDateTest() {
        pricePageRepository.countByDate(LocalDate.parse("2024-06-01"))
                .doOnNext(count -> log.info("count: {}", count))
                .block()
        ;
    }

    @Test
    void countByDateTest2() {
        pricePageRepository.countByDate(LocalDate.parse("2024-07-14"))
                .doOnNext(count -> log.info("count: {}", count))
                .block()
        ;
    }

    @Test
    void findAllByDateIsAfterOrderByDateAscPageNoTest() {
        pricePageRepository.findAllByDateIsAfterOrderByDateAscPageNo(LocalDate.parse("2025-01-01"))
                .doOnNext(pricePage -> {
                    log.info("pricePage.getDate: {}", pricePage.getDate());
                })
                .blockLast()
        ;
    }
}
