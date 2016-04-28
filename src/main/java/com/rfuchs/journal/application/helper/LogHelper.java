package com.rfuchs.journal.application.helper;

import java.util.UUID;

/**
 * Created by rfuchs on 14/04/2016.
 */
public class LogHelper {

    public static String methodEntering(String log) {
        return "Entering method - " + log;
    }

    public static String timedLog(String log, long startTime) {
        return String.format("Call took %s seconds - parameters: %s", secondsElapsed(startTime), log);
    }

    private static double secondsElapsed(long start) {
        return (((double) System.currentTimeMillis() - (double) start) / (double) 1000.00);
    }

    public static String error(String message) {
        return String.format("ERROR with UUID = [%s] > %s", UUID.randomUUID().toString(), message);
    }

}
