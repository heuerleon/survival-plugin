package de.leonheuer.survival.enums;

public enum AlliancePermission {

    BASIC("Kein Schaden und Chatten", 0, "Mitglied I"),
    ACCESS_1("Gemeinsamer tragbarer Rucksack", 1, "Mitglied II"),
    EXTENDED("Positionen sehen", 2, "Ältester I"),
    ACCESS_2("Gemeinsames Bank Konto", 3, "Ältester II"),
    ADMIN("Mitglieder verwalten", 4, "Manager"),
    ;

    private final String name;
    private final int permLevel;
    private final String rank;

    AlliancePermission(String name, int permLevel, String rank) {
        this.name = name;
        this.permLevel = permLevel;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public int getPermLevel() {
        return permLevel;
    }

    public String getRank() {
        return rank;
    }
}
