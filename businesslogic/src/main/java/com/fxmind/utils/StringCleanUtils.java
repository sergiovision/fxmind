package com.fxmind.utils;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class StringCleanUtils {

    public static boolean stringIsNullOrEmpty(String string) {
        return (string == null) || (string.length() == 0);
    }

	public static String multiClean(String string) {
		return string.replaceAll("(\\s?_+\\s?)+", " ");
	}

    public static String cleanID3Tags(String string) {
        if (string == null)
            return new String();
        String stringWithCorrectApostrophe =  string.replaceAll("[´`]", "'");
        return multiClean(stringWithCorrectApostrophe).trim();
    }

    public static String replaceSpacesWithSingleOne(String string) {
        return string.replaceAll("(\\p{Space})+", " ");
    }

    public static String replacePunctuationSimbolsWithSpace(String string) {
        String result = string.replaceAll("\\p{Punct}", " ");
        return replaceSpacesWithSingleOne(result.trim());
    }

    public static String replaceWordsWithSpace(String string, String[] ignoringWords) {
        for (String word : ignoringWords) {
            string = string.replaceAll("(?i)\\b" + word + "\\b", " ");
        }
        return replaceSpacesWithSingleOne(string.trim());
    }

    public static String replaceWordsWithDigitWithSpace(String string, String[] ignoringWords, boolean saveIgnoringWordDigit) {
        String replacement = saveIgnoringWordDigit ? " $1" : " ";
        for (String word : ignoringWords) {
			/* remove word1, word 1, word 2 */
            string = string.replaceAll("(?i)\\b" + word + "\\s*(\\d+)\\b", replacement);
        }
        return replaceSpacesWithSingleOne(string.trim());
    }

    public static String [] separateWords(String string) {
        return stringIsNullOrEmpty(string) ? new String[]{} : string.split(" ");
    }


    private static Set<String> exceptions;
    private static final String DELIMITERS = " ,.!()[]:;/|<>\"";

    /**
     * Taken from org.apache.commons.lang.WordUtils.capitalize(String str)
     *
     * <p>Capitalizes all the delimiter separated words in a String.
     * Only the first letter of each word is changed.
     *
     * <p>The delimiters represent a set of characters understood to separate words.
     * The first string character and the first non-delimiter character after a
     * delimiter will be capitalized. </p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * Capitalization uses the Unicode title case, normally equivalent to
     * upper case.</p>
     *
     * @param str  the String to capitalize, may be null
     * @param delimiters  set of characters to determine capitalization, null means whitespace
     * @return capitalized String, <code>null</code> if null String input
     * @since 2.1
     */
    public static String capitalizeGeneric(String str, char... delimiters) {
        int delimLen = delimiters == null ? -1 : delimiters.length;
        if (str.length()==0 || delimLen == 0) {
            return str;
        }
        char[] buffer = str.toCharArray();
        boolean capitalizeNext = true;


        for (int i = 0; i < buffer.length; i++)
        {
            char ch = buffer[i];
            if (isDelimiter(ch, delimiters))
            {
                capitalizeNext = true;
            } else
            if (capitalizeNext)
            {
                buffer[i] = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
        }
        return new String(buffer);
    }

    /**
     *
     * Capitalization Rules for Song Titles
     * Taken from here: http://aitech.ac.jp/~ckelly/midi/help/caps.html
     *
     * 1. The first and last words are always capitalized, and all except the
     * words listed below are capitalized.
     *
     * 2. These are lower-case, unless they are the first word or last word.
     *
     * articles: a, an, the conjunctions: and, but, or, nor prepositions that
     * are less than five letters long: at, by, for, from, in, into, of, off,
     * on, onto, out, over, to, up, with as (only if it is followed by a noun)
     *
     * 3. Prepositions are sometimes capitalized.
     *
     * Prepositions are capitalized when they are the first or last word.
     * Prepositions that are part of two-word "phrasal verbs" (Come On, Hold On,
     * etc....) are capitalized. Prepositions that are over four letters long.
     * (across, after, among, beyond, ...) 4. These short words are capitalized.
     *
     * Some people occasionally forget to capitalize these. also, be, if, than,
     * that, thus, when as (if it is followed by a verb)
     *
     * @param str - input string
     * @param delimiters
     * @return capitalized string
     */
    public static String capitalizeMusic(String str) {
        if ( str.length()==0 ) {
            return str;
        }
        if (exceptions == null) {
            // Initialize exceptions in capitalization algorithm
            String[] array = { "a", "an", "the", // Articles
                    "and", "but", "or", "nor",  // Conjunctions
                    "at", "by", "for", "from", "in", "into", "of", "off", "on", "onto", "out", "over", "to", "up", "with", // short prepositions < 5 letters
                    "feat", "vs"};
            exceptions = new TreeSet<String>( String.CASE_INSENSITIVE_ORDER );
            Collections.addAll(exceptions, array);
        }
        StringTokenizer st = new StringTokenizer(str, DELIMITERS, true);
        StringTokenizer stWords = new StringTokenizer(str, DELIMITERS, false);
        int wordsCount = stWords.countTokens();
        String strToken = "";
        String strWord = "";
        String result = "";
        int i = 0;
        while (st.hasMoreTokens()) {
            strToken = st.nextToken();
            char firstChar = strToken.charAt(0);
            if (stWords.hasMoreTokens() && !isDelimiter(firstChar, DELIMITERS.toCharArray())) {
                strWord = stWords.nextToken();
                if (strWord.equals(strToken)) {
                    if (Character.isLetter(firstChar) && Character.isLowerCase(firstChar) &&
                            ((i == 0) || (i==(wordsCount-1)) || (exceptions.contains(strWord)==false)) )  	{
                        strWord = strWord.substring(0, 1).toUpperCase() + strWord.substring(1);
                        result = result + strWord;
                        continue;
                    }
                    i++;
                }
            }
            result = result + strToken;
        }
        return result;
    }

    /**
     * Is the character a delimiter.
     * @param ch  the character to check
     * @param delimiters  the delimiters
     * @return true if it is a delimiter
     */
    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch == delimiter) {
                return true;
            }
        }
        return false;
    }

    public static String SingleClean(String string) {
        return stringIsNullOrEmpty(string)? string : string.replaceAll("\\s+", " ").trim();
    }

}
