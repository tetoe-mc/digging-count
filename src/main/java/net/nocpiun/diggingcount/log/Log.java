package net.nocpiun.diggingcount.log;

import org.slf4j.Logger;

public class Log {
    private static Logger logger;

    public static void setLogger(Logger logger) {
        Log.logger = logger;
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }
}
