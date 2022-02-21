package me.khanh.plugin.kforms.utils;

import org.bukkit.entity.Player;

import java.util.Locale;

public final class ExpUtils {
    private ExpUtils() {
    }

    public static void setExp(Player target, String strAmount) {
        long amount;
        if ((strAmount = strAmount.toLowerCase(Locale.ENGLISH)).contains("l")) {
            int neededLevel = Integer.parseInt(strAmount.replaceAll("l", "")) + target.getLevel();
            amount = ExpUtils.getExpToLevel(neededLevel) + (ExpUtils.getTotalExperience(target) - ExpUtils.getExpToLevel(target.getLevel()));
            ExpUtils.setTotalExperience(target, 0);
        } else {
            amount = Long.parseLong(strAmount);
        }
        if ((amount += (long)ExpUtils.getTotalExperience(target)) > Integer.MAX_VALUE) {
            amount = Integer.MAX_VALUE;
        }
        if (amount < 0L) {
            amount = 0L;
        }
        ExpUtils.setTotalExperience(target, (int)amount);
    }

    public static void setTotalExperience(Player player, int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }
        player.setExp(0.0f);
        player.setLevel(0);
        player.setTotalExperience(0);
        int amount = exp;
        while (amount > 0) {
            int expToLevel = ExpUtils.getExpAtLevel(player);
            if ((amount -= expToLevel) >= 0) {
                player.giveExp(expToLevel);
                continue;
            }
            player.giveExp(amount += expToLevel);
            amount = 0;
        }
    }

    private static int getExpAtLevel(Player player) {
        return ExpUtils.getExpAtLevel(player.getLevel());
    }

    public static int getExpAtLevel(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        }
        if (level <= 30) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }

    public static int getExpToLevel(int level) {
        int exp = 0;
        for (int currentLevel = 0; currentLevel < level; ++currentLevel) {
            exp += ExpUtils.getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    public static int getTotalExperience(Player player) {
        int exp = Math.round((float)ExpUtils.getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();
        while (currentLevel > 0) {
            exp += ExpUtils.getExpAtLevel(--currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }
}
