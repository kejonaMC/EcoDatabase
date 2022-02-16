package dev.projectg.ecodatabase.handers;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.ecodatabase.api.VaultApiHandler;
import dev.projectg.logger.EcoDatabaseLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EcoHandler {

    public static Map<UUID, Double> balance = new HashMap<>();
    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

    // Update when server is shutting down
    public void updateEcoOnShutdown() {
        if (!onlinePlayers.isEmpty()) {
            for (Player player : onlinePlayers) {
                if (player.isOnline()) {
                    try {
                        EcoDatabase.updateBalance(player.getUniqueId(), VaultApiHandler.eco().getBalance(player));
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.getName() + " balance");
                    }
                }
            }
        }
    }

    // Update hashmap balance each 5min
    public void updateHashmapBalance() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
            if (!onlinePlayers.isEmpty()) {
                for (Player player : onlinePlayers) {
                    try {
                        balance.put(player.getUniqueId(), VaultApiHandler.eco().getBalance(player));
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.getName() + " balance");
                    }
                }
            }
        }, 100L, 1000L * 60 * 5);
    }

    // Update balance at interval to database
    public void queryHashmapBalance(int interval) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
            if (!balance.isEmpty()) {
                for (Map.Entry<UUID, Double> entry : balance.entrySet()) {
                    try {
                        EcoDatabase.updateBalance(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error query eco balance to database");
                    }
                }
            }
        }, 100L, 1000L * 60 * (long) interval);
    }
    public static EcoHandler handler() {
        return new EcoHandler();
    }
}