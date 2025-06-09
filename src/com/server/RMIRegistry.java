package com.server;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIRegistry{
    public static void main(String[] args) {
        try{
            Registry reg = LocateRegistry.createRegistry(1099);
            System.out.println("Registry created");
            Thread.sleep(Long.MAX_VALUE);
        }catch(Exception ignored){}
    }

}
