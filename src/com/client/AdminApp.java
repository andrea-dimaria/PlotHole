package com.client;

import com.common.Data;
import com.common.Pothole;
import com.common.Report;
import com.server.AdminRemoteServices;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class AdminApp {
    private static AdminRemoteServices server;
    private static final Scanner scanner = new Scanner(System.in);

    private static AdminRemoteServices connect(){
        try{
            AdminRemoteServices serv = (AdminRemoteServices) Naming.lookup("rmi://localhost:1099/AdminRemoteServices");
            System.out.println("Admin remote services connected");
            return serv;
        }catch(RemoteException | NotBoundException | MalformedURLException ex){
            System.err.println("Something went wrong with the server");
            return null;
        }
    }

    private static void printMenu(){
        System.out.println("1. View all users");
        System.out.println("2. View reports by user");
        System.out.println("3. View most active users");
        System.out.println("4. Fix a reported pothole");
        System.out.println("5. Show all potholes");
        System.out.println("6. Save");
        System.out.println("7. Load");
        System.out.println("0. Exit");
        System.out.print("Make a choice: ");
    }

    private static void showUserProcedure(){
        try {
            System.out.printf("Users registered in the system %d\n", server.getUsers().size());
            for(String user : server.getUsers()){
                System.out.println(user);
            }
            System.out.println("End of report\n");
        }catch(RemoteException ex){
            System.err.println("Something went wrong with the server");
        }
    }

    private static void showReportProcedure(){
        System.out.print("Username to look up("+Data.INTERNAL_USER+"for all): ");
        String username = scanner.nextLine();
        if(username.equalsIgnoreCase(Data.INTERNAL_USER)){
            try{
                TreeMap<String, LinkedList<Report>> allReports = server.getAllReports();
                if(allReports.isEmpty()){
                    System.out.println("No reports found");
                    return;
                }else{
                    for(String user : allReports.keySet()){
                        System.out.println(user+":");
                        for(Report report : allReports.get(user)){
                            System.out.println(report);
                        }
                    }
                    System.out.println("End of report\n");
                    return;
                }
            }catch(RemoteException ex){
                System.err.println("Something went wrong with the server");
            }
        }
        try {
            LinkedList<Report> reportsByUser = server.getReportsByUsername(username);
            if(reportsByUser.isEmpty()){
                System.out.println("User made no reports");
                return;
            }
            System.out.println("Reports made by "+username);
            for(Report report : reportsByUser){
                System.out.println(report);
            }
            System.out.println("End of report\n");

        }catch(RemoteException ex){
            System.err.println("Something went wrong with the server");
        }
    }

    private static void showActiveUsersProcedure(){
        System.out.println("Looking for the most active users");

        try {
            TreeMap<Integer, LinkedList<String>> activeUsers = server.getActiveUsers();
            if(activeUsers.isEmpty()) {
                System.out.println("Users made no reports");
                return;
            }
            System.out.println("Most active users are: ");
            for(Integer sizeValue : activeUsers.keySet()){
                System.out.println("Reports made: "+sizeValue);
                activeUsers.get(sizeValue).forEach(System.out::println);
            }
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the server");
        }
    }

    private static void fixReportedPotholeProcedure(){
        System.out.println("Fix a reported pothole");
        System.out.print("Username that made the report: ");
        String user  = scanner.nextLine();
        try {
            LinkedList<Report> reportsByUser = server.getReportsByUsername(user);
            if(reportsByUser.isEmpty()){
                System.out.println("User made no reports");
                return;
            }
            System.out.println("Reports made by "+user);
            for(Report report : reportsByUser){
                System.out.printf("%d: %s\n",reportsByUser.indexOf(report),report);
            }
            System.out.print("Choose the report you want to fix: ");
            int reportNumber = Integer.parseInt(scanner.nextLine());
            Report reportChosen = reportsByUser.get(reportNumber);
            System.out.println("Fixed: "+server.fixReportedPothole(reportChosen));
        }catch (RemoteException ex){
            System.err.println("Something went wrong with the server");
        }
    }

    private static void showAllPotholesProcedure(){
        System.out.println("Show all potholes");
        try{
            TreeSet<Pothole> allPotholes = server.getPotholes();
            if(allPotholes.isEmpty()) {
                System.out.println("No potholes reported");
                return;
            }
            System.out.println("There are "+allPotholes.size()+" potholes");
            for(Pothole pothole : allPotholes){
                System.out.println(pothole);
            }
            System.out.println("End of report\n");
        }catch(RemoteException ex){
            System.err.println("Something went wrong with the server");
        }
    }

    private static void loadFileProcedure(){
        System.out.print("What to load: ");
        String saving = scanner.nextLine();
        try {
            server.loadData(saving);
            System.out.println("Data successfully loaded");
        }catch(RemoteException ex){
            System.err.println("Something went wrong with the server...");
            ex.printStackTrace();
        }
    }

    public static void saveFileProcedure(){
        System.out.print("What to save: ");
        String saving = scanner.nextLine();
        try {
             server.saveData(saving);
            System.out.println("Data saved successfully");
        }catch(RemoteException ex){
            System.err.println("Something went wrong with the server...");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        server = connect();
        if(server == null){
            System.err.println("Server could not be found");
            return;
        }

        while(true){
            printMenu();
            int choice = Integer.parseInt(scanner.nextLine());
            switch(choice){
                case 0 ->{
                            System.out.println("Goodbye "+Data.INTERNAL_USER);
                            scanner.close();
                            return;
                }
                case 1 ->showUserProcedure();
                case 2 ->showReportProcedure();
                case 3 ->showActiveUsersProcedure();
                case 4 ->fixReportedPotholeProcedure();
                case 5 ->showAllPotholesProcedure();
                case 6 ->saveFileProcedure();
                case 7 ->loadFileProcedure();
                default -> {
                    System.out.println("Invalid choice");
                }
            }
        }
    }
}
