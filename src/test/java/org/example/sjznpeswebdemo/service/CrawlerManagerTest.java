package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class CrawlerManagerTest {
    @Autowired
    CrawlerManager crawlerManager;

    @Test
    void dateProducerTest() {
        crawlerManager.dateProducer(LocalDate.parse("2025-02-01"))
                .doOnNext(localDate -> {
                    log.info("localDate, {}", localDate);
                })
                .blockLast();
    }

    @Test
    void dateProducerTest2() {
        crawlerManager.dateProducer(LocalDate.parse("2025-02-01"))
                .doOnNext(localDate -> {
                    log.info("localDate, {}", localDate);
                })
                .doOnCancel(() -> log.info("cancel"))
                .take(5)
                .blockLast();
    }

    @Test
    void crawlOnePageTest() {
        crawlerManager.crawlOnePage(LocalDate.parse("2025-04-10"), 1, null)
                .doOnNext(pricePage -> {
                    log.info("pricePage, {}", pricePage);
                })
                .block()
        ;
    }

    @Test
    void parsePageCountTest() {
        crawlerManager.crawlOnePage(LocalDate.parse("2025-04-10"), 1, null)
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

                    int count = crawlerManager.parsePageCount(document);
                    log.info("count, {}", count);
                })
                .block()
        ;
    }

    @Test
    void crawlByDateTest() {
        Flux<PricePage> pricePageFlux = crawlerManager.crawlByDate(LocalDate.parse("2025-04-10"));

        pricePageFlux.doOnNext(pricePage -> {
                    log.info("pricePage.getId, {}", pricePage.getId());
                    log.info("pricePage.getContent.length, {}", pricePage.getContent().length());
                })
                .blockLast();

    }
}
