package dev.projectg.ecodatabase.api;

import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.logger.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultApi {

    public Economy economy;

    public VaultApi() {

        if (!initVault()) {
            Logger.getLogger().severe("Vault not found! Disabling EcoDatabase!");
            EcoDatabaseSpigot.getPlugin().onDisable();
        }
    }

    private boolean initVault() {

        if (EcoDatabaseSpigot.getPlugin().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = EcoDatabaseSpigot.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        economy = rsp.getProvider();

        return economy != null;
    }

    public double getBalance(Player player) {
        return this.economy.getBalance(player);
    }

    public void withdrawBalance(Player player, double amount) {
        this.economy.withdrawPlayer(player, amount);
    }

    public void depositBalance(Player player, double amount) {
        this.economy.depositPlayer(player, amount);
    }

    public void databaseSetBalance(Player player) {
        // Remove players balance
        double balance = this.economy.getBalance(player);
        this.withdrawBalance(player, balance);

        // Add new balance from database into vault
        double newAmount = EcoDatabaseSpigot.getPlugin().getEcoDatabase().balance(player.getUniqueId(), "BALANCE");
        this.depositBalance(player, newAmount);
    }

    public static VaultApi eco() {
        return new VaultApi();
    }

    public Economy getEconomy() {
        return economy;
    }
}
