package com.test;


import org.locationtech.jts.geom.Coordinate;

public class MainTest {
    public static void main(String[] args) {
        Coordinate c1 = new Coordinate(45.464256, 9.191383);
        Coordinate c2 = new Coordinate(45.464239, 9.191421);
        System.out.println(c1.distance(c2)*111000 );



    }
}
