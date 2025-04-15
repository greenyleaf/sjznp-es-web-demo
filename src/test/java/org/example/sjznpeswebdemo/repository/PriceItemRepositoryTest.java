package org.example.sjznpeswebdemo.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Collections;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class PriceItemRepositoryTest {
    @Autowired
    PriceItemRepository priceItemRepository;
    @Autowired
    private ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    @Test
    void countTest() {
        priceItemRepository.count()
                .doOnNext(count -> log.info("count, {}", count))
                .block();
    }

    @Test
    void deleteAllTest() {
        priceItemRepository.deleteAll().block();
    }

    @Test
    void deleteByDateIsAfterTest() {
        priceItemRepository.deleteByDateIsAfter(LocalDate.parse("2024-11-28"))
                .doOnNext(count -> log.info("count, {}", count))
                .block();
    }

    @Test
    void deleteIndexTest() {
        reactiveElasticsearchOperations.indexOps(PriceItem.class).delete()
                .block();
    }

    @Test
    void firstAllTest() {
        priceItemRepository.findAll()
                .take(100)
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast();
    }

    @Test
    void firstTest() {
        PriceItem item = priceItemRepository.findAll()
                .blockFirst();

        log.info("first: {}", item);
    }

    @Test
    void findFirstByOrderByDateDescTest() {
        priceItemRepository.findFirstByOrderByDateDesc()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void findFirstByOrderByDateTest() {
        priceItemRepository.findFirstByOrderByDate()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void findTopByTest() {
        priceItemRepository.findTopBy()
                .doOnNext(priceItem -> log.info("first: {}", priceItem))
                .block();
    }

    @Test
    void findByNameTest() {
        priceItemRepository.findByNameIs("鸭", PageRequest.of(0, 20))
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast()
        ;
    }

    @Test
    void findByDateTest() {
        priceItemRepository.findByDate(LocalDate.now().minusWeeks(1), PageRequest.of(0, 20))
                .doOnNext(item -> log.info("item: {}", item))
                .blockLast()
        ;
    }

    @Test
    void countByNameTest() {
        priceItemRepository.countByName("西瓜")
                .doOnNext(count -> log.info("count: {}", count))
                .block()
        ;
    }

    @Test
    void countByNameTest2() {
        priceItemRepository.countByName("鸡腿菇")
                .doOnNext(count -> log.info("count: {}", count))
                .block()
        ;
    }

    @Test
    void findByNameMatchesTest() {
        // Sort sort = Sort.by(Sort.Direction.DESC, "id");
        priceItemRepository.findByNameMatchesOrderByDateAscTypeNameAsc("鱼", PageRequest.of(0, 20))
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast()
        ;
    }

    @Test
    void findByNameMatchesTest2() {
        priceItemRepository.findByNameMatchesOrderByDateAscTypeNameAsc("枣", PageRequest.of(0, 20))
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast()
        ;
    }

    @Test
    void findByTypeNameAndDateAndNameMatchesTest() {
        priceItemRepository
                .findByTypeNameAndDateAndNameMatches("水果", LocalDate.now().minusWeeks(1), "瓜", PageRequest.of(0, 20))
                .doOnNext(priceItem -> log.info("priceItem: {}", priceItem))
                .blockLast()
        ;
    }

    @Test
    void countByDateTest() {
        priceItemRepository.countByDate(LocalDate.parse("2024-11-27"))
                .doOnNext(count -> log.info("count: {}", count))
                .block();
    }

    @Test
    void countByDateTest2() {
        priceItemRepository.countByDate(LocalDate.parse("2024-07-15"))
                .doOnNext(count -> log.info("count: {}", count))
                .block();
    }

    @Test
    void saveAllEmptyTest() {
        priceItemRepository.saveAll(Collections.emptyList())
                .count()
                .doOnNext(aLong -> log.info("count: {}", aLong))
                .block();
    }

    @Test
    void saveAllEmptyTest2() {
        Flux<PriceItem> flux = Flux.empty();

        priceItemRepository.saveAll(flux)
                .count()
                .doOnNext(aLong -> log.info("count: {}", aLong))
                .block();
    }
}
