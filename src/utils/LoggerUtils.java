package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class LoggerUtils {
    private Map<String, Logger> loggers = new HashMap<>();

    public Logger getLogger(String name, String fileName) {
        Logger logger = loggers.get(name);
        if (logger == null) {
            logger = Logger.getLogger(name);
            try {
                // Get the path of the directory where the LoggerUtils class is stored
                String directoryPath = Paths.get(LoggerUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().toString();
                String fullPath = directoryPath + "/" + fileName;

                FileHandler fileHandler = new FileHandler(fullPath, true);
                fileHandler.setFormatter(new CustomFormatter());
                logger.addHandler(fileHandler);

                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new CustomFormatter());
                logger.addHandler(consoleHandler);

                logger.setLevel(Level.ALL);
                loggers.put(name, logger);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }

    public void log(String loggerName, String fileName, Level level, String message) {
        Logger logger = getLogger(loggerName, fileName);
        logger.log(level, message);
    }

    static class CustomFormatter extends Formatter {
        private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(sdf.format(new Date(record.getMillis()))).append(" ");
            sb.append(record.getLevel().getLocalizedName()).append(": ");
            sb.append(formatMessage(record));
            sb.append(System.lineSeparator());
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        // Example usage
        LoggerUtils loggerUtils = new LoggerUtils();
        loggerUtils.log("TestLogger", "test.log", Level.INFO, "This is a test log message.");
    }
}
