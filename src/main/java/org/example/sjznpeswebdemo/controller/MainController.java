package org.example.sjznpeswebdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.dto.PageDto;
import org.example.sjznpeswebdemo.service.CrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@Slf4j
public class MainController {
    private final CrawlerService crawlerService;

    public MainController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @GetMapping
    public Mono<PageDto> query(@RequestParam(required = false) String typeName,
                               @RequestParam(required = false) LocalDate date,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) boolean exact,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "20") Integer size) {
        if (page < 0) {
            page = 0;
        }
        return crawlerService.query(typeName, date, name, exact, page, size);
    }
}
