package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class CrawlerManagerTest {
    @Autowired
    CrawlerManager crawlerManager;
    @Autowired
    private PricePageRepository pricePageRepository;

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
                    log.info("pricePage.getPageCount, {}", pricePage.getPageCount());
                    log.info("pricePage.getContent.length, {}", pricePage.getContent().length());
                })
                .blockLast();

    }

    @Test
    void crawlByDateTest2() {
        Flux<PricePage> pricePageFlux = crawlerManager.crawlByDate(LocalDate.parse("2025-04-10"));

        pricePageFlux
                .count()
                .doOnNext(count -> {
                    log.info("count, {}", count);
                })
                .block();
    }

    @Test
    void crawlByDateTest3() {
        pricePageRepository.saveAll(crawlerManager.crawlByDate(LocalDate.parse("2025-04-10")))
                .blockLast();
    }
}
