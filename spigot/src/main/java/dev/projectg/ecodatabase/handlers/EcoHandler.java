package dev.projectg.ecodatabase.handlers;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.ecodatabase.api.VaultApi;
import dev.projectg.logger.EcoDatabaseLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EcoHandler {

    public static Map<UUID, Double> balanceHashmap = new HashMap<>();
    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

    // Update when server is shutting down
    public void updateEcoOnShutdown() {
        try {
            if (!onlinePlayers.isEmpty()) {
                for (Player player : onlinePlayers) {
                    if (player.isOnline()) {
                        try {
                            EcoDatabase.updateBalance(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        } catch (Exception e) {
                            EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.getName() + " balance");
                        }
                    }
                }
            }
        } catch (Exception e) {
            EcoDatabaseLogger.getLogger().error("Error while sending data into database on shutdown");
        }
    }

    // Update hashmap balance each 5min
    public void updateHashmapBalance() {
        try {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
                if (!onlinePlayers.isEmpty()) {
                    for (Player player : onlinePlayers) {
                        try {
                            balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        } catch (Exception e) {
                            EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.getName() + " balance");
                        }
                    }
                }
            }, 100L, 1000L * 60 * 5);
        } catch (Exception e) {
            EcoDatabaseLogger.getLogger().error("Error while updating hashmap balance");
        }
    }

    // Update balance at interval to database
    public void queryHashmapBalance(int interval) {
        try {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
                if (!balanceHashmap.isEmpty()) {
                    for (Map.Entry<UUID, Double> entry : balanceHashmap.entrySet()) {
                        try {
                            EcoDatabase.updateBalance(entry.getKey(), entry.getValue());
                        } catch (Exception e) {
                            EcoDatabaseLogger.getLogger().error("Error query eco balance to database");
                        }
                    }
                }
            }, 100L, 1000L * 60 * (long) interval);
        } catch (Exception e) {
            EcoDatabaseLogger.getLogger().error("Error while querying hashmap balance");
        }
    }
    public static EcoHandler handler() {
        return new EcoHandler();
    }
}