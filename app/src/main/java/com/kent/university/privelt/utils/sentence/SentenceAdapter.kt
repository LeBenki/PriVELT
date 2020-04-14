/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.kent.university.privelt.utils.sentence

import com.kent.university.privelt.utils.sentence.RuleEnum.Companion.findRuleAdapter
import java.util.*

object SentenceAdapter {
    private const val delimiter = "@plural"
    private fun handlePlural(word: String): String {
        if (!word.contains(delimiter)) return word
        val pluralParameters = word.substring(word.indexOf("(") + 1, word.indexOf(")")).split(",").toTypedArray()
        val number = pluralParameters[0].toInt()
        val wordToUse = pluralParameters[1]
        return if (number == 1) wordToUse else findRuleAdapter(wordToUse).apply(wordToUse)
    }

    private fun checkWords(words: Array<String>): String {
        for (i in words.indices) words[i] = handlePlural(words[i])
        return convertArrayToStringMethod(words)
    }

    fun adapt(sentence: String, vararg objects: Any?): String {
        var sentence = sentence
        sentence = String.format(sentence, *objects)
        return if (!sentence.contains(delimiter)) sentence else checkWords(sentence.split(" ").toTypedArray())
    }

    private fun convertArrayToStringMethod(strArray: Array<String>): String {
        val stringBuilder = StringBuilder()
        for (i in strArray.indices) {
            stringBuilder.append(strArray[i])
            if (i + 1 < strArray.size) stringBuilder.append(" ")
        }
        return stringBuilder.toString()
    }

    fun capitaliseFirstLetter(str: String?): String {
        return if (str == null) "" else str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1)
    }
}