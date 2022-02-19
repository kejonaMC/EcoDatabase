package dev.projectg.ecodatabase.handlers;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.api.EconomyHandler;
import dev.projectg.logger.EcoDatabaseLogger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EcoHandler {

    public static Map<UUID, Double> balanceHashmap = new HashMap<>();
    List<Player> onlinePlayers = new ArrayList<>(Sponge.server().onlinePlayers());
    Task.Builder taskBuilder = Task.builder();

    // Update when server is shutting down
    public void updateEcoOnShutdown() {
        if (!onlinePlayers.isEmpty()) {
            for (Player player : onlinePlayers) {
                if (player.isLoaded()) {
                    try {
                        EcoDatabase.updateBalance(player.uniqueId(), EconomyHandler.eco().getBalance(player.uniqueId()).doubleValue());
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.name() + " balance");
                    }
                }
            }
        }
    }

    // Update hashmap balance each 5min
    public void updateHashmapBalance() {
        taskBuilder.interval(5, TimeUnit.MINUTES).execute(() -> {

            if (!onlinePlayers.isEmpty()) {
                for (Player player : onlinePlayers) {
                    try {
                        balanceHashmap.put(player.uniqueId(), EconomyHandler.eco().getBalance(player.uniqueId()).doubleValue());
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error while updating player: " + player.name() + " balance");

                    }
                }
            }
        });
    }

    // Update balance at interval to database
    public void queryHashmapBalance(int interval) {
        taskBuilder.interval(interval, TimeUnit.MINUTES).execute(() -> {
            if (!balanceHashmap.isEmpty()) {
                for (Map.Entry<UUID, Double> entry : balanceHashmap.entrySet()) {
                    try {
                        EcoDatabase.updateBalance(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        EcoDatabaseLogger.getLogger().error("Error query eco balance to database");
                    }
                }
            }
        });
    }
    public static EcoHandler handler() {
        return new EcoHandler();
    }
}