package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CrawlerService {
    private final WebClient webClient;

    public CrawlerService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.sjznp.com/Home/PriceTrend").build();
    }

    Flux<LocalDate> dateProducer(LocalDate start) {
        Flux<LocalDate> flux = Flux.create(fluxSink -> {
            // LocalDate start = LocalDate.parse("2025-02-01");
            LocalDate end = LocalDate.now();

            for (LocalDate d = start; !d.isAfter(end) && !fluxSink.isCancelled(); d = d.plusDays(1)) {
                fluxSink.next(d);
                log.info("fluxSink.next, {}", d);
            }

            fluxSink.complete();
        });
        return flux;
    }

    int extractPageCount(Document doc) {
        String href = doc.select(".page a:last-child").attr("href");
        if (!href.isEmpty()) {
            Pattern pageNoPattern = Pattern.compile("page=(\\d+)");
            Matcher matcher = pageNoPattern.matcher(href);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        // 当只有一页、或者没有数据， page 标签内容是空的
        return 1;
    }

    Mono<PricePage> crawlPage(LocalDate date, Integer pageNo) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("ADate", date).queryParam("page", pageNo).build())
                .exchangeToMono(clientResponse ->
                        clientResponse
                                .bodyToMono(String.class)
                                .map(s -> {
                                    PricePage pricePage = new PricePage(date, pageNo);

                                    pricePage.setDate(date);
                                    pricePage.setFetched(true);
                                    pricePage.setHttpStatus(clientResponse.statusCode().value());
                                    pricePage.setContent(s);
                                    return pricePage;
                                }));
    }

}
