package org.example.sjznpeswebdemo.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface AppConstant {
    /**
     * 网页价格的最早有效日期
     */
    LocalDate INITIAL_DATE = LocalDate.parse("2024-06-01");

    List<String> TYPE_NAMES = Arrays.asList(
            "蔬菜",
            "果品",
            "水产",
            "禽蛋类",
            "粮油",
            "肉类"
    );

    String BASE_URL = "https://www.sjznp.com/Home/PriceTrend";
}
