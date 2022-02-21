package me.khanh.plugin.kforms.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import me.khanh.plugin.kforms.KForms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public interface Utils {
    default void print(String s){
        KForms.getInstance().getServer().getLogger().info(ChatColor.translateAlternateColorCodes('&',
                "&7[&bKForms&7] &r" + s));
    }

    default void print(String[] strings){
        for(String s:strings){
            KForms.getInstance().getServer().getLogger().info(ChatColor.translateAlternateColorCodes('&',
                    "&7[&bKForms&7] &r" + s));
        }
    }

    default void severe(String s){
        KForms.getInstance().getServer().getLogger().severe(ChatColor.translateAlternateColorCodes('&',
                "&7[&cKForms&7] &r" + s));
    }

    default String color(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    default String message(String s, Object p){
        if (p instanceof Player){
            s = PlaceholderAPI.setPlaceholders((Player) p, s);
            return color(s);
        }
        return color(s);
    }

    default String path(List<String> list){
        String str="";
        for(String s : list)
        {
            str=str + s + ",";
        }
        return str;
    }

    static void write(String s){
        KForms.getInstance().getServer().getLogger().info(ChatColor.translateAlternateColorCodes('&',
                "&7[&bKForms&7] &r" + s));
    }

    static String placeholder(String s, Object p){
        if (p instanceof Player){
            s = PlaceholderAPI.setPlaceholders((Player) p, s);
            return ChatColor.translateAlternateColorCodes('&', s);
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
