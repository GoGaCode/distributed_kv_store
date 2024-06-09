package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerUtils {

    private static final String LOG_DIR = "logs";
    private static final String SERVER_LOG_FILE = LOG_DIR + "/server.log";
    private static final String CLIENT_LOG_FILE = LOG_DIR + "/client.log";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        // Create the logs directory if it does not exist
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    // Method to log server interactions
    public static void logServer(String message) {
        log(message, SERVER_LOG_FILE);
    }

    // Method to log client interactions
    public static void logClient(String message) {
        log(message, CLIENT_LOG_FILE);
    }

    // General log method
    private static synchronized void log(String message, String logFile) {
        String timeStamp = dateFormat.format(new Date());
        String logMessage = timeStamp + " - " + message;

        // Print to console
        System.out.println(logMessage);

        // Write to log file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
