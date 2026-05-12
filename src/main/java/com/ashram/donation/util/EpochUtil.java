package com.ashram.donation.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpochUtil {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private EpochUtil() {}

    public static long nowInSeconds() {
        return Instant.now().getEpochSecond();
    }

    public static String deriveMonthPartition(Long epochSeconds) {
        String month = Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .format(MONTH_FORMATTER);
        return "MONTH#" + month;
    }

    public static List<String> getMonthPartitions(Long startEpoch, Long endEpoch) {
        List<String> partitions = new ArrayList<>();
        LocalDate start = Instant.ofEpochSecond(startEpoch).atZone(ZoneOffset.UTC).toLocalDate().withDayOfMonth(1);
        LocalDate end   = Instant.ofEpochSecond(endEpoch).atZone(ZoneOffset.UTC).toLocalDate().withDayOfMonth(1);
        while (!start.isAfter(end)) {
            partitions.add("MONTH#" + start.format(MONTH_FORMATTER));
            start = start.plusMonths(1);
        }
        return partitions;
    }
}
