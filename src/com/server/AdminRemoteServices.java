package com.server;

import com.common.Pothole;
import com.common.Report;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;


public interface AdminRemoteServices extends Remote {
    LinkedList<String> getUsers() throws RemoteException;
    TreeSet<Pothole> getPotholes() throws RemoteException;
    void saveData(String fileToSave) throws RemoteException;
    void loadData(String fileToLoad) throws RemoteException;
    LinkedList<Report> getReportsByUsername(String username) throws RemoteException;
    boolean registerNewUser(String username, int passwordHash) throws RemoteException;
    boolean fixReportedPothole(Report report) throws RemoteException;
    TreeMap<Integer, LinkedList<String>> getActiveUsers() throws RemoteException;
    TreeMap<String, LinkedList<Report>> getAllReports() throws RemoteException;
}
