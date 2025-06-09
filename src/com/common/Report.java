package com.common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Report implements Serializable {
    private final Pothole pothole;
    private final String username;
    private final String timeStamp;

    public Report(Pothole pothole, String username){
        this.pothole = pothole;
        this.username = username;
        this.timeStamp = LocalDateTime.now().format(Data.formatter);
    }

    public Pothole getPothole() {
        return pothole;
    }

    public String getReporter() {
        return username;
    }

    @Override
    public String toString() {
        return timeStamp+","+pothole.toString() + "," +username;
    }
}
