package me.khanh.plugin.kforms.requirement;

import me.khanh.plugin.kforms.utils.ExpUtils;
import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.entity.Player;

public class HasExpRequirement extends Requirement implements Utils {
    private final int amount;
    private final boolean invert;
    private final boolean level;

    public HasExpRequirement(int amount, boolean invert, boolean level) {
        this.amount = amount;
        this.invert = invert;
        this.level = level;
    }

    @Override
    public boolean evaluate(Player player) {
        int has = this.level ? player.getLevel() : ExpUtils.getTotalExperience(player);
        if (has < amount) {
            return this.invert;
        }
        return !this.invert;
    }
}
