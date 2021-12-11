package de.leonheuer.survival;

import de.leonheuer.survival.commands.AllianceCommand;
import de.leonheuer.survival.commands.SkipCommand;
import de.leonheuer.survival.database.MongoManager;
import de.leonheuer.survival.listeners.BedEnterListener;
import de.leonheuer.survival.listeners.DeathListener;
import de.leonheuer.survival.listeners.JoinLeaveListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Survival extends JavaPlugin {

    public static final String ALLIANCE_PREFIX = "&3&lA: ";
    private MongoManager mongoManager;
    private final List<Player> skipVote = new ArrayList<>();

    @Override
    public void onEnable() {
        mongoManager = new MongoManager();

        registerCommand("skip", new SkipCommand(this));
        registerCommand("alliance", new AllianceCommand(this));

        registerListener(new BedEnterListener(this));
        registerListener(new JoinLeaveListener(this));
        registerListener(new DeathListener(this));
    }

    @Override
    public void onDisable() {
        mongoManager.disconnect();
    }

    private void registerCommand(String command, CommandExecutor executor) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) {
            this.getLogger().severe("Command " + command + " not found in plugin.yml.");
        } else {
            cmd.setExecutor(executor);
        }
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public List<Player> getSkipVote() {
        return skipVote;
    }

}
