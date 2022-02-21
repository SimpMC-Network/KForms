package me.khanh.plugin.kforms.requirement;

import java.util.Arrays;
import java.util.List;

public enum RequirementType {
    JAVASCRIPT(Arrays.asList("javascript", "js")),
    HAS_EXP(Arrays.asList("has exp", "hasexp", "exp")),
    DOES_NOT_HAVE_EXP(Arrays.asList("!has exp", "!hasexp", "!exp")),
    STRING_CONTAINS(Arrays.asList("string contains", "stringcontains", "contains")),
    STRING_DOES_NOT_CONTAIN(Arrays.asList("!string contains", "!stringcontains", "!contains")),
    STRING_EQUALS(Arrays.asList("string equals", "stringequals", "equals")),
    STRING_DOES_NOT_EQUAL(Arrays.asList("!string equals", "!stringequals", "!equals")),
    STRING_EQUALS_IGNORECASE(Arrays.asList("stringequalsignorecase", "string equals ignorecase", "equalsignorecase")),
    STRING_DOES_NOT_EQUAL_IGNORECASE(Arrays.asList("!stringequalsignorecase", "!string equals ignorecase", "!equalsignorecase")),
    GREATER_THAN(Arrays.asList(">", "greater than", "greaterthan")),
    GREATER_THAN_EQUAL_TO(Arrays.asList(">=", "greater than or equal to", "greaterthanorequalto")),
    EQUAL_TO(Arrays.asList("==", "equal to", "equalto")),
    LESS_THAN_EQUAL_TO(Arrays.asList("<=", "less than or equal to", "lessthanorequalto")),
    LESS_THAN(Arrays.asList("<", "less than", "lessthan")),
    REGEX_MATCHES(Arrays.asList("regex matches", "regex")),
    REGEX_DOES_NOT_MATCH(Arrays.asList("!regex matches", "!regex"));

    private final List<String> identifier;
    RequirementType(List<String> identifier) {
        this.identifier = identifier;
    }

    public static RequirementType getType(String s) {
        for (RequirementType type : RequirementType.values()) {
            for (String id : type.getIdentifiers()) {
                if (!s.equalsIgnoreCase(id)) continue;
                return type;
            }
        }
        return null;
    }

    public List<String> getIdentifiers() {
        return this.identifier;
    }

}
