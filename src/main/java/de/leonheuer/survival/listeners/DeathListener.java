package de.leonheuer.survival.listeners;

import com.mongodb.client.model.Filters;
import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.Message;
import de.leonheuer.survival.models.User;
import org.bson.conversions.Bson;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class DeathListener implements Listener {

    private final Survival main;

    public DeathListener(Survival main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        Bson filter = Filters.eq("uuid", uuid.toString());
        User user = main.getMongoManager().getUsers().find(filter).first();
        if (user == null) {
            user = new User(uuid.toString());
            user.setDeaths(1);
        } else {
            user.setDeaths(user.getDeaths() + 1);
        }
        user.setLastDeath(System.currentTimeMillis());
        main.getMongoManager().getUsers().replaceOne(filter, user);

        EntityDamageEvent deathCause = event.getEntity().getLastDamageCause();
        if (deathCause == null) {
            return;
        }
        Entity entity = deathCause.getEntity();
        if ((entity instanceof Player killer)) {
            if (killer == event.getEntity()) {
                return;
            }
            Bson filter2 = Filters.eq("uuid", killer.getUniqueId().toString());
            User other = main.getMongoManager().getUsers().find(filter2).first();
            if (other == null) {
                other = new User(killer.getUniqueId().toString());
                other.setKills(1);
            } else {
                other.setKills(other.getKills() + 1);
            }
            main.getMongoManager().getUsers().replaceOne(filter, other);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = main.getMongoManager().getUsers().find(Filters.eq("uuid", player.getUniqueId())).first();
        if (user == null) {
            return;
        }
        if (System.currentTimeMillis() - user.getLastDeath() < 120*60*1000) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Message.DEATH_SPECTATE.getString().get());
        }
    }

}
