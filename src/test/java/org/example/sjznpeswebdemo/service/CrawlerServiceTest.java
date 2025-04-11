package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class CrawlerServiceTest {
    @Autowired
    CrawlerService crawlerService;

    @Test
    void dateProducerTest() {
        crawlerService.dateProducer(LocalDate.parse("2025-02-01"))
                .doOnNext(localDate -> {
                    log.info("localDate, {}", localDate);
                })
                .blockLast();
    }

    @Test
    void dateProducerTest2() {
        crawlerService.dateProducer(LocalDate.parse("2025-02-01"))
                .doOnNext(localDate -> {
                    log.info("localDate, {}", localDate);
                })
                .doOnCancel(() -> log.info("cancel"))
                .take(5)
                .blockLast();
    }
}
