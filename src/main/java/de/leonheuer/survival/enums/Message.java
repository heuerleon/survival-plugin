package de.leonheuer.survival.enums;

import de.leonheuer.survival.models.FormattableString;

public enum Message {

    NO_PLAYER("&cDu musst ein Spieler sein."),

    ALLIANCE_UNKNOWN("&cUnbekannter Befehl. Siehe /alliance help"),
    ALLIANCE_NO_OWNER("&cDir gehört keine Allianz."),

    ALLIANCE_CREATE_SYNTAX("&e/alliance create <Name> <Tag>"),
    ALLIANCE_CREATE_NAME_EXISTS("&cEine Allianz mit dem Name %name existiert bereits."),
    ALLIANCE_CREATE_TAG_EXISTS("&cEine Allianz mit dem Tag %tag existiert bereits."),
    ALLIANCE_CREATE_ALREADY("&cDu besitzt bereits eine Allianz."),
    ALLIANCE_CREATE_MEMBER_ALREADY("&cDu bist bereits in einer Allianz."),
    ALLIANCE_CREATE_SUCCESS("&aDu hast erfolgreich die Allianz %name [%tag] erstellt."),
    ALLIANCE_CREATE_TAG("&cDer Tag darf maximal 5 und minimal 3 Zeichen lang sein."),

    ALLIANCE_DISBAND_SUCCESS("&4Du hast die Allianz %name [%tag] aufgelöst!"),

    ALLIANCE_SETNAME_SYNTAX("&e/alliance setname <Name>"),
    ALLIANCE_SETNAME_SUCCESS("&aDer name deiner Allianz wurde zu %name geändert."),

    SKIP_TOGGLE("&c%player möchte die Nacht nichr mehr überspringen. (%count/%all)"),
    SKIP_SUCCESS("&a%player möchte die Nacht überspringen. (%count/%all)"),
    SKIP_DONE("&2Die Nacht wurde übersprungen."),

    JOIN_TITLE("&6&lSurvival 1.18"),
    JOIN_SUBTITLE("&7by Hakuyamu"),

    DEATH_SPECTATE("&eDu hast nun eine Auszeit von 2 Minuten. Danach wirst du automatisch in den Überlebensmodus gesetzt!"),
    DEATH_SURVIVAL("&bDeine Auszeit ist vorbei. Du wurdest wieder in den Überlebensmodus gesetzt.")

    ;

    private final String string;

    Message(String string) {
        this.string = string;
    }

    public FormattableString getString() {
        return new FormattableString(string);
    }

}
