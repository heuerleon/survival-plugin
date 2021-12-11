package de.leonheuer.survival.commands;

import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkipCommand implements CommandExecutor {

    private final Survival main;

    public SkipCommand(Survival main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cDu musst ein Spieler sein.");
            return true;
        }

        List<Player> skip = main.getSkipVote();
        if (skip.contains(player)) {
            skip.remove(player);
            player.sendMessage(Message.SKIP_TOGGLE.getString()
                    .replace("%count", "" + skip.size())
                    .replace("%all", "" + Bukkit.getOnlinePlayers().size())
                    .get());
            return true;
        }

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
        return true;
    }

}
