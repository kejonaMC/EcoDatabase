package dev.projectg.ecodatabase.api;

import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.logger.EcoDatabaseLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultApiHandler {

    EcoDatabaseSpigot plugin = EcoDatabaseSpigot.getPlugin();
    public Economy economy;
    public EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

    public VaultApiHandler() {
        if(!initVault()) {
            logger.error("Vault not found! Disabling EcoDatabase!");
            plugin.onDisable();
        }
    }

    private boolean initVault() {

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }

    public double getBalance(Player player) {return this.economy.getBalance(player);}

    public void withdrawBalance(Player player, double amount) {
        this.economy.withdrawPlayer(player, amount);
    }

    public void depositBalance(Player player, double amount) {
        this.economy.depositPlayer(player, amount);
    }

    public void databaseSetBalance(Player player, double amount) {

        this.withdrawBalance(player, amount);
        amount = EcoDatabase.balance(player.getUniqueId(), "BALANCE");
        this.depositBalance(player, amount);
    }

    public static VaultApiHandler eco() {
        return new VaultApiHandler();
    }
    public Economy getEconomy() {return economy;}

}
