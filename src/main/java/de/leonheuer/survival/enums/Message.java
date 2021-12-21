package de.leonheuer.survival.enums;

import de.leonheuer.survival.models.FormattableString;

public enum Message {

    NO_PLAYER("&cDu musst ein Spieler sein."),

    ALLIANCE_UNKNOWN("&cUnbekannter Befehl. Siehe /alliance help"),
    ALLIANCE_NO_OWNER("&cDir gehört keine Allianz."),
    ALLIANCE_NO_MEMBER("&cDu bist in keiner Allianz Mitglied."),
    ALLIANCE_CHANGED("&cDie Allianz %tag existiert nicht mehr."),
    INVALID_PLAYER("&cDer Spieler %player ist unbekannt."),
    NO_PERMISSION("&cDu musst mindestens Manager einer Allianz sein."),

    ALLIANCE_CREATE_SYNTAX("&e/alliance create <Name> <Tag>"),
    ALLIANCE_CREATE_NAME_EXISTS("&cEine Allianz mit dem Name %name existiert bereits."),
    ALLIANCE_CREATE_TAG_EXISTS("&cEine Allianz mit dem Tag %tag existiert bereits."),
    ALLIANCE_CREATE_ALREADY("&cDu besitzt bereits eine Allianz."),
    ALLIANCE_CREATE_MEMBER_ALREADY("&cDu bist bereits in einer Allianz."),
    ALLIANCE_CREATE_SUCCESS("&aDu hast erfolgreich die Allianz %name [%tag] erstellt."),
    ALLIANCE_CREATE_TAG("&cDer Tag darf maximal 5 und minimal 3 Zeichen lang sein."),

    ALLIANCE_DISBAND_SUCCESS("&4Du hast die Allianz %name [%tag] aufgelöst!"),

    ALLIANCE_INVITE_SYNTAX("&e/alliance invite <Nachricht>"),
    ALLIANCE_INVITE_ALREADY("&cDer Spieler %player wurde bereits in deine Allianz eingeladen."),
    ALLIANCE_INVITE_SUCCESS("&aDer Spieler %player wurde erfolgreich in die Allianz %name [%tag] eingeladen."),

    ALLIANCE_INVITES("&aDu wurdest eingeladen zu: &7%invites"),
    ALLIANCE_INVITES_NONE("&cDu hast keine Einladungen."),

    ALLIANCE_ACCEPT_SPECIFY("&eDu hast mehrere Einladungen! Bitte gib an: %invites"),
    ALLIANCE_ACCEPT_NOT_INVITED("&cDu hast keine Einladung für die Allianz mit dem Tag %tag."),
    ALLIANCE_ACCEPT_ALREADY("&cDu bist bereits Mitglied der Allianz %name [%tag]."),
    ALLIANCE_ACCEPT_SUCCESS("&aDu bist nun Mitglied der Allianz %name [%tag]."),

    ALLIANCE_TRANSFER_SYNTAX("&e/alliance transfer <Spieler>"),
    ALLIANCE_TRANSFER_SUCCESS("&2Du hast die Allianz %name [%tag] erfolgreich an %player übertragen."),

    ALLIANCE_INFO("&7Infos zur Allianz &b%name &8[&7%tag&8]&7:\n&8» &6Ersteller: &7%owner\n&bMitglieder&7:"),
    ALLIANCE_MEMBER("&8» &7%name &8- &7%rank"),
    ALLIANCE_NO_MEMBERS("&8» &ckeine"),
    ALLIANCE_NOT_EXIST("&cEine Allianz mit dem Tag [%tag] konnte nicht gefunden werden."),

    ALLIANCE_RANK("&8(&7%level&8) &e%rank &8- &7%name"),

    ALLIANCE_CHAT("&e%sender &8» &7%message"),
    ALLIANCE_CHAT_SYNTAX("&e/alliance chat <Nachricht>"),

    ALLIANCE_LEAVE_OWNER("&cDu kannst deine eigene Allianz nicht verlassen."),
    ALLIANCE_LEAVE_SUCCESS("&aDu hast die Allianz %name [%tag] verlassen."),

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
