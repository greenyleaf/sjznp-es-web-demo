package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.dto.PageDto;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.repository.PricePageRepository;
import org.example.sjznpeswebdemo.util.AppConstant;
import org.example.sjznpeswebdemo.util.AppUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private final SaveManager saveManager;
    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    public CrawlerService(CrawlerManager crawlerManager, PricePageRepository pricePageRepository, SaveManager saveManager, ReactiveElasticsearchOperations reactiveElasticsearchOperations) {
        this.crawlerManager = crawlerManager;
        this.pricePageRepository = pricePageRepository;
        this.saveManager = saveManager;
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
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

    Flux<PriceItem> saveByDate(LocalDate date) {
        log.info("saveByDate entered");

        Flux<PricePage> pricePageFlux = crawlerManager.crawlByDate(date);

        Flux<PriceItem> priceItemFlux =
                pricePageFlux
                        .flatMapSequential(pricePage ->
                                Flux.fromIterable(parsePriceItems(Jsoup.parse(pricePage.getContent()))));

        log.info("saveByDate, saving: {}", date);

        return saveManager.save(pricePageFlux, priceItemFlux);
        /*return pricePageRepository
                .saveAll(pricePageFlux)
                .thenMany(priceItemRepository.saveAll(priceItemFlux));*/
    }

    Mono<Long> processTillNow() {
        return pricePageRepository.findFirstByOrderByDateDesc()
                .map(pricePage -> pricePage.getDate().plusDays(1))
                .switchIfEmpty(Mono.just(AppConstant.INITIAL_DATE))
                .flatMapMany(this::dateProducer)
                .flatMapSequential(
                        this::saveByDate,
                        1
                )
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
                .flatMapSequential(
                        this::saveByDate,
                        1
                )
                .count()
                ;
    }

    public Mono<PageDto> query(String typeName, LocalDate date, String name, boolean exact,
                               Integer page, Integer size) {
        log.info("query entered");

        Criteria criteria = Criteria.and();

        if (StringUtils.hasLength(typeName)) {
            criteria = criteria.and("typeName")
                    .is(typeName);
        }
        if (date != null) {
            criteria = criteria.and("date")
                    .is(date);
        }
        if (StringUtils.hasLength(name)) {

            if (exact) {
                criteria = criteria.and("name.key")
                        .is(name);
            } else {
                criteria = criteria.and("name")
                        .matchesAll(name);
            }
        }

        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }

        CriteriaQuery query = CriteriaQuery
                .builder(criteria)
                .withPageable(Pageable.ofSize(size).withPage(page))
                .withSort(Sort
                                .sort(PriceItem.class).by(PriceItem::getDate).descending()
                                .and(Sort.sort(PriceItem.class).by(PriceItem::getTypeName))
                        // .and(Sort.by("name.key"))
                ).withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(
                                        List.of(
                                                new HighlightField("name",
                                                        HighlightFieldParameters
                                                                .builder()
                                                                .withPreTags("<mark>")
                                                                .withPostTags("</mark>")
                                                                .build())
                                        )
                                ), PriceItem.class)
                )
                .build();

        return reactiveElasticsearchOperations.searchForPage(query, PriceItem.class)
                .map(searchHits -> new PageDto(
                        searchHits.getNumber(),
                        searchHits.getSize(),
                        searchHits.getTotalPages(),
                        (int) searchHits.getTotalElements(),
                        searchHits.getContent().stream().map(SearchHit::getContent).collect(Collectors.toUnmodifiableList())
                ));
    }

}
