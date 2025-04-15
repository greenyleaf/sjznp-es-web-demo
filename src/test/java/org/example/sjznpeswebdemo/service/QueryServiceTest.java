package org.example.sjznpeswebdemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class QueryServiceTest {
    @Autowired
    private QueryService queryService;

    @Test
    void queryTest() {
        queryService.query(null, LocalDate.parse("2025-04-09"), "果", false, 0, 20)
                // crawlerService.query(null, null, "鱼", false, 0, 20)
                // crawlerService.query("禽蛋类", LocalDate.parse("2024-06-01"), null, false, 0, 20)
                // crawlerService.query("禽蛋类", null, "鸡", false, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto: {}", pageDto);
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                // .doOnNext(pageDto -> log.info("pageDto: {}", pageDto))
                .block();
    }

    @Test
    void queryTest2() {
        queryService.query(null, null, null, false, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                // .doOnNext(pageDto -> log.info("pageDto: {}", pageDto))
                .block();
    }

    @Test
    void queryExactTest() {
        queryService.query(null, null, "西瓜", true, 0, 20)
                .doOnNext(pageDto -> {
                    log.info("pageDto items: {}", JSON.toJSONString(pageDto.getItems(), JSONWriter.Feature.PrettyFormat));
                })
                .block();
    }
}