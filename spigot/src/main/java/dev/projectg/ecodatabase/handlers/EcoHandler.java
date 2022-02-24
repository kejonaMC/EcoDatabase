package dev.projectg.ecodatabase.handlers;

import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.ecodatabase.api.VaultApi;
import dev.projectg.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EcoHandler {

    public static Map<UUID, Double> balanceHashmap = new HashMap<>();
    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
    EcoDatabaseSpigot instance = EcoDatabaseSpigot.getPlugin();

    // Update when server is shutting down
    public void updateEcoOnShutdown() {
        try {
            if (!onlinePlayers.isEmpty()) {
                for (Player player : onlinePlayers) {
                    if (player.isOnline()) {
                        try {
                            instance.getEcoDatabase().updateBalance(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        } catch (Exception e) {
                            Logger.getLogger().severe("Error while updating player: " + player.getName() + " balance");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger().severe("Error while sending data into database on shutdown");
        }
    }

    // Update hashmap balance each 5min
    public void updateHashmapBalance() {
        try {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
                if (!onlinePlayers.isEmpty()) {
                    for (Player player : onlinePlayers) {
                        try {
                            balanceHashmap.put(player.getUniqueId(), VaultApi.eco().getBalance(player));
                        } catch (Exception e) {
                            Logger.getLogger().severe("Error while updating player: " + player.getName() + " balance");
                        }
                    }
                }
            }, 20L * 60L * 5L, 20L * 60 * 5);
        } catch (Exception e) {
            Logger.getLogger().severe("Error while updating hashmap balance");
        }
    }

    // Update balance at interval to database
    public void queryHashmapBalance() {
        try {
            if (!balanceHashmap.isEmpty()) {
                Logger.getLogger().warn("Updating player's balance in database do not shutdown the server!");
                for (Map.Entry<UUID, Double> entry : balanceHashmap.entrySet()) {
                    try {
                        instance.getEcoDatabase().updateBalance(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        Logger.getLogger().severe("Error query eco balance to database");
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger().severe("Error while querying hashmap balance");
        }
    }

    public static EcoHandler handler() { return new EcoHandler();}
}