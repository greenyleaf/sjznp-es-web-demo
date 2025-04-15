package org.example.sjznpeswebdemo.util;

import org.example.sjznpeswebdemo.entity.PriceItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface AppUtil {
    Logger log = LoggerFactory.getLogger(AppUtil.class);

    Pattern DATE_REGEX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    Pattern PRICE_REGEX = Pattern.compile("\\d+(\\.\\d+)?");

    static PriceItem wrapFromList(List<String> list) {
        if (list.size() != 6) {
            log.info("list size is not 6");
            return null;
        }
        if (!AppConstant.TYPE_NAMES.contains(list.get(0))) {
            log.info("type name not matched, of {}", list.get(0));
            return null;
        }

        if (!DATE_REGEX.matcher(list.get(5)).matches()) {
            log.info("wrong date format");
            return null;
        }

        return new PriceItem(MessageFormat.format("{0}-{1}", list.get(5), list.get(1)), list.get(0), list.get(1), parsePrice(list.get(2)), parsePrice(list.get(3)), parsePrice(list.get(4)), LocalDate.parse(list.get(5)));
    }

    static BigDecimal parsePrice(String str) {
        Matcher matcher = PRICE_REGEX.matcher(str);
        if (matcher.find()) {
            return new BigDecimal(matcher.group());
        } else {
            log.info("parsePrice match failed");
            return null;
        }
    }
}
