package com.server;

import com.common.Data;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private static final Logger logger = Logger.getLogger(Data.LOGGER_NAME);

    static {
        try{
            FileHandler fh = new FileHandler(Data.LOG_FILENAME,true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);
        }catch(IOException e){
            System.err.println("Logger initialization failed: "+e.getMessage());
        }
    }
    public static void log(String msg){
        logger.info(msg);
    }

    public static void warn(String msg){
        logger.warning(msg);
    }
    public static void error(String msg){
        logger.severe(msg);
    }
}
