package dev.projectg.ecodatabase;

import dev.projectg.configuration.Configurate;
import dev.projectg.database.DatabaseSetup;
import dev.projectg.database.EcoDatabase;
import dev.projectg.ecodatabase.api.VaultApi;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import dev.projectg.ecodatabase.listeners.PlayerEvents;
import dev.projectg.ecodatabase.utils.Metrics;
import dev.projectg.logger.JavaUtilLogger;
import dev.projectg.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class EcoDatabaseSpigot extends JavaPlugin {

    public static EcoDatabaseSpigot plugin;
    private static EcoDatabase ecoData;

    @Override
    public void onEnable() {
        plugin = this;

        // Logger
        Logger logger = new JavaUtilLogger(this.getLogger());

        Path path = this.getDataFolder().toPath();

        // Config setup
        Configurate config = null;
        try {
            config = Configurate.configuration(path);
        } catch (IOException e) {
            logger.severe("Could not load config.yml! " + e.getMessage());
        }

        // Bstats metrics
        new Metrics(this, 14430);

        // Enable vault
        new VaultApi();

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEvents(), this);

        // Sync Economy to database
        EcoHandler.handler().updateHashmapBalance();
        if (Objects.requireNonNull(config).getEnableSync()) {
            logger.info("Sync economy enabled");
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> EcoHandler.handler().queryHashmapBalance(), 20L + 30, 20L * 60 * config.getSyncInterval());
        }

        // Database setup
        logger.info("Selected " + config.getDatabaseType() + " database!");
        new DatabaseSetup().mysqlSetup(path, config);
        ecoData = new EcoDatabase();

        // End
        logger.info("EcoDatabase has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        EcoHandler.handler().updateEcoOnShutdown();
        new DatabaseSetup().connectionClose();
    }

    public static EcoDatabaseSpigot getPlugin() {
        return plugin;
    }

    public EcoDatabase getEcoDatabase() {
        return ecoData;
    }
}
