package dev.projectg.ecodatabase.listeners;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.api.VaultApi;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import dev.projectg.logger.EcoDatabaseLogger;
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
            // Check if player has a record in database
            Double checkDatabase = EcoDatabase.balance(player.getUniqueId(), "BALANCE");
            if (checkDatabase == null) {
                try {
                    // Adding player in database
                        EcoHandler.balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        // Add player in database
                        EcoDatabase.addPlayer(player.getName(), player.getUniqueId(), VaultApi.eco().getBalance(player));
                } catch (Exception e) {
                    EcoDatabaseLogger.getLogger().error("Error while adding player in database");
                }
            } else {
                try {
                    // Updating player balance from database to economy
                    VaultApi.eco().databaseSetBalance(player);
                    EcoHandler.balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                } catch (Exception e) {
                    EcoDatabaseLogger.getLogger().error("Error while updating player balance from database to economy");
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
            // need updateBalance logic from hashmap
            EcoDatabase.updateBalance(player.getUniqueId(), VaultApi.eco().getBalance(player));
            EcoHandler.balanceHashmap.remove(player.getUniqueId());
        } catch (Exception e) {
            EcoDatabaseLogger.getLogger().error("Error while updating player balance from economy to database");
        }
    }
}