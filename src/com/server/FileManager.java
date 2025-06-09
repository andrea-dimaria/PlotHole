package com.server;

import com.common.Data;
import com.common.Pothole;
import com.common.Report;

import com.common.ReportStatus;
import org.locationtech.jts.geom.Coordinate;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class FileManager {

    private final ReentrantLock userLock = new ReentrantLock();
    private final ReentrantLock reportLock = new ReentrantLock();
    private final ReentrantLock potholeLock = new ReentrantLock();

    private TreeSet<Pothole> potholes;
    private TreeMap<String, Integer> users;
    private TreeMap<String, LinkedList<Report>> reports;



    public FileManager(){
        users = new TreeMap<>();
        reports = new TreeMap<>();
        potholes = new TreeSet<>();
    }

    public void saveUsers(String fileName){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            userLock.lock();
            oos.writeObject(users);

        } catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        } catch(IOException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally{
            userLock.unlock();
        }
    }

    public void saveReports(String fileName){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            reportLock.lock();
            oos.writeObject(reports);

        }catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        } catch(IOException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally {
            reportLock.unlock();
        }
    }

    public void savePotholes(String fileName){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))){
            potholeLock.lock();
            oos.writeObject(potholes);

        }catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        } catch(IOException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally {
            potholeLock.unlock();
        }
    }

    public void loadUsers(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Data.USER_FILENAME))){

            userLock.lock();
            users = (TreeMap<String, Integer>) ois.readObject();

        }catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        }catch (IOException | ClassNotFoundException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally{
            userLock.unlock();
        }
    }

    public void loadReports(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Data.REPORT_FILENAME))){

            reportLock.lock();
            reports = (TreeMap<String, LinkedList<Report>>) ois.readObject();

        }catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        }catch (IOException | ClassNotFoundException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally {
        reportLock.unlock();
        }
    }

    public void loadPotholes(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Data.POTHOLE_FILENAME))){

            potholeLock.lock();
            potholes = (TreeSet<Pothole>) ois.readObject();

        }catch(FileNotFoundException ex){
            System.err.println("File not found "+ ex.getMessage());
        }catch (IOException | ClassNotFoundException ex){
            System.err.println("I/O exception "+ ex.getMessage());
        }finally {
        potholeLock.unlock();
        }
    }

    public boolean login(String username, int passwordHash){
        try{
            userLock.lock();
            return users.containsKey(username) && users.get(username).equals(passwordHash);
        }finally{
            userLock.unlock();
        }

    }

    public boolean register(String username, int passwordHash){
        try{
            userLock.lock();
            if(!users.containsKey(username)){
                users.put(username, passwordHash);
                return true;
            }else{
                return false;
            }
        }finally{
            userLock.unlock();
        }

    }

    public ReportStatus makeReport(Coordinate coordinate, int intensity, String username){
        boolean isKnown = false;
        boolean isReported = false;
        Pothole newPothole = new Pothole(coordinate, intensity);
        //check for the coordinate
        try{
            potholeLock.lock();
            for(Pothole pothole : potholes){
                if(pothole.getCoordinate().equals2D(newPothole.getCoordinate(), Data.METER_TO_DEGREE)) {
                    isKnown = true;
                    break;
                }
            }
            if(!isKnown){
                potholes.add(newPothole);
            }
        }finally{
            potholeLock.unlock();
        }
        //anyway add this report for the user

        try{
            Report userReport = new Report(newPothole,username);
            reportLock.lock();
            if(reports.containsKey(username)){
                for(Report report :reports.get(username)){
                    //check if the user has already reported the same pothole
                    if(report.getPothole().getCoordinate().equals2D(coordinate, Data.METER_TO_DEGREE)){
                        isReported = true;
                        break;
                    }
                }
                if(!isReported){
                    reports.get(username).add(userReport);
                }
            }else{
                //it is the first time the user makes a report
                reports.put(username, new LinkedList<>());
                reports.get(username).add(userReport);
            }
        }finally{
            reportLock.unlock();
        }

        if(isReported)
            return ReportStatus.ALREADY_REPORTED_SAME_USER;
        else if(isKnown)
            return ReportStatus.ALREADY_REPORTED_DIFFERENT_USER;
        return ReportStatus.NEW_REPORT;

    }

    public TreeSet<Pothole> getPotholes(){
        try{
            potholeLock.lock();
            return new TreeSet<>(potholes);
        }finally{
            potholeLock.unlock();
        }
    }

    public TreeSet<Pothole> getPotholes(Coordinate center){
        try{
            potholeLock.lock();
            TreeSet<Pothole> nearCenterPotholes = new TreeSet<>();
            for(Pothole pothole : potholes){
                if((pothole.getCoordinate().distance(center)*Data.DEGREE_TO_METER )< Data.DISTANCE_THRESHOLD){
                    nearCenterPotholes.add(pothole);
                }
            }
            return nearCenterPotholes;

        }finally {
            potholeLock.unlock();
        }

    }

    public LinkedList<String> getUsernames(){
        try{
            userLock.lock();
            return new LinkedList<>(users.keySet());
        }finally{
            userLock.unlock();
        }

    }

    public LinkedList<Report> getReportsByUsername(String username){
        try{
            reportLock.lock();
            return new LinkedList<>(reports.getOrDefault(username, new LinkedList<>()));
        }finally{
            reportLock.unlock();
        }

    }


    public boolean fixReportedPothole(Report report){
        Coordinate coordinate = report.getPothole().getCoordinate();
        try{
            potholeLock.lock();
            return potholes.removeIf(pothole -> pothole.getCoordinate().equals2D(coordinate, Data.METER_TO_DEGREE));
        }finally{
            potholeLock.unlock();
        }
    }

    public TreeMap<Integer, LinkedList<String>> getActiveUsers(){
        TreeMap<Integer, LinkedList<String>> activeUsers = new TreeMap<>();
        try{
            userLock.lock();
            reportLock.lock();
            for(String username : users.keySet()){
                LinkedList<Report> userReports = reports.get(username);
                int size = (userReports != null) ? userReports.size() : 0;

                activeUsers.computeIfAbsent(size, k -> new LinkedList<>()).add(username);
            }
            return activeUsers;
        }finally{
            reportLock.unlock();
            userLock.unlock();
        }
    }

    public TreeMap<String, LinkedList<Report>> getAllReports(){
        try{
            reportLock.lock();
            return new TreeMap<>(reports);
        }finally{
            reportLock.unlock();
        }
    }

}
