package dev.projectg.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.projectg.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configurate {

    public Configurate() {
    }

    /**
     * Load EcoBase config
     *
     * @param dataDirectory The config's directory
     */
    public static Configurate configuration(Path dataDirectory) throws IOException {

        File folder = dataDirectory.toFile();
        File file = new File(folder, "config.yml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = Configurate.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                Logger.getLogger().severe("Could not generate config file! " + exception.getMessage());
            }
        }

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(dataDirectory.resolve("config.yml").toFile(), Configurate.class);
    }

    @JsonProperty("database-type")
    private String databaseType;

    public String getDatabaseType() {
        return databaseType;
    }

    @JsonProperty("host")
    private String host;

    public String getHost() {
        return host;
    }

    @JsonProperty("port")
    private int port;

    public int getPort() {
        return port;
    }

    @JsonProperty("database")
    private String database;

    public String getDatabase() {
        return database;
    }

    @JsonProperty("username")
    private String username;

    public String getUsername() {
        return username;
    }

    @JsonProperty("password")
    private String password;

    public String getPassword() {
        return password;
    }

    @JsonProperty("sync-interval")
    private int syncInterval;

    public int getSyncInterval() {
        return syncInterval;
    }

    @JsonProperty("enable-sync")
    private boolean enableSync;

    public boolean getEnableSync() {
        return enableSync;
    }
}