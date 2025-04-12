package org.example.sjznpeswebdemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Slf4j
public class NoobDateTest {
    @Test
    void test() {
        LocalDate date = LocalDate.parse("2022-01-31");
        date = date.plusMonths(1);
        log.info("date is {}", date);
    }

    @Test
    void test2() {
        LocalDate date = LocalDate.parse("2024-06-01");
        log.info("date is {}", date);
    }
}
