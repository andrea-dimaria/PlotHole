package com.server;

import com.common.Report;

import com.common.ReportStatus;
import org.locationtech.jts.geom.Coordinate;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.LinkedList;



public interface RemoteServices extends Remote {
    boolean login(String username, int passwordHash) throws RemoteException;
    boolean register(String username, int passwordHash) throws RemoteException;
    ReportStatus makeReport(Coordinate coordinate, int intensity, String username) throws RemoteException;
    LinkedList<Report> getReports(String username) throws RemoteException;


}