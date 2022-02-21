package me.khanh.plugin.kforms.requirement;

import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class RegexMatchesRequirement extends Requirement implements Utils {
    private final Pattern pattern;
    private final String input;
    private final boolean invert;

    public RegexMatchesRequirement(Pattern pattern, String input, boolean invert) {
        this.pattern = pattern;
        this.input = input;
        this.invert = invert;
    }

    @Override
    public boolean evaluate(Player player) {
        String toCheck = message(input, player);
        if (this.invert) {
            return !this.pattern.matcher(message(toCheck, player)).find();
        }
        return this.pattern.matcher(message(toCheck, player)).find();
    }
}
