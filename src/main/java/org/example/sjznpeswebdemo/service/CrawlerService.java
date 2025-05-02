package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.repository.PriceItemRepository;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.example.sjznpeswebdemo.util.AppConstant;
import org.example.sjznpeswebdemo.util.AppUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
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

    public CrawlerService(CrawlerManager crawlerManager, PricePageRepository pricePageRepository, SaveManager saveManager, ReactiveElasticsearchOperations reactiveElasticsearchOperations, PriceItemRepository priceItemRepository) {
        this.crawlerManager = crawlerManager;
        this.pricePageRepository = pricePageRepository;
        this.priceItemRepository = priceItemRepository;
    }

    Flux<LocalDate> dateProducer(LocalDate start) {
        Flux<LocalDate> flux = Flux.create(fluxSink -> {
            LocalDate end = LocalDate.now();

            for (LocalDate d = start; end.isAfter(d) && !fluxSink.isCancelled(); d = d.plusDays(1)) {
                fluxSink.next(d);
            }

            fluxSink.complete();
        });
        return flux;
    }

    Flux<LocalDate> dateProducerByMonth(LocalDate start) {
        Flux<LocalDate> flux = Flux.create(fluxSink -> {
            LocalDate end = start.withDayOfMonth(1).plusMonths(1);
            if (end.isAfter(LocalDate.now())) {
                end = LocalDate.now();
            }

            for (LocalDate d = start; end.isAfter(d) && !fluxSink.isCancelled(); d = d.plusDays(1)) {
                fluxSink.next(d);
            }

            fluxSink.complete();
        });
        return flux;
    }

    List<PriceItem> parsePriceItems(Document doc) {
        Element tbody = doc.selectFirst("tbody");
        // "TypeName, ProName, low, min, max, ADate"
        if (tbody != null && tbody.childrenSize() > 0) {
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

    Mono<Long> saveByDate(LocalDate date) {
        log.info("saveByDate entered");
        log.info("saveByDate, date: {}", date);

        return crawlerManager.crawlByDate(date)
                .collectList()
                .flatMapMany(pricePageRepository::saveAll)
                .flatMapSequential(pricePage -> {
                    List<PriceItem> list = parsePriceItems(Jsoup.parse(pricePage.getContent()));

                    return Mono.just(list);
                })
                .concatMap(priceItems ->
                        priceItems.isEmpty() ? Mono.just(0)
                                : priceItemRepository.saveAll(priceItems)
                                .count())
                .count()
                .doOnNext(count -> {
                    log.info("stage 6, count: {}", count);
                });

        /*return pricePageRepository
                .saveAll(pricePageFlux)
                .thenMany(priceItemRepository.saveAll(priceItemFlux));*/
    }

    Flux<PriceItem> saveAllPageItems() {
        log.info("saveAllPageItems entered");

        Flux<PriceItem> priceItemFlux = priceItemRepository.findFirstByOrderByDateDesc()
                // .map(priceItem -> priceItem.getDate().plusDays(1))
                .map(PriceItem::getDate)
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .doOnNext(date -> {
                    log.info("saveAllPageItems, is of after date: {}", date);
                })
                .flatMapMany(pricePageRepository::findAllByDateIsAfterOrderByDateAscPageNo)
                .flatMapSequential(pricePage ->
                        Flux.fromIterable(parsePriceItems(Jsoup.parse(pricePage.getContent()))));

        log.info("saveAllPageItems, saving");

        return priceItemRepository.saveAll(priceItemFlux);
    }

    Mono<Long> processTillNow() {
        return pricePageRepository.findFirstByOrderByDateDesc()
                .map(pricePage -> pricePage.getDate().plusDays(1))
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .flatMapMany(this::dateProducer)
                .concatMap(this::saveByDate)
                .count()
                ;
    }

    Mono<Long> processOneMonth() {
        return pricePageRepository.findFirstByOrderByDateDesc()
                .map(pricePage -> pricePage.getDate().plusDays(1))
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .doOnNext(date -> {
                    log.info("processing from month range: {}", date);
                })
                .flatMapMany(this::dateProducerByMonth)
                .concatMap(this::saveByDate)
                .count()
                ;
    }

}
