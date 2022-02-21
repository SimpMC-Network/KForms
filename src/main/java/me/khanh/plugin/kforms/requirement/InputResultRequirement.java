package me.khanh.plugin.kforms.requirement;

import me.khanh.plugin.kforms.utils.Utils;
import org.bukkit.entity.Player;

public class InputResultRequirement extends Requirement implements Utils {
    private final String input;
    private final String result;
    private final RequirementType type;

    public InputResultRequirement(RequirementType type, String input, String result) {
        this.input = input;
        this.result = result;
        this.type = type;
    }
    @Override
    public boolean evaluate(Player player) {
        int res;
        int in;
        String parsedInput = message(this.input, player);
        String parsedResult = message(this.result, player);
        switch (this.type) {
            case STRING_CONTAINS: {
                return parsedInput.contains(parsedResult);
            }
            case STRING_EQUALS: {
                return parsedInput.equals(parsedResult);
            }
            case STRING_EQUALS_IGNORECASE: {
                return parsedInput.equalsIgnoreCase(parsedResult);
            }
            case STRING_DOES_NOT_CONTAIN: {
                return !parsedInput.contains(parsedResult);
            }
            case STRING_DOES_NOT_EQUAL: {
                return !parsedInput.equals(parsedResult);
            }
            case STRING_DOES_NOT_EQUAL_IGNORECASE: {
                return !parsedInput.equalsIgnoreCase(parsedResult);
            }
        }
        try {
            in = Integer.parseInt(parsedInput);
            res = Integer.parseInt(parsedResult);
        }
        catch (NumberFormatException ex) {
            ex.printStackTrace();
            return false;
        }
        switch (this.type) {
            case GREATER_THAN: {
                return in > res;
            }
            case GREATER_THAN_EQUAL_TO: {
                return in >= res;
            }
            case EQUAL_TO: {
                return in == res;
            }
            case LESS_THAN_EQUAL_TO: {
                return in <= res;
            }
            case LESS_THAN: {
                return in < res;
            }
        }
        return false;
    }
}
