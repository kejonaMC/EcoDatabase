package dev.projectg.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EcoDatabase {

    public static void addPlayer(String playerName, UUID playerUUID, double balance) {

        try {
            String sql = "(PLAYERNAME,UUID,BALANCE) VALUES (?,?,?)";
            PreparedStatement insert = DatabaseSetup.getConnection().prepareStatement("INSERT INTO " + DatabaseSetup.economy
                    + sql);
            insert.setString(1, playerName);
            insert.setString(2, playerUUID.toString());
            insert.setDouble(3, balance);
            insert.executeUpdate();

        } catch (SQLException exe) {
            exe.printStackTrace();
        }
    }

    public static Double balance(UUID uuid, String column) {
        try {
            // Balance column
            PreparedStatement statement = DatabaseSetup.getConnection()
                    .prepareStatement("SELECT * FROM " + DatabaseSetup.economy + " WHERE UUID=?");

            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getDouble(column);

        } catch (SQLException ignored) {

        }
        return null;

    }
    public static void updateBalance(UUID playerUUID, Double balance) {
        try {
            String sql = " SET BALANCE ='" + balance + "' WHERE UUID='" + playerUUID + "'";
            PreparedStatement insert = DatabaseSetup.getConnection().prepareStatement("UPDATE " + DatabaseSetup.economy
                    + sql);
            insert.executeUpdate();
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
    }
}