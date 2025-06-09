package com.common;

import java.time.format.DateTimeFormatter;

public class Data {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static final double METER_TO_DEGREE = 0.000009;
    public static final int DISTANCE_THRESHOLD = 25;
    public static final int DEGREE_TO_METER = 111000;
    public static final String INTERNAL_USER = "ADMIN";
    public static final String USER_FILENAME = "users.bin";
    public static final String REPORT_FILENAME = "reports.bin";
    public static final String POTHOLE_FILENAME = "potholes.bin";
    public static final String LOGGER_NAME = "CustomLogger";
    public static final String LOG_FILENAME = "logs.txt";
}
