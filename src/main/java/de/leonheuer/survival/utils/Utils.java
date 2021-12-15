package de.leonheuer.survival.utils;

import de.leonheuer.survival.Survival;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Utils {

    private static final Survival main = JavaPlugin.getPlugin(Survival.class);

    @Nullable
    public static OfflinePlayer getOfflinePlayer(String username) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (Objects.equals(player.getName(), username)) {
                return player;
            }
        }
        return null;
    }

}
