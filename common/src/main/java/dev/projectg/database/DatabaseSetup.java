package dev.projectg.database;

import dev.projectg.configuration.Configurate;
import dev.projectg.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

public class DatabaseSetup {

    private static Connection connection = null;
    public String host;
    public String database;
    public String username;
    public String password;
    public int port;
    public static String economy;

    public void mysqlSetup(Path dataDirectory, Configurate config) {
        host = config.getHost();
        port = config.getPort();
        database = config.getDatabase();
        username = config.getUsername();
        password = config.getPassword();
        economy = "ecodatabase";

        if (config.getDatabaseType().equalsIgnoreCase("mysql")) {
            Logger.getLogger().info("Connecting to MySQL database...");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Properties properties = new Properties();
                properties.setProperty("user", username);
                properties.setProperty("password", password);
                properties.setProperty("autoReconnect", "true");
                properties.setProperty("verifyServerCertificate", "false");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
                // Create tables
                createTable();
                    Logger.getLogger().info("MYSQL Connected");
            } catch (SQLException | ClassNotFoundException e) {
                Logger.getLogger().severe("Could not connect to MySQL database!" + e.getMessage());
            }
        } else if (config.getDatabaseType().equalsIgnoreCase("sqlite")) {
            Logger.getLogger().info("Connecting to SQLite database...");
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
                    Logger.getLogger().info("SQLite Connected");
                } catch (Exception e) {
                    System.out.println("SQLite Error");
                    Logger.getLogger().severe("Could not connect to SQLite database!" + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger().severe("Database type not found! We currently support only mysql and sqlite");
        }
    }
    public static void createTable() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + DatabaseSetup.economy + "(Playername varchar(16), UUID char(36), Balance varchar(500))");
            statement.closeOnCompletion();
        } catch (SQLException e) {
            Logger.getLogger().severe("Could not create tables!" + e.getMessage());
        }
    }

    public void connectionAlive() {
        try {
            Logger.getLogger().info("Checking Database connection...");
            if (connection.isClosed()) {
                Logger.getLogger().warn("Connection to database is closed, Trying to reconnect...");
                connection.close();
                connectionReconnect();
            }
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "ecodatabase", null);
            if (!tables.next()) {
                Logger.getLogger().severe("Connection to the database was lost! Trying to reconnect...");
                connection.close();
                connectionReconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectionReconnect() {
        long start;
        long end;
        start = System.currentTimeMillis();
        Logger.getLogger().warn("Attempting to establish a connection to the MySQL server!");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, properties);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.getLogger().severe("Could not connect to MySQL database!" + e.getMessage());
        }
        end = System.currentTimeMillis();
        Logger.getLogger().info("Connection to MySQL server established in " + ((end - start)) + " ms!");
    }

    public void connectionClose() {
        try {
            Logger.getLogger().warn("Closing connection to database!");
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