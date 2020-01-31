package com.kent.university.privelt.utils.sentence;

import lombok.Data;

@Data
class RuleReplacement {
    private final String ending;
    private final String replacement;
    private final Boolean adding;

    RuleReplacement(String ending, String replacement, Boolean adding) {
        this.ending = ending;
        this.replacement = replacement;
        this.adding = adding;
    }

    String apply(String word) {
        return this.adding ? word + replacement : replacement;
    }

    String getEnding() {
        return ending;
    }
}
