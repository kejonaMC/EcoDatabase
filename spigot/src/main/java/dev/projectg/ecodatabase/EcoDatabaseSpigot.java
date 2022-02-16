package dev.projectg.ecodatabase;

import dev.projectg.configuration.Configurate;
import dev.projectg.database.DatabaseSetup;
import dev.projectg.ecodatabase.api.VaultApiHandler;
import dev.projectg.ecodatabase.handers.EcoHandler;
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

        // Enable vault
        new VaultApiHandler();

        // Logger
        new JavaUtilLogger(getLogger());
        EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        // Config setup
        Path path = this.getDataFolder().toPath();
        Configurate config = Configurate.create(path);

        if (!config.getEnableSync()) {
            new EcoHandler().updateHashmapBalance(config.getSyncInterval());
        }

        // Database setup
        logger.info("Selected " + config.getDatabaseType() + " database!");
        new DatabaseSetup().mysqlSetup(path, config);

        // End
        logger.info("EcoDatabase has been enabled!");
    }

    @Override
    public void onDisable() {
        new EcoHandler().updateEcoOnShutdown();
    }

    public static EcoDatabaseSpigot getPlugin() {
        return plugin;
    }

}
