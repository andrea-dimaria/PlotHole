package com.server;

import com.common.*;
import org.locationtech.jts.geom.Coordinate;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RemoteServer extends UnicastRemoteObject implements RemoteServices, AdminRemoteServices{

    private final FileManager fileManager;

    protected RemoteServer() throws RemoteException {
        super();
        fileManager = new FileManager();
    }

    @Override
    public boolean login(String username, int passwordHash) throws RemoteException {
        boolean result = fileManager.login(username, passwordHash);
        String status = result ? "logged in" : "NOT logged in";
        MyLogger.log("User "+ username + " " +status);
        return result;

    }

    @Override
    public boolean register(String username, int passwordHash) throws RemoteException {
        return fileManager.register(username, passwordHash);
    }

    @Override
    public ReportStatus makeReport(Coordinate coordinate, int intensity, String username) throws RemoteException {
        MyLogger.log("User "+ username +" making a report");
        return fileManager.makeReport(coordinate, intensity, username);
    }

    @Override
    public LinkedList<Report> getReports(String username) throws RemoteException {
        return fileManager.getReportsByUsername(username);
    }


    private static RemoteServices connect(){
        try {
            RemoteServer server = new RemoteServer();
            Naming.rebind("rmi://localhost:1099/RemoteServices", server);
            Naming.rebind("rmi://localhost:1099/AdminRemoteServices", server);
            return server;
        }catch(RemoteException | MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
    }



    public static void printMenu(){
        System.out.println("1. Show Potholes");
        System.out.println("2. Show Users");
        System.out.println("3. Make an internal report");
        System.out.println("6. Show Reports");
        System.out.println("7. Register user");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    static Scanner scanner = new Scanner(System.in);
    static RemoteServices server;

    public static void main(String[] args) {

        server = connect();
        System.out.println("Welcome to serverApp");

        while(true){
            printMenu();
            int command = Integer.parseInt(scanner.nextLine());
            Coordinate coord;

            switch(command){
                case 0:
                    System.out.println("Shutting down server...");
                    scanner.close();
                    return;
                case 1:
                    try{
                        TreeSet<Pothole> allPotholes = ((AdminRemoteServices) server).getPotholes();
                        if(allPotholes.isEmpty()){
                            System.out.println("There are no potholes");
                            break;
                        }else{
                            System.out.println("There are " + allPotholes.size() + " potholes");
                            for(Pothole pothole : allPotholes){
                                System.out.println(pothole);
                            }
                            System.out.println("End of report\n");
                        }
                    }catch(RemoteException ex){
                        System.err.println("Something went wrong with the server...");
                        ex.printStackTrace();
                    }
                    break;
                case 2:

                    try {
                        LinkedList<String> usernames = ((AdminRemoteServices)server).getUsers();
                        System.out.println("All users in the system: ");
                        for(String username : usernames){
                            System.out.println(username);
                        }
                        System.out.println("End of report\n");
                    } catch (RemoteException e) {
                        System.err.println("Something went wrong with the server...");
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    System.out.println("Making an internal report.");
                    coord = getCoordinateByUser();
                    System.out.print("Pothole's intensity: ");
                    int intensity = Integer.parseInt(scanner.nextLine());
                    try {
                        ReportStatus reportStatus = server.makeReport(coord, intensity, Data.INTERNAL_USER);
                        switch(reportStatus){
                            case ReportStatus.NEW_REPORT -> System.out.println("Pothole reported successfully");
                            case ReportStatus.ALREADY_REPORTED_SAME_USER -> System.out.println("You already reported this pothole");
                            case ReportStatus.ALREADY_REPORTED_DIFFERENT_USER -> System.out.println("Pothole already known");
                        }

                    }catch(RemoteException ex){
                        System.err.println("Something went wrong with the server...");
                        ex.printStackTrace();
                    }

                    break;

                case 6:
                    System.out.print("Username to find: ");
                    String username = scanner.nextLine();
                    try {
                        LinkedList<Report> reportsOfUser = ((AdminRemoteServices) server).getReportsByUsername(username);
                        if(reportsOfUser.isEmpty()){
                            System.out.println("User "+username+" did not make any report");
                            break;
                        }
                        System.out.println("Reports made by "+username);
                        for(Report report : reportsOfUser){
                            System.out.println(report);
                        }
                        System.out.println("End of report\n");
                    }catch (RemoteException ex){
                        System.err.println("Something went wrong with the server...");
                        ex.printStackTrace();
                    }

                    break;
                case 7:
                    System.out.print("Enter username: ");
                    username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    int passwordHash = scanner.nextLine().hashCode();

                    try{
                        System.out.println("Registered: "+((AdminRemoteServices)server).registerNewUser(username,passwordHash));
                    }catch(RemoteException ex){
                        System.err.println("Something went wrong with the server...");
                        ex.printStackTrace();
                    }

                    break;
                default:
                    System.out.println("Invalid command");
                    break;
            }
        }

    }
    public static Coordinate getCoordinateByUser(){
        System.out.print("Enter coordinate (x,y): ");
        String input = scanner.nextLine();
        String[] parts = input.split(",\\s*");
        try{
            return new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        }catch(NumberFormatException ex){
            return null;
        }
    }

    @Override
    public LinkedList<String> getUsers() throws RemoteException {
        return fileManager.getUsernames();
    }

    @Override
    public TreeSet<Pothole> getPotholes() throws RemoteException {
        return fileManager.getPotholes();
    }

    @Override
    public void saveData(String fileToSave) throws RemoteException{
        switch(fileToSave.toLowerCase()){
            case "report", "reports" ->fileManager.saveReports(Data.REPORT_FILENAME);
            case "user", "users"->fileManager.saveUsers(Data.USER_FILENAME);
            case "pothole", "potholes"->fileManager.savePotholes(Data.POTHOLE_FILENAME);
        }

    }

    @Override
    public void loadData(String fileToLoad) throws RemoteException{

        switch(fileToLoad.toLowerCase()){
            case "report", "reports" -> fileManager.loadReports();
            case "user", "users" -> fileManager.loadUsers();
            case "pothole", "potholes" -> fileManager.loadPotholes();
        }
        MyLogger.warn("Loading :" +fileToLoad);
    }

    @Override
    public LinkedList<Report> getReportsByUsername(String username) throws RemoteException{
        return fileManager.getReportsByUsername(username);
    }

    @Override
    public boolean registerNewUser(String username, int passwordHash) throws RemoteException{
        return fileManager.register(username, passwordHash);
    }

    @Override
    public boolean fixReportedPothole(Report report) throws RemoteException{
        boolean success =  fileManager.fixReportedPothole(report);
        String status = success ? "fixed" : "NOT fixed";
        MyLogger.warn("Pothole " + report.getPothole() + " " + status);
        return success;
    }

    @Override
    public TreeMap<Integer, LinkedList<String>> getActiveUsers() throws RemoteException{
        return fileManager.getActiveUsers();
    }

    @Override
    public TreeMap<String, LinkedList<Report>> getAllReports() throws RemoteException {
        return fileManager.getAllReports();
    }

}
