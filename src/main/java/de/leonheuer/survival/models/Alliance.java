package de.leonheuer.survival.models;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Alliance {

    private ObjectId id;
    private String ownerUUID;
    private HashMap<String, Integer> members = new HashMap<>();
    private String name;
    private String tag;
    private List<String> invites = new ArrayList<>();
    private String created;

    public Alliance() {}

    public Alliance(@NotNull String ownerUUID, @NotNull String name, @NotNull String tag) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.tag = tag;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public HashMap<String, Integer> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Integer> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<String> getInvites() {
        return invites;
    }

    public void setInvites(List<String> invites) {
        this.invites = invites;
    }

}
