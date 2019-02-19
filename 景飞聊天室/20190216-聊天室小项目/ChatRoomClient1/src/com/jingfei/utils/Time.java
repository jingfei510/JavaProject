package com.jingfei.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Time {
    public static String getTime() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DateTimeFormatter.ofPattern("MM月dd日 HH:mm:ss"));
        return time;
    }
}
