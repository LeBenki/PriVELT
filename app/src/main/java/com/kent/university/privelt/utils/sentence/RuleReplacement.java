/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
