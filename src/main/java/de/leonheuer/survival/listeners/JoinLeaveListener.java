package de.leonheuer.survival.listeners;

import com.mongodb.client.model.Filters;
import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.Message;
import de.leonheuer.survival.models.User;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class JoinLeaveListener implements Listener {

    private final Survival main;

    public JoinLeaveListener(Survival main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendTitle(Message.JOIN_TITLE.getString().get(), Message.JOIN_SUBTITLE.getString().get(), 10, 60, 30);
        Bukkit.getOnlinePlayers().forEach(other ->
                other.playSound(other.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, .5f, 1.5f)
        );

        User user = main.getMongoManager().getUsers().find(Filters.eq("uuid", player.getUniqueId())).first();
        if (user == null) {
            return;
        }
        if (System.currentTimeMillis() - user.getLastDeath() < 120*60*1000) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Message.DEATH_SPECTATE.getString().get());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        List<Player> skip = main.getSkipVote();
        if (skip.contains(player)) {
            skip.remove(player);
            player.sendMessage(Message.SKIP_TOGGLE.getString()
                    .replace("%count", "" + skip.size())
                    .replace("%all", "" + Bukkit.getOnlinePlayers().size())
                    .get());
        }
    }

}
