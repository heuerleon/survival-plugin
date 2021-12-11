package de.leonheuer.survival.commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.Message;
import de.leonheuer.survival.models.Alliance;
import org.bson.conversions.Bson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AllianceCommand implements CommandExecutor, TabCompleter {

    private final Survival main;

    public AllianceCommand(Survival main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        MongoCollection<Alliance> alliances = main.getMongoManager().getAlliances();

        switch (args[0]) {
            case "create" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.ALLIANCE_CREATE_SYNTAX.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                if (alliances.countDocuments(Filters.eq("name", args[1])) > 0) {
                    player.sendMessage(Message.ALLIANCE_CREATE_NAME_EXISTS.getString()
                            .replace("%name", args[1])
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                if (alliances.countDocuments(Filters.eq("tag", args[2])) > 0) {
                    player.sendMessage(Message.ALLIANCE_CREATE_TAG_EXISTS.getString()
                            .replace("%tag", args[1])
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                if (alliances.countDocuments(Filters.eq("ownerUUID", player.getUniqueId().toString())) > 0) {
                    player.sendMessage(Message.ALLIANCE_CREATE_ALREADY.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                if (alliances.countDocuments(Filters.in("members", player.getUniqueId().toString())) > 0) {
                    player.sendMessage(Message.ALLIANCE_CREATE_MEMBER_ALREADY.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                if (args[2].length() > 5 || args[2].length() < 3) {
                    player.sendMessage(Message.ALLIANCE_CREATE_TAG.getString().get());
                    return true;
                }
                Alliance alliance = new Alliance(player.getUniqueId().toString(), args[1], args[2].toUpperCase(Locale.GERMAN));
                alliances.insertOne(alliance);
                player.sendMessage(Message.ALLIANCE_CREATE_SUCCESS.getString()
                        .replace("%name", args[1])
                        .replace("%tag", args[2])
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            case "disband" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }
                Bson filter = Filters.eq("ownerUUID", player.getUniqueId().toString());
                Alliance alliance = alliances.find(filter).first();
                if (alliance == null) {
                    player.sendMessage(Message.ALLIANCE_NO_OWNER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                alliances.deleteOne(filter);
                player.sendMessage(Message.ALLIANCE_DISBAND_SUCCESS.getString()
                        .replace("%name", alliance.getName())
                        .replace("%tag", alliance.getTag())
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            case "setname" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(Message.ALLIANCE_SETNAME_SYNTAX.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                Bson filter = Filters.eq("ownerUUID", player.getUniqueId().toString());
                Alliance alliance = alliances.find(filter).first();
                if (alliance == null) {
                    player.sendMessage(Message.ALLIANCE_NO_OWNER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }
                alliances.updateOne(filter, Updates.set("name", args[1]));
                player.sendMessage(Message.ALLIANCE_SETNAME_SUCCESS.getString()
                        .replace("%name", alliance.getName())
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            default -> sender.sendMessage(Message.ALLIANCE_UNKNOWN.getString().get(Survival.ALLIANCE_PREFIX));
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        // TODO help
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            arguments.add("create");
            arguments.add("disband");
            arguments.add("invite");
            arguments.add("kick");
            arguments.add("promote");
            arguments.add("demote");
            arguments.add("bank");
            arguments.add("bp");
            arguments.add("transfer");
            arguments.add("info");
            arguments.add("chat");
            arguments.add("accept");
            arguments.add("leave");
            arguments.add("setname");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }
        Collections.sort(completions);
        return completions;
    }

}
