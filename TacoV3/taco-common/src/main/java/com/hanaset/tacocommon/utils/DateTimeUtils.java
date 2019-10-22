package com.hanaset.tacocommon.utils;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    private static DateFormat createSdf(String sdfFormat, String timeZone) {
        DateFormat dateFormat = new SimpleDateFormat(sdfFormat);
        if (!StringUtils.isEmpty(timeZone))
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        else
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        return dateFormat;
    }

    public static String getTimezoneDateStr(String dateStr, String fromTimeZone, String toTimeZone) {
        String currentTimeStr="";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(fromTimeZone));

        Date date;
        try {
            date = dateFormat.parse(dateStr);

            dateFormat.setTimeZone(TimeZone.getTimeZone(toTimeZone));
            currentTimeStr = dateFormat.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return currentTimeStr;
    }

    public static String getCurrentDay(String timeZone) {
        if (StringUtils.isEmpty(timeZone))
            timeZone = "UTC";

        DateFormat dateFormat = createSdf("yyyy-MM-dd", timeZone);

        return dateFormat.format(new Date());
    }


    public static String getCurrentBeforeNDay(String format, String timeZone, int nday) {
        if (StringUtils.isEmpty(timeZone))
            timeZone = "UTC";

        DateFormat dateFormat = createSdf(format, timeZone);

        Date date = Date.from(Instant.now().minus(Duration.ofDays(nday)));

        return dateFormat.format(date);
    }
}
