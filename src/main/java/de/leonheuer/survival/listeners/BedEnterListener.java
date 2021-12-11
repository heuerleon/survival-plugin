package de.leonheuer.survival.listeners;

import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.List;

public class BedEnterListener implements Listener {

    private final Survival main;

    public BedEnterListener(Survival main) {
        this.main = main;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        List<Player> skip = main.getSkipVote();
        if (!skip.contains(player)) {
            skip.add(player);
            player.sendMessage(Message.SKIP_SUCCESS.getString()
                    .replace("%count", "" + skip.size())
                    .replace("%all", "" + Bukkit.getOnlinePlayers().size())
                    .get());
            if (skip.size() > Math.floorDiv(Bukkit.getOnlinePlayers().size(), 2)) {
                skip.clear();
                Bukkit.broadcastMessage(Message.SKIP_DONE.getString().get());
                Bukkit.getWorlds().forEach(world -> world.setTime(6));
            }
        }
    }

}
