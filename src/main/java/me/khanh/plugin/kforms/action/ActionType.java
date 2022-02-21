package me.khanh.plugin.kforms.action;

import me.khanh.plugin.kforms.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ActionType{
    CONSOLE("[console]"),
    PLAYER("[player]"),
    PLAYER_COMMAND_EVENT("[commandevent]"),
    MESSAGE("[message]"),
    BROADCAST("[broadcast]"),
    CHAT("[chat]"),
    OPEN_FORM("[openform]"),
    CONNECT("[connect]"),
    REFRESH("[refresh]"),
    BROADCAST_SOUND("[broadcastsound]"),
    BROADCAST_WORLD_SOUND("[broadcastsoundworld]"),
    PLAY_SOUND("[sound]"),
    PLACEHOLDER("[placeholder]");

    private final String identifier;

    private ActionType(String identifier) {
        this.identifier = identifier;
    }

    public static ActionType getType(String s) {
        for (ActionType type : ActionType.values()) {
            if (!s.startsWith(type.getIdentifier())) continue;
            return type;
        }
        Utils.write("Couldn't find Action Type with name: " + s);
        return null;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public static String getExec(String s){
        return s.split("\\[[^\\]]*] ", 2)[1];
    }

    public static ActionType getActionType(String s){
        Pattern pattern = Pattern.compile("\\[[^\\]]*]");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()){
            return getType(matcher.group(0));
        }
        return null;
    }
}
