package de.leonheuer.survival.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.leonheuer.survival.Survival;
import de.leonheuer.survival.enums.AlliancePermission;
import de.leonheuer.survival.enums.Message;
import de.leonheuer.survival.models.Alliance;
import de.leonheuer.survival.models.User;
import de.leonheuer.survival.utils.Utils;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        MongoCollection<User> users = main.getMongoManager().getUsers();

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
            case "invite" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(Message.ALLIANCE_INVITE_SYNTAX.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                FindIterable<Alliance> memberAlliances = alliances.find();
                Alliance result = null;
                for (Alliance al : memberAlliances) {
                    if (al.getMembers().containsKey(player.getUniqueId().toString())) {
                        if (al.getMembers().get(player.getUniqueId().toString()) >= 4) {
                            result = al;
                            break;
                        }
                    }
                    if (Objects.equals(al.getOwnerUUID(), player.getUniqueId().toString())) {
                        result = al;
                        break;
                    }
                }

                if (result == null) {
                    player.sendMessage(Message.NO_PERMISSION.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                OfflinePlayer other = Utils.getOfflinePlayer(args[1]);
                if (other == null) {
                    player.sendMessage(Message.INVALID_PLAYER.getString()
                            .replace("%player", args[1])
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                Bson filter = Filters.eq("uuid", other.getUniqueId().toString());
                User user = users.find(filter).first();
                if (user == null) {
                    user = new User(other.getUniqueId().toString());
                    user.getInvites().add(result.getTag());
                    users.insertOne(user);
                    player.sendMessage(Message.ALLIANCE_INVITE_ALREADY.getString()
                            .replace("%player", other.getName())
                            .replace("%name", result.getName())
                            .replace("%tag", result.getTag())
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                if (user.getInvites().contains(result.getTag())) {
                    player.sendMessage(Message.ALLIANCE_INVITE_ALREADY.getString()
                            .replace("%player", other.getName())
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                user.getInvites().add(result.getTag());
                users.replaceOne(filter, user);
                player.sendMessage(Message.ALLIANCE_INVITE_SUCCESS.getString()
                        .replace("%player", other.getName())
                        .replace("%name", result.getName())
                        .replace("%tag", result.getTag())
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            case "invites" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                Bson filter = Filters.eq("uuid", player.getUniqueId());
                User user = users.find(filter).first();
                if (user == null) {
                    player.sendMessage(Message.ALLIANCE_INVITES_NONE.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                StringJoiner sj = new StringJoiner("§8, §7");
                user.getInvites().forEach(sj::add);
                player.sendMessage(Message.ALLIANCE_INVITES.getString()
                        .replace("%invites", sj.toString())
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            case "accept" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                Bson filter = Filters.eq("uuid", player.getUniqueId());
                User user = users.find(filter).first();
                if (user == null || user.getInvites().isEmpty()) {
                    player.sendMessage(Message.ALLIANCE_INVITES_NONE.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                FindIterable<Alliance> memberAlliances = alliances.find();
                Alliance result = null;
                for (Alliance al : memberAlliances) {
                    if (al.getMembers().containsKey(player.getUniqueId().toString())) {
                        if (al.getMembers().get(player.getUniqueId().toString()) >= 4) {
                            result = al;
                            break;
                        }
                    }
                    if (Objects.equals(al.getOwnerUUID(), player.getUniqueId().toString())) {
                        result = al;
                        break;
                    }
                }

                if (result != null) {
                    player.sendMessage(Message.ALLIANCE_ACCEPT_ALREADY.getString()
                            .replace("%name", result.getName())
                            .replace("%tag", result.getTag())
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                if (user.getInvites().size() > 1) {
                    if (args.length < 2) {
                        StringJoiner sj = new StringJoiner("§8, §7");
                        user.getInvites().forEach(sj::add);
                        player.sendMessage(Message.ALLIANCE_ACCEPT_SPECIFY.getString()
                                .replace("%invites", sj.toString())
                                .get(Survival.ALLIANCE_PREFIX));
                        return true;
                    }

                    Bson alFilter = Filters.eq("tag", args[1].toUpperCase());
                    Alliance specified = alliances.find(alFilter).first();
                    if (specified == null && user.getInvites().contains(args[1].toUpperCase())) {
                        player.sendMessage(Message.ALLIANCE_CHANGED.getString()
                                .replace("%tag", args[1].toUpperCase())
                                .get(Survival.ALLIANCE_PREFIX));
                        user.getInvites().remove(args[1].toUpperCase());
                        return true;
                    }
                    if (specified == null || !user.getInvites().contains(specified.getTag())) {
                        player.sendMessage(Message.ALLIANCE_ACCEPT_NOT_INVITED.getString()
                                .replace("%tag", args[1].toUpperCase())
                                .get(Survival.ALLIANCE_PREFIX));
                        return true;
                    }

                    specified.getMembers().put(player.getUniqueId().toString(), 0);
                    user.getInvites().remove(specified.getTag());
                    player.sendMessage(Message.ALLIANCE_ACCEPT_SUCCESS.getString()
                            .replace("%name", specified.getName())
                            .replace("%tag", specified.getTag())
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                Bson alFilter = Filters.eq("tag", user.getInvites().toArray()[0]);
                Alliance specified = alliances.find(alFilter).first();
                if (specified == null) {
                    player.sendMessage(Message.ALLIANCE_CHANGED.getString()
                            .replace("%tag", args[1].toUpperCase())
                            .get(Survival.ALLIANCE_PREFIX));
                    user.getInvites().remove(args[1].toUpperCase());
                    return true;
                }

                specified.getMembers().put(player.getUniqueId().toString(), 0);
                user.getInvites().remove(specified.getTag());
                player.sendMessage(Message.ALLIANCE_ACCEPT_SUCCESS.getString()
                        .replace("%name", specified.getName())
                        .replace("%tag", specified.getTag())
                        .get(Survival.ALLIANCE_PREFIX));
                return true;
            }
            case "transfer" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(Message.ALLIANCE_TRANSFER_SYNTAX.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                Bson filter = Filters.eq("ownerUUID", player.getUniqueId().toString());
                Alliance alliance = alliances.find(filter).first();
                if (alliance == null) {
                    player.sendMessage(Message.ALLIANCE_NO_OWNER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                OfflinePlayer newOwner = Utils.getOfflinePlayer(args[1]);
                if (newOwner == null) {
                    player.sendMessage(Message.INVALID_PLAYER.getString()
                            .replace("%player", args[1])
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                alliance.setOwnerUUID(newOwner.getUniqueId().toString());
                alliance.getMembers().remove(newOwner.getUniqueId().toString());
                alliance.getMembers().put(player.getUniqueId().toString(), 0);
                alliances.replaceOne(filter, alliance);
                player.sendMessage(Message.ALLIANCE_TRANSFER_SUCCESS.getString()
                        .replace("%name", alliance.getName())
                        .replace("%tag", alliance.getTag())
                        .replace("%player", newOwner.getName())
                        .get());
                return true;
            }
            case "info" -> {
                if (args.length < 2) {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Message.NO_PLAYER.getString().get());
                        return true;
                    }

                    FindIterable<Alliance> memberAlliances = alliances.find();
                    Alliance result = null;
                    for (Alliance al : memberAlliances) {
                        if (al.getMembers().containsKey(player.getUniqueId().toString())) {
                            result = al;
                            break;
                        }
                        if (Objects.equals(al.getOwnerUUID(), player.getUniqueId().toString())) {
                            result = al;
                            break;
                        }
                    }

                    if (result == null) {
                        player.sendMessage(Message.ALLIANCE_NO_MEMBER.getString().get(Survival.ALLIANCE_PREFIX));
                        return true;
                    }

                    Player owner = Bukkit.getPlayer(UUID.fromString(result.getOwnerUUID()));
                    String ownerName = "§cunbekannt";
                    if (owner != null) {
                        ownerName = owner.getName();
                    }
                    player.sendMessage(Message.ALLIANCE_INFO.getString()
                            .replace("%name", result.getName())
                            .replace("%tag", result.getTag())
                            .replace("%owner", ownerName)
                            .get());
                    if (result.getMembers().isEmpty()) {
                        player.sendMessage(Message.ALLIANCE_NO_MEMBERS.getString().get());
                    }
                    for (Map.Entry<String,Integer> member : result.getMembers().entrySet()) {
                        Player memberPlayer = Bukkit.getPlayer(member.getKey());
                        AlliancePermission level = AlliancePermission.fromPermLevel(member.getValue());
                        if (memberPlayer == null || level == null) {
                            continue;
                        }
                        player.sendMessage(Message.ALLIANCE_MEMBER.getString()
                                .replace("%name", memberPlayer.getName())
                                .replace("%rank", level.getRank())
                                .get());
                    }
                }

                Bson filter = Filters.eq("tag", args[1]);
                Alliance alliance = alliances.find(filter).first();
                if (alliance == null) {
                    sender.sendMessage(Message.ALLIANCE_NOT_EXIST.getString()
                            .replace("%tag", args[1])
                            .get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                Player owner = Bukkit.getPlayer(UUID.fromString(alliance.getOwnerUUID()));
                String ownerName = "§cunbekannt";
                if (owner != null) {
                    ownerName = owner.getName();
                }
                sender.sendMessage(Message.ALLIANCE_INFO.getString()
                        .replace("%name", alliance.getName())
                        .replace("%tag", alliance.getTag())
                        .replace("%owner", ownerName)
                        .get());
                if (alliance.getMembers().isEmpty()) {
                    sender.sendMessage(Message.ALLIANCE_NO_MEMBERS.getString().get());
                }
                for (Map.Entry<String,Integer> member : alliance.getMembers().entrySet()) {
                    Player memberPlayer = Bukkit.getPlayer(member.getKey());
                    AlliancePermission level = AlliancePermission.fromPermLevel(member.getValue());
                    if (memberPlayer == null || level == null) {
                        continue;
                    }
                    sender.sendMessage(Message.ALLIANCE_MEMBER.getString()
                            .replace("%name", memberPlayer.getName())
                            .replace("%rank", level.getRank())
                            .get());
                }
                return true;
            }
            case "ranks" -> {
                for (AlliancePermission level : AlliancePermission.values()) {
                    sender.sendMessage(Message.ALLIANCE_RANK.getString()
                            .replace("%level", "" + level.getPermLevel())
                            .replace("%rank", level.getRank())
                            .replace("%name", level.getName())
                            .get());
                }
                return true;
            }
            case "chat" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(Message.ALLIANCE_CHAT_SYNTAX.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                Alliance result = alliances.find(Filters.eq("ownerUUID", player.getUniqueId().toString())).first();
                if (result == null) {
                    FindIterable<Alliance> memberAlliances = alliances.find();
                    for (Alliance al : memberAlliances) {
                        if (al.getMembers().containsKey(player.getUniqueId().toString())) {
                            result = al;
                            break;
                        }
                    }
                }

                if (result == null) {
                    player.sendMessage(Message.ALLIANCE_NO_MEMBER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                for (Player receiver : Bukkit.getOnlinePlayers()) {
                    if (result.getMembers().containsKey(receiver.getUniqueId().toString())) {
                        receiver.sendMessage(Message.ALLIANCE_CHAT.getString()
                                .replace("%sender", player.getName())
                                .replace("%message", args[1])
                                .get(Survival.ALLIANCE_PREFIX));
                    }
                }
                return true;
            }
            case "leave" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Message.NO_PLAYER.getString().get());
                    return true;
                }

                Alliance ownerAlliance = alliances.find(Filters.eq("ownerUUID", player.getUniqueId().toString())).first();
                if (ownerAlliance != null) {
                    player.sendMessage(Message.ALLIANCE_LEAVE_OWNER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                FindIterable<Alliance> memberAlliances = alliances.find();
                Alliance result = null;
                for (Alliance al : memberAlliances) {
                    if (al.getMembers().containsKey(player.getUniqueId().toString())) {
                        result = al;
                        break;
                    }
                }
                if (result == null) {
                    player.sendMessage(Message.ALLIANCE_NO_MEMBER.getString().get(Survival.ALLIANCE_PREFIX));
                    return true;
                }

                result.getMembers().remove(player.getUniqueId().toString());
                alliances.replaceOne(Filters.eq("name", result.getName()), result);
                player.sendMessage(Message.ALLIANCE_LEAVE_SUCCESS.getString()
                        .replace("%name", result.getName())
                        .replace("%tag", result.getTag())
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
            arguments.add("invites");
            arguments.add("accept");
            arguments.add("kick");
            arguments.add("promote");
            arguments.add("demote");
            arguments.add("bank");
            arguments.add("bp");
            arguments.add("transfer");
            arguments.add("info");
            arguments.add("ranks");
            arguments.add("chat");
            arguments.add("leave");
            arguments.add("setname");
            StringUtil.copyPartialMatches(args[0], arguments, completions);
        }
        Collections.sort(completions);
        return completions;
    }

}
