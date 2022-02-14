package dev.projectg.ecodatabase.tasks;

import dev.projectg.database.EcoData;
import dev.projectg.ecodatabase.EcoDatabaseSpigot;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEcoTask {

    public void syncDB(Economy economy, int syncTime) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EcoDatabaseSpigot.plugin, () -> {
            for(Player player : Bukkit.getOnlinePlayers()){
                EcoData.updateBalance(player.getUniqueId(), economy.getBalance(player));
            }
        }, 100, 1000L * 60 * syncTime);

    }
}
