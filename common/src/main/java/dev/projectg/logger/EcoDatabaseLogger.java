package dev.projectg.logger;

public interface EcoDatabaseLogger {

    static void setLogger(EcoDatabaseLogger updaterLogger) {
        LoggerHolder.UPDATER_LOGGER = updaterLogger;
    }
    static EcoDatabaseLogger getLogger() {
        return LoggerHolder.UPDATER_LOGGER;
    }

    /**
     * Logs an error message to the console.
     *
     * @param message the message to log to the console
     */
    void error(String message);

    /**
     * Logs a warning message to the console.
     *
     * @param message the message to log to the console
     */
    void warn(String message);

    /**
     * Logs an info message to the console.
     *
     * @param message the message to log to the console
     */
    void info(String message);

    /**
     * Logs a debug message to the console.
     *
     * @param message the message to log to the console
     */
    void debug(String message);

    /**
     * Logs a trace message to the console.
     *
     * @param message the message to log to the console
     */
    void trace(String message);

    /**
     * Enables debug mode for the GeyserUpdater logger.
     */
    void enableDebug();

    /**
     * Disables debug mode for the GeyserUpdater logger. Debug messages can still be sent after running
     * this method, but they will be hidden from the console.
     */
    void disableDebug();

    /**
     * Returns if debugging is enabled
     */
    boolean isDebug();
}