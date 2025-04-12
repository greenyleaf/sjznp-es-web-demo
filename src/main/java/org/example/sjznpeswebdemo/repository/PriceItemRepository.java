package org.example.sjznpeswebdemo.repository;

import org.example.sjznpeswebdemo.entity.PriceItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface PriceItemRepository extends ReactiveElasticsearchRepository<PriceItem, String> {
    Mono<PriceItem> findFirstByOrderByDateDesc();

    Mono<PriceItem> findFirstByOrderByDate();

    Mono<PriceItem> findTopBy();

    Flux<PriceItem> findByNameIs(String name, Pageable pageable);

    Flux<PriceItem> findByDate(LocalDate date, Pageable pageable);

    Mono<Long> countByName(String name);

    Flux<PriceItem> findByNameMatchesOrderByDateAsc(String name, Pageable pageable);

    Flux<PriceItem> findByTypeNameAndDateAndNameMatches(String typeName, LocalDate date, String name, Pageable pageable);

    Mono<Long> countByDate(LocalDate date);
}
