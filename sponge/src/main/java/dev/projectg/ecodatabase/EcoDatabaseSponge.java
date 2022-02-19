package dev.projectg.ecodatabase;

import com.google.inject.Inject;
import dev.projectg.configuration.Configurate;
import dev.projectg.database.DatabaseSetup;
import dev.projectg.ecodatabase.handlers.EcoHandler;
import dev.projectg.logger.EcoDatabaseLogger;
import dev.projectg.logger.JavaUtilLogger;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin("EcoDatabase")
public class EcoDatabaseSponge {

    public static EcoDatabaseSponge plugin;

    Task.Builder taskBuilder = Task.builder();

    @Inject
    public static Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path configPath;

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        new JavaUtilLogger((java.util.logging.Logger) logger);
        EcoDatabaseLogger logger = EcoDatabaseLogger.getLogger();

        // Config setup
        Path path = configPath;
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
            taskBuilder.interval(30, TimeUnit.MINUTES).execute(() -> {
                new DatabaseSetup().connectionAlive();
            });
        }

        // End
        logger.info("EcoDatabase has been enabled!");
    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        // Any tear down per-game instance. This can run multiple times when
        // using the integrated (singleplayer) server.
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getConfigPath() {
        return configPath;
    }

}
