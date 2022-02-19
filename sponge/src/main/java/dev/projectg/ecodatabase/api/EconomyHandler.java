package dev.projectg.ecodatabase.api;

import dev.projectg.database.EcoDatabase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import java.math.BigDecimal;
import java.util.UUID;

public class EconomyHandler {
    public EconomyService economyService;

    public EconomyHandler() {
        Sponge.serviceProvider().provide(EconomyService.class);
    }

    Currency currency;
    {
        assert economyService != null;
        currency = economyService.defaultCurrency();
    }

    public BigDecimal getBalance(UUID playerUUID) {
        if (!economyService.findOrCreateAccount(playerUUID).isPresent()) {
            return BigDecimal.ZERO;
        }
        return economyService.findOrCreateAccount(playerUUID).get().balance(currency);
    }

    public void setBalance(UUID playerUUID, BigDecimal amount) {
        if (!economyService.findOrCreateAccount(playerUUID).isPresent()) {
            return;
        }
        economyService.findOrCreateAccount(playerUUID).get().setBalance(currency,amount);
    }

    public static void databaseSetBalance(UUID playerUUID, BigDecimal amount) {

        eco().setBalance(playerUUID, amount);
        amount = BigDecimal.valueOf(EcoDatabase.balance(playerUUID, "BALANCE"));
        eco().setBalance(playerUUID, amount);
    }

    public static EconomyHandler eco() {
        return new EconomyHandler();
    }

}
