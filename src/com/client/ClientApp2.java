//package com.client;
//
//import com.common.Report;
//import com.common.ReportStatus;
//import com.server.RemoteServices;
//import org.locationtech.jts.geom.Coordinate;
//
//import java.net.MalformedURLException;
//import java.rmi.Naming;
//import java.rmi.NotBoundException;
//import java.rmi.RemoteException;
//import java.util.LinkedList;
//import java.util.Scanner;
//
//public class ClientApp2 {
//    private static RemoteServices server;
//    private static String username = "";
//    private static Scanner scanner = new Scanner(System.in);
//    private static boolean loggedIn = false;
//
//
//    public static void main(String[] args) {
//        server = connect();
//        if(server == null){
//            System.out.println("Could not connect to server");
//            return;
//        }
//
//        while(!loggedIn){
//            boolean success = loginProcedure();
//            if(success){
//                loggedIn = true;
//            }else{
//                System.out.println("Login failed: username does not exist or password is incorrect");
//            }
//        }
//
//        System.out.println("Welcome "+username+"!");
//        while(loggedIn){
//            printMenu();
//            int command = Integer.parseInt(scanner.nextLine());
//            switch(command){
//                case 1:
//                    reportProcedure();
//                    break;
//                case 2:
//                    showProcedure();
//                    break;
//                case 3:
//                    logoutProcedure();
//                    break;
//                default:
//                    System.out.println("Invalid command");
//                    break;
//            }
//        }
//        scanner.close();
//
//    }
//
//
//    public static RemoteServices connect() {
//        try{
//            RemoteServices services = (RemoteServices) Naming.lookup("rmi://localhost:1099/RemoteServices");
//            System.out.println("Remote services are ready");
//            return services;
//        }catch(RemoteException | NotBoundException | MalformedURLException ex){
//            System.err.println("Something went wrong with the server...");
//            return null;
//        }
//    }
//
//    public static boolean loginProcedure(){
//        System.out.print("Enter username: ");
//        username = scanner.nextLine();
//        System.out.print("Enter password: ");
//        int password = scanner.nextLine().hashCode();
//        try {
//            return server.login(username, password);
//        } catch (RemoteException e) {
//            System.err.println("Something went wrong with the server...");
//            return false;
//        }
//    }
//    public static void printMenu(){
//        System.out.println("Menu");
//        System.out.println("1. Make a report");
//        System.out.println("2. Show your reports");
//        System.out.println("3. Logout");
//        System.out.print("Make a choice: ");
//    }
//
//    private static void reportProcedure() {
//        Coordinate coordinate = null;
//        int intensity = 0;
//
//        System.out.println("Make a report");
//        while(coordinate ==null){
//            System.out.print("Enter coordinate (x,y): ");
//            String input = scanner.nextLine();
//            String[] parts = input.split(",\\s*");
//            try{
//                coordinate = new Coordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
//            }catch (NumberFormatException ex){
//                System.err.println("Invalid coordinate");
//            }
//        }
//        while(intensity ==0){
//            System.out.print("Pothole intensity: ");
//
//            try {
//                intensity = Integer.parseInt(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.err.println("Invalid value");
//
//            }
//        }
//
//        try {
//            ReportStatus success = server.makeReport(coordinate,intensity,username);
//            if(success == ReportStatus.NEW_REPORT){
//                System.out.println("New pothole, thanks!");
//            }else if( success == ReportStatus.ALREADY_REPORTED_DIFFERENT_USER){
//                System.out.println("Pothole already known, thanks!");
//            }else{
//                System.out.println("You already reported this pothole");
//            }
//        } catch (RemoteException e) {
//            System.err.println("Something went wrong with the server...");
//        }
//
//    }
//
//    private static void showProcedure() {
//        System.out.println("Here your reports");
//        try{
//        LinkedList<Report> reports = server.getReports(username);
//        if(!reports.isEmpty()){
//            for(Report report : reports){
//                System.out.println(report);
//            }
//            System.out.println("End of reports\n");
//        }else{
//            System.out.println("No reports found");
//        }
//        }catch (RemoteException e){
//            System.err.println("Something went wrong with the server...");
//        }
//    }
//
//    private static void logoutProcedure() {
//        loggedIn = false;
//        System.out.println("Logging out\tBye");
//    }
//
//
//}
