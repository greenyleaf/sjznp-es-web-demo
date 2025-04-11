package org.example.sjznpeswebdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "price_page")
public class PricePage {
    @Id
    private String id;
    private LocalDate date;
    private Integer pageNo;
    private Boolean fetched;
    private String content;
    private Integer httpStatus;
    private String tableData;
    private Integer pageTotal;

    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;

    public PricePage(LocalDate date, Integer pageNo) {
        this.date = date;
        this.pageNo = pageNo;
    }
}
