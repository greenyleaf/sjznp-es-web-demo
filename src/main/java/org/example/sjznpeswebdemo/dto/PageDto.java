package org.example.sjznpeswebdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.sjznpeswebdemo.entity.PriceItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    private int page;
    private int size;
    private int totalPages;
    private int total;
    private List<PriceItem> items;
}
