package org.manlier.srapp.thesaurus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SynonymsConvertor {

    private static final String WORD_DELIMITER = ",";

    public static Set<String> parseToSet(String synonymStr) {
        return new HashSet<>(Arrays.asList(synonymStr.split(WORD_DELIMITER)));
    }

    public static String parseToString(Set<String> synonyms) {
        return String.join(WORD_DELIMITER, synonyms);
    }
}
