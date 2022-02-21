package dev.projectg.database;

import dev.projectg.configuration.Configurate;
import dev.projectg.logger.Logger;

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
                synchronized (this) {
                    if (getConnection() != null && !getConnection().isClosed()) {
                        Logger.getLogger().severe("Connection is already open!");
                    }

                    Class.forName("com.mysql.jdbc.Driver");
                    setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                            + this.port + "/" + this.database, this.username, this.password));
                    createTable();

                    Logger.getLogger().info("MYSQL Connected");
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
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
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger().severe("[EcoDatabase] Database type not found we currently support only mysql and sqlite");
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
                Logger.getLogger().warn("Connection to database was lost, Trying to reconnect...");
                connectionReconnect();
            }
            if (connection.isClosed()) {
                Logger.getLogger().warn("Connection to database is closed, Trying to reconnect...");
                connectionReconnect();
            }
        } catch (Exception e) {
            Logger.getLogger().severe("Could not reconnect to Database! Error: " + e.getMessage());
        }
    }

    public void connectionReconnect() {
        try {
            long start;
            long end;

            start = System.currentTimeMillis();
            Logger.getLogger().warn("Attempting to establish a connection to the MySQL server!");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":"
                    + this.port + "/" + this.database, this.username, this.password);
            end = System.currentTimeMillis();
            Logger.getLogger().info("Connection to MySQL server established in " + ((end - start)) + " ms!");
        } catch (Exception e) {
            Logger.getLogger().severe("Error re-connecting to the database! Error: " + e.getMessage());
        }
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