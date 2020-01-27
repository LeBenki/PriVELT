package com.kent.university.privelt.utils.sentence;

import android.util.Log;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RuleEnum {
    REGULAR(new RuleReplacement[]{
            new RuleReplacement("", "s", true)
    }), SINGULAR(new RuleReplacement[]{
            new RuleReplacement("ss", "es", true),
            new RuleReplacement("s", "es", true),
            new RuleReplacement("h", "es", true),
            new RuleReplacement("x", "es", true),
            new RuleReplacement("z", "es", true),
            new RuleReplacement("y", "ies", true),
            new RuleReplacement("o", "oes", true),
            new RuleReplacement("is", "es", true),
    }), EXCEPTION(new RuleReplacement[]{
            new RuleReplacement("fez", "zes", true),
            new RuleReplacement("gas", "ses", true),
            new RuleReplacement("wife", "wives", false),
            new RuleReplacement("wolf", "wolves", false),
            new RuleReplacement("roof", "s", true),
            new RuleReplacement("belief", "s", true),
            new RuleReplacement("chef", "s", true),
            new RuleReplacement("ray", "s", true),
            new RuleReplacement("boy", "s", true),
            new RuleReplacement("photo", "s", true),
            new RuleReplacement("piano", "s", true),
            new RuleReplacement("halo", "s", true),
            new RuleReplacement("piano", "s", true),
            new RuleReplacement("phenomenon", "phenomena", false),
            new RuleReplacement("criterion", "criteria", false),
            new RuleReplacement("child", "ren", true),
            new RuleReplacement("goose", "geese", false),
            new RuleReplacement("man", "men", false),
            new RuleReplacement("woman", "women", false),
            new RuleReplacement("tooth", "teeth", false),
            new RuleReplacement("foot", "feet", false),
            new RuleReplacement("mouse", "mice", false),
            new RuleReplacement("person", "people", false),
    }), NO_CHANGES(new RuleReplacement[]{
            new RuleReplacement("sheep", "sheep", false),
            new RuleReplacement("series", "series", false),
            new RuleReplacement("species", "sheep", false),
            new RuleReplacement("deer", "deer", false),
    });

    private final RuleReplacement[] ruleReplacements;

    public static RuleReplacement findRuleReplacement(String word, RuleEnum ruleEnum) {
        for (RuleReplacement ruleReplacement : ruleEnum.ruleReplacements)
            if (word.endsWith(ruleReplacement.getEnding()))
                return ruleReplacement;
        return null;
    }

    public static RuleReplacement findRuleAdapter(String word) {
        RuleReplacement ruleReplacement = findRuleReplacement(word, NO_CHANGES);
        if (ruleReplacement != null) return ruleReplacement;

        ruleReplacement = findRuleReplacement(word, EXCEPTION);
        if (ruleReplacement != null) return ruleReplacement;

        ruleReplacement = findRuleReplacement(word, SINGULAR);
        if (ruleReplacement != null) return ruleReplacement;

        return REGULAR.ruleReplacements[0];
    }
}
