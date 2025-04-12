package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.dto.PageDto;
import org.example.sjznpeswebdemo.entity.PriceItem;
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
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QueryService {

    private final ReactiveElasticsearchOperations reactiveElasticsearchOperations;

    public QueryService(ReactiveElasticsearchOperations reactiveElasticsearchOperations) {
        this.reactiveElasticsearchOperations = reactiveElasticsearchOperations;
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
                )
                .withHighlightQuery(
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
