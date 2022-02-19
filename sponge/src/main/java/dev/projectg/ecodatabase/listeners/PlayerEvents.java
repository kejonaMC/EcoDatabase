package dev.projectg.ecodatabase.listeners;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.api.EconomyHandler;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.math.BigDecimal;
import java.util.UUID;

public class PlayerEvents {

    public void onPlayerJoin(ServerSideConnectionEvent.Handshake event) {
        try {
            UUID playerUUID = event.connection().profile().uuid();
            // Check if player has a record in database
            Double checkDatabase = EcoDatabase.balance(playerUUID, "UUID");
            if (checkDatabase == null) {
                // Adding player in database
                EcoDatabase.addPlayer(event.profile().name().toString(), playerUUID, EconomyHandler.eco().getBalance(playerUUID).doubleValue());
            } else {
                EconomyHandler.databaseSetBalance(playerUUID, BigDecimal.valueOf(EconomyHandler.eco().getBalance(playerUUID).doubleValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPlayerLeave(ServerSideConnectionEvent.Disconnect event) {
        try {
            UUID playerUUID = event.connection().profile().uuid();

            // need updateBalance logic from hashmap
            EcoDatabase.updateBalance(playerUUID, EconomyHandler.eco().getBalance(playerUUID).doubleValue());
            EcoHandler.balanceHashmap.remove(playerUUID);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}