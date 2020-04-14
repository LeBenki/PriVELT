/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils.sentence

import lombok.AllArgsConstructor

@AllArgsConstructor
enum class RuleEnum(private val ruleReplacements: Array<RuleReplacement>) {
    REGULAR(arrayOf<RuleReplacement>(
            RuleReplacement("", "s", true)
    )),
    SINGULAR(arrayOf<RuleReplacement>(
            RuleReplacement("ss", "es", true),
            RuleReplacement("s", "es", true),
            RuleReplacement("h", "es", true),
            RuleReplacement("x", "es", true),
            RuleReplacement("z", "es", true),
            RuleReplacement("y", "ies", true),
            RuleReplacement("o", "oes", true),
            RuleReplacement("is", "es", true))),
    EXCEPTION(arrayOf<RuleReplacement>(
            RuleReplacement("fez", "zes", true),
            RuleReplacement("gas", "ses", true),
            RuleReplacement("wife", "wives", false),
            RuleReplacement("wolf", "wolves", false),
            RuleReplacement("roof", "s", true),
            RuleReplacement("belief", "s", true),
            RuleReplacement("chef", "s", true),
            RuleReplacement("ray", "s", true),
            RuleReplacement("boy", "s", true),
            RuleReplacement("photo", "s", true),
            RuleReplacement("piano", "s", true),
            RuleReplacement("halo", "s", true),
            RuleReplacement("piano", "s", true),
            RuleReplacement("phenomenon", "phenomena", false),
            RuleReplacement("criterion", "criteria", false),
            RuleReplacement("child", "ren", true),
            RuleReplacement("goose", "geese", false),
            RuleReplacement("man", "men", false),
            RuleReplacement("woman", "women", false),
            RuleReplacement("tooth", "teeth", false),
            RuleReplacement("foot", "feet", false),
            RuleReplacement("mouse", "mice", false),
            RuleReplacement("person", "people", false))),
    NO_CHANGES(arrayOf<RuleReplacement>(
            RuleReplacement("sheep", "sheep", false),
            RuleReplacement("series", "series", false),
            RuleReplacement("species", "sheep", false),
            RuleReplacement("deer", "deer", false)));

    companion object {
        private fun findRuleReplacement(word: String, ruleEnum: RuleEnum): RuleReplacement? {
            for (ruleReplacement in ruleEnum.ruleReplacements) if (word.endsWith(ruleReplacement.ending)) return ruleReplacement
            return null
        }

        @JvmStatic
        fun findRuleAdapter(word: String): RuleReplacement {
            var ruleReplacement = findRuleReplacement(word, NO_CHANGES)
            if (ruleReplacement != null) return ruleReplacement
            ruleReplacement = findRuleReplacement(word, EXCEPTION)
            if (ruleReplacement != null) return ruleReplacement
            ruleReplacement = findRuleReplacement(word, SINGULAR)
            return ruleReplacement ?: REGULAR.ruleReplacements[0]
        }
    }

}