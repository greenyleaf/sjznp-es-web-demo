package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
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

    @Test
    void crawlPageTest() {
        crawlerService.crawlPage(LocalDate.parse("2025-04-10"), 1)
                .doOnNext(pricePage -> {
                    log.info("pricePage, {}", pricePage);
                })
                .block()
        ;
    }

    @Test
    void extractPageCountTest() {
        crawlerService.crawlPage(LocalDate.parse("2025-04-10"), 1)
                .map(pricePage -> {
                    // log.info("pricePage, {}", pricePage);

                    // log.info("pricePage.getContent(), {}", pricePage.getContent());
                    return Jsoup.parse(pricePage.getContent());
                })
                .doOnNext(document -> {
                    log.info("document, {}", document);

                    Elements elements = document.select(".page a:last-child");
                    log.info("elements.size, {}", elements.size());
                    String href = elements.attr("href");
                    log.info("href, {}", href);

                    /*int count = crawlerService.extractPageCount(document);
                    log.info("count, {}", count);*/
                })
                .block()
        ;
    }
}
