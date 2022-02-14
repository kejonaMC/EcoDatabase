package dev.projectg.ecodatabase;

import dev.projectg.configuration.Configurate;
import dev.projectg.database.DatabaseSetup;
import dev.projectg.ecodatabase.listeners.PlayerEvents;
import dev.projectg.ecodatabase.tasks.PlayerEcoTask;
import dev.projectg.logger.EcoDatabaseLogger;
import dev.projectg.logger.JavaUtilLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class EcoDatabaseSpigot extends JavaPlugin {

    private static Economy econ = null;
    public static EcoDatabaseSpigot plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Logger
        new JavaUtilLogger(getLogger());
        EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.error("Vault not found! Disabling EcoDatabase!");
            onDisable();
        }

        // Config setup
        Path path = this.getDataFolder().toPath();
        Configurate config = Configurate.create(path);

        // Database setup
        logger.info("Selected " + config.getDatabaseType() + " database!");
        new DatabaseSetup().mysqlSetup(path, config);

        // Sync player eco at interval
        if (config.getEnableSync()) {
            new PlayerEcoTask().syncDB(econ, config.getSyncInterval());
            logger.info("Sync enabled!");
        }
        logger.info("EcoDatabase has been enabled!");
    }

    @Override
    public void onDisable() {
    }

    public static Economy getEconomy() {
        return econ;
    }
}
