package org.example.sjznpeswebdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "price_item")
public class PriceItem {
    @Id
    private Long id;
    @Field(type = FieldType.Keyword)
    private String typeName;
    @MultiField(mainField = @Field(type = FieldType.Text), otherFields = @InnerField(suffix = "key", type = FieldType.Keyword))
    private String name;
    private BigDecimal min;
    private BigDecimal avg;
    private BigDecimal max;
    @Field(format = DateFormat.basic_date, type = FieldType.Date)
    private LocalDate date;

    @CreatedDate
    @Field(format = DateFormat.date_hour_minute_second, index = false, type = FieldType.Date)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createTime;
    @LastModifiedDate
    @Field(format = DateFormat.date_hour_minute_second, index = false, type = FieldType.Date)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updateTime;

    public PriceItem(String typeName, String name, BigDecimal min, BigDecimal avg, BigDecimal max, LocalDate date) {
        this.typeName = typeName;
        this.name = name;
        this.min = min;
        this.avg = avg;
        this.max = max;
        this.date = date;
    }
}
