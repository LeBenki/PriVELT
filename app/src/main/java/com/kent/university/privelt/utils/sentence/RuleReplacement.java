package com.kent.university.privelt.utils.sentence;

import lombok.Data;

@Data
public class RuleReplacement {
    private final String ending;
    private final String replacement;
    private final Boolean adding;

    public String apply(String word) {
        return this.adding ? word + replacement : replacement;
    }
}
