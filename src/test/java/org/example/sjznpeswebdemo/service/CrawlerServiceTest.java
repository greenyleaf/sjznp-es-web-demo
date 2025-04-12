package org.example.sjznpeswebdemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.example.sjznpeswebdemo.util.AppConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class CrawlerServiceTest {
    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private PricePageRepository pricePageRepository;

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

    @Test
    void dateProducerByMonthTest() {
        crawlerService.dateProducerByMonth(LocalDate.parse("2025-01-22"))
                .doOnNext(date -> {
                    log.info("date, {}", date);
                })
                .blockLast();
    }

    @Test
    void saveByDateTest() {
        crawlerService.saveByDate(LocalDate.parse("2024-06-01"))
                .count()
                .doOnNext(count -> {
                    log.info("count, {}", count);
                })
                .block();
    }

    @Test
    void saveByDateTest2() {
        crawlerService.saveByDate(LocalDate.parse("2024-06-01"))
                .doOnNext(item -> {
                    log.info("doOnNext");
                })
                .blockLast()
        ;
    }

    @Test
    void processTillNowTest() {
        pricePageRepository.findFirstByOrderByDateDesc()
                .map(pricePage -> pricePage.getDate().plusDays(1))
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .doOnNext(localDate -> {
                    log.info("localDate, {}", localDate);
                })
                .block();
    }

    @Test
    void processTillNowTest2() {
        crawlerService.processTillNow()
                .doOnNext(count -> log.info("count, {}", count))
                .block()
        ;
    }

    @Test
    void processOneMonthTest() {
        crawlerService.processOneMonth()
                .doOnNext(count -> log.info("count, {}", count))
                .block()
        ;
    }

    @Test
    void queryTest() {
        crawlerService.query(null, LocalDate.parse("2025-04-09"), "鱼", false, 0, 20)
                // crawlerService.query(null, null, "鱼", false, 0, 20)
                // crawlerService.query("禽蛋类", LocalDate.parse("2024-06-01"), null, false, 0, 20)
                // crawlerService.query("禽蛋类", null, "鸡", false, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                // .doOnNext(pageDto -> log.info("pageDto: {}", pageDto))
                .block();
    }

    @Test
    void queryTest2() {
        crawlerService.query(null, null, null, false, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                // .doOnNext(pageDto -> log.info("pageDto: {}", pageDto))
                .block();
    }

    @Test
    void queryExactTest() {
        crawlerService.query(null, null, "西瓜", true, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                .block();
    }
}
