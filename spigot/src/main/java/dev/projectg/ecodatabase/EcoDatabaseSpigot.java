package dev.projectg.ecodatabase;

import dev.projectg.configuration.Configurate;
import dev.projectg.database.DatabaseSetup;
import dev.projectg.ecodatabase.api.VaultApiHandler;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import dev.projectg.ecodatabase.listeners.PlayerEvents;
import dev.projectg.logger.EcoDatabaseLogger;
import dev.projectg.logger.JavaUtilLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class EcoDatabaseSpigot extends JavaPlugin {
    public static EcoDatabaseSpigot plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Logger
        new JavaUtilLogger(getLogger());
        EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

        // Enable vault
        new VaultApiHandler();

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        // Config setup
        Path path = this.getDataFolder().toPath();
        Configurate config = Configurate.create(path);

        // Sync Economy
        EcoHandler.handler().updateHashmapBalance();
        if (config.getEnableSync()) {
            logger.info("Sync economy enabled");
            EcoHandler.handler().queryHashmapBalance(config.getSyncInterval());
        }

        // Database setup
        logger.info("Selected " + config.getDatabaseType() + " database!");
        new DatabaseSetup().mysqlSetup(path, config);
        // Check if connection is mysql and alive -> reconnect
        if (config.getDatabaseType().equals("mysql")) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                new DatabaseSetup().connectionAlive();
            }, 100L, 1000L * 60 * 30);
        }

        // End
        logger.info("EcoDatabase has been enabled!");
    }

    @Override
    public void onDisable() {
        EcoHandler.handler().updateEcoOnShutdown();
        new DatabaseSetup().connectionClose();
    }

    public static EcoDatabaseSpigot getPlugin() {
        return plugin;
    }
}
