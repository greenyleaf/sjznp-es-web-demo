package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.repository.PriceItemRepository;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.example.sjznpeswebdemo.util.AppConstant;
import org.example.sjznpeswebdemo.util.AppUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CrawlerService {
    private final CrawlerManager crawlerManager;
    private final PricePageRepository pricePageRepository;
    private final PriceItemRepository priceItemRepository;
    private final ConcurrentMapCacheManager cacheManager;

    public CrawlerService(CrawlerManager crawlerManager, PricePageRepository pricePageRepository, PriceItemRepository priceItemRepository, ConcurrentMapCacheManager cacheManager) {
        this.crawlerManager = crawlerManager;
        this.pricePageRepository = pricePageRepository;
        this.priceItemRepository = priceItemRepository;
        this.cacheManager = cacheManager;
    }

    Flux<LocalDate> dateProducer(LocalDate start) {
        Flux<LocalDate> flux = Flux.create(fluxSink -> {
            LocalDate end = LocalDate.now();

            for (LocalDate d = start; end.isAfter(d) && !fluxSink.isCancelled(); d = d.plusDays(1)) {
                fluxSink.next(d);
                log.info("fluxSink.next, {}", d);
            }

            fluxSink.complete();
        });
        return flux;
    }

    List<PriceItem> extractPriceItems(Document doc) {
        Element tbody = doc.selectFirst("tbody");
        // TypeName, ProName, low, min, max, ADate
        if (tbody != null && tbody.childrenSize() > 0) {
            // log.info("tbody, \n{}", tbody.html());

            return tbody.children()
                    .stream()
                    .map(tr -> tr.children()
                            .stream()
                            .map(Element::text)
                            .collect(Collectors.toList()))
                    .map(AppUtil::wrapFromList)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    Flux<PriceItem> saveByDate(LocalDate date) {
        Flux<PricePage> pricePageFlux = crawlerManager.crawlByDate(date);

        Flux<PriceItem> priceItemFlux =
                pricePageFlux
                        .flatMapSequential(pricePage ->
                                Flux.fromIterable(extractPriceItems(Jsoup.parse(pricePage.getContent()))));

        return pricePageRepository
                .saveAll(pricePageFlux)
                .thenMany(priceItemRepository.saveAll(priceItemFlux));
    }

    Mono<Long> crawlTillNow() {
        return pricePageRepository.findFirstByOrderByDateDesc()
                .map(PricePage::getDate)
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .flatMapMany(this::dateProducer)
                .flatMapSequential(
                        this::saveByDate,
                        1
                )
                .count()
                ;
    }
}
