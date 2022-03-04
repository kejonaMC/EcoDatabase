package dev.projectg.ecodatabase.listeners;

import dev.projectg.database.DatabaseSetup;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.ecodatabase.api.VaultApi;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import dev.projectg.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            if (!(DatabaseSetup.connectionAlive())) {
                DatabaseSetup.connectionReconnect();
            }
            // Check if player has a record in database
            Double checkDatabase = EcoDatabaseSpigot.getPlugin().getEcoDatabase().balance(player.getUniqueId(), "BALANCE");
            if (checkDatabase == null) {
                try {
                    // Adding player in database
                     EcoHandler.balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        // Add player in database
                     EcoDatabaseSpigot.getPlugin().getEcoDatabase().addPlayer(player.getName(), player.getUniqueId(), VaultApi.eco().getBalance(player));
                } catch (Exception e) {
                    Logger.getLogger().severe("Error while adding player in database");
                }
            } else {
                try {
                    // Updating player balance from database to economy
                    VaultApi.eco().databaseSetBalance(player);
                    EcoHandler.balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                } catch (Exception e) {
                    Logger.getLogger().severe("Error while updating player balance from database to economy");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();

            if (!(DatabaseSetup.connectionAlive())) {
                DatabaseSetup.connectionReconnect();
            }
            // need updateBalance logic from hashmap
            EcoDatabaseSpigot.getPlugin().getEcoDatabase().updateBalance(player.getUniqueId(), VaultApi.eco().getBalance(player));
            EcoHandler.balanceHashmap.remove(player.getUniqueId());
        } catch (Exception e) {
            Logger.getLogger().severe("Error while updating player balance from economy to database");
        }
    }
}