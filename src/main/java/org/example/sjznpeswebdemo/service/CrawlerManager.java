package org.example.sjznpeswebdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sjznpeswebdemo.entity.PriceItem;
import org.example.sjznpeswebdemo.entity.PricePage;
import org.example.sjznpeswebdemo.util.AppConstant;
import org.example.sjznpeswebdemo.util.AppUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CrawlerManager {
    private final WebClient webClient;

    public CrawlerManager() {
        this.webClient = WebClient.builder()
                .baseUrl(AppConstant.BASE_URL).build();
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

    int parsePageCount(Document doc) {
        String href = doc.select(".page a:last-child").attr("href");
        if (!href.isEmpty()) {
            final Pattern pageNoPattern = Pattern.compile("page=(\\d+)");
            Matcher matcher = pageNoPattern.matcher(href);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }

        // 当只有一页、或者没有数据， page 标签内容是空的
        return 1;
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

    Mono<PricePage> crawlOnePage(LocalDate date, Integer pageNo, Integer pageCount) {
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

                                    if (pageCount != null) {
                                        pricePage.setPageCount(pageCount);
                                    }
                                    return pricePage;
                                }));
    }

    Flux<PricePage> crawlAllSubPage(LocalDate date, Integer pageCount) {
        return Flux.range(2, pageCount - 1)
                .flatMapSequential(integer -> crawlOnePage(date, integer, pageCount), 5);
    }

    public Flux<PricePage> crawlByDate(LocalDate date) {

        return crawlOnePage(date, 1, null)
                .doOnNext(pricePage -> {
                    Document document = Jsoup.parse(pricePage.getContent());
                    int pageCount = parsePageCount(document);
                    pricePage.setPageCount(pageCount);
                })
                .flatMapMany(pricePage -> {
                            log.info("pricePage.getPageCount(), {}", pricePage.getPageCount());
                            return Mono.just(pricePage)
                                    .concatWith(crawlAllSubPage(pricePage.getDate(), pricePage.getPageCount()));
                        }
                );
    }

}
