/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.sentence;

public class SentenceAdapter {

    private static String delimiter = "@plural";

    private static String handlePlural(String word) {
        if (!word.contains(delimiter)) return word;

        String[] pluralParameters = word.substring(word.indexOf("(") + 1, word.indexOf(")")).split(",");
        int number = Integer.parseInt(pluralParameters[0]);
        String wordToUse = pluralParameters[1];

        if (number == 1) return wordToUse;

        return RuleEnum.findRuleAdapter(wordToUse).apply(wordToUse);
    }

    private static String checkWords(String[] words) {
        for (int i = 0; i < words.length; i ++) words[i] = handlePlural(words[i]);

        return convertArrayToStringMethod(words);
    }

    public static String adapt(String sentence, Object ... objects) {
        sentence = String.format(sentence, objects);

        if (!sentence.contains(delimiter))
            return sentence;

        return checkWords(sentence.split(" "));
    }

    private static String convertArrayToStringMethod(String[] strArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strArray.length; i++) {
            stringBuilder.append(strArray[i]);
            if (i + 1 < strArray.length)
                stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public static String capitaliseFirstLetter(String str) {
        if (str == null)
            return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
