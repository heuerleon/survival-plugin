package de.leonheuer.survival.models;

import org.bukkit.ChatColor;

public class FormattableString {

    private String result;

    public FormattableString(String base) {
        result = base;
    }

    public FormattableString replace(String from, String to) {
        result = result.replaceFirst(from, to);
        return this;
    }

    public FormattableString replaceAll(String from, String to) {
        result = result.replaceAll(from, to);
        return this;
    }

    public String get() {
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public String get(String prefix) {
        return ChatColor.translateAlternateColorCodes('&', prefix + result);
    }

}
