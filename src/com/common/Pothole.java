package com.common;

import org.locationtech.jts.geom.Coordinate;

import java.io.Serializable;

public class Pothole implements Comparable<Pothole>, Serializable {

    private Coordinate coordinate;
    private int intensity;

    public Pothole(Coordinate coordinate, int intensity){
        this.coordinate = coordinate;
        this.intensity = intensity;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getIntensity() {
        return intensity;
    }

    @Override
    public String toString() {
        return "("+coordinate.x +","+coordinate.y+"),"+ intensity;
    }

    @Override
    public int compareTo(Pothole o) {
        if (o == null) return 1;

        int latCompare = Double.compare(this.coordinate.getY(), o.coordinate.getY());
        if (latCompare != 0) return latCompare;

        int lonCompare = Double.compare(this.coordinate.getX(), o.coordinate.getX());
        if (lonCompare != 0) return lonCompare;

        return Integer.compare(this.intensity, o.intensity);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Pothole)) return false;
        Pothole pothole = (Pothole) o;

        return this.coordinate.equals2D(pothole.getCoordinate(),Data.METER_TO_DEGREE) &&
                this.intensity == pothole.intensity;
    }

    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }
}