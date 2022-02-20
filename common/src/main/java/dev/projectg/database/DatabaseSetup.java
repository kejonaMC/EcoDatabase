package dev.projectg.database;

import dev.projectg.configuration.Configurate;
import dev.projectg.logger.EcoDatabaseLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseSetup {

    private static Connection connection;
    public String host;
    public String database;
    public String username;
    public String password;
    public int port;
    public static String economy;

    EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

    public void mysqlSetup(Path dataDirectory, Configurate config) {
        host = config.getHost();
        port = config.getPort();
        database = config.getDatabase();
        username = config.getUsername();
        password = config.getPassword();
        economy = "ecodatabase";


        if (config.getDatabaseType().equalsIgnoreCase("mysql")) {
            logger.info("Connecting to MySQL database...");
            try {
                synchronized (this) {
                    if (getConnection() != null && !getConnection().isClosed()) {
                        logger.error("Connection is already open!");
                    }

                    Class.forName("com.mysql.jdbc.Driver");
                    setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                            + this.port + "/" + this.database, this.username, this.password));
                    createTable();

                    logger.info("MYSQL Connected");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (config.getDatabaseType().equalsIgnoreCase("sqlite")) {
            logger.info("Connecting to SQLite database...");
            try {
                File dataFolder = new File(dataDirectory.toFile(), "PlayerData.db");
                if (!dataFolder.exists()) {
                    try {
                        dataFolder.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Class.forName("org.sqlite.JDBC");
                    setConnection(DriverManager.getConnection("jdbc:sqlite:" + dataFolder));
                    PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + DatabaseSetup.economy + " (Playername varchar(16), UUID char(36), Balance varchar(500))");
                    stmt.execute();
                    logger.info("SQLite Connected");
                } catch (Exception e) {
                    System.out.println("SQLite Error");
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.error("[EcoDatabase] Database type not found we currently support only mysql and sqlite");
        }
    }
    public static void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + DatabaseSetup.economy + "(Playername varchar(16), UUID char(36), Balance varchar(500))");
            statement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connectionAlive() {
        try {
            if (connection == null) {
                logger.warn("Connection to database was lost, Trying to reconnect...");
                connectionReconnect();
            }
            if (connection.isClosed()) {
                logger.warn("Connection to database is closed, Trying to reconnect...");
                connectionReconnect();
            }
        } catch (Exception e) {
            logger.error("Could not reconnect to Database! Error: " + e.getMessage());
        }
    }

    public void connectionReconnect() {
        try {
            long start;
            long end;

            start = System.currentTimeMillis();
            logger.warn("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                    + this.port + "/" + this.database, this.username, this.password);
            end = System.currentTimeMillis();
            logger.info("Connection to MySQL server established in " + ((end - start)) + " ms!");
        } catch (Exception e) {
            logger.error("Error re-connecting to the database! Error: " + e.getMessage());
        }
    }

    public void connectionClose() {
        try {
            logger.warn("Close database connection!");
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setConnection(Connection connection) {
        DatabaseSetup.connection = connection;
    }

    public static Connection getConnection() {
        return connection;
    }
}