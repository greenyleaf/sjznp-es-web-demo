package org.example.sjznpeswebdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "price_page")
public class PricePage {
    @Id
    private String id;
    @Field(format = DateFormat.basic_date, type = FieldType.Date)
    private LocalDate date;
    private Integer pageNo;
    private Boolean fetched;
    @Field(index = false)
    private String content;
    private Integer httpStatus;

    private String tableData;
    private Integer pageCount;

    @CreatedDate
    @Field(format = DateFormat.date_hour_minute_second, index = false, type = FieldType.Date)
    private LocalDateTime created;
    @LastModifiedDate
    @Field(format = DateFormat.date_hour_minute_second, index = false, type = FieldType.Date)
    private LocalDateTime updated;

    public PricePage(LocalDate date, Integer pageNo) {
        this.date = date;
        this.pageNo = pageNo;
    }
}
