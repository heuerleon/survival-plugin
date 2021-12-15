package de.leonheuer.survival.models;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private ObjectId id;
    private String uuid;
    private int deaths = 0;
    private int kills = 0;
    private long lastDeath;
    private Map<String, String> remembered = new HashMap<>();
    private List<String> invites = new ArrayList<>();

    public User() {}

    public User(@NotNull String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public long getLastDeath() {
        return lastDeath;
    }

    public void setLastDeath(long lastDeath) {
        this.lastDeath = lastDeath;
    }

    public Map<String, String> getRemembered() {
        return remembered;
    }

    public void setRemembered(Map<String, String> remembered) {
        this.remembered = remembered;
    }

    public List<String> getInvites() {
        return invites;
    }

    public void setInvites(List<String> invites) {
        this.invites = invites;
    }
}
