package dev.projectg.ecodatabase.tasks;

import dev.projectg.database.EcoData;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import dev.projectg.ecodatabase.api.VaultApiHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEcoTask {

    public void syncDB(int syncTime) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()){
                EcoData.updateBalance(player.getUniqueId(), VaultApiHandler.eco().getBalance(player) );
            }
        }, 100, 1000L * 60 * syncTime);

    }
}
