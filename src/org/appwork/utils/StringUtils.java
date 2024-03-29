/**
 *
 * ====================================================================================================================================================
 *         "AppWork Utilities" License
 *         The "AppWork Utilities" will be called [The Product] from now on.
 * ====================================================================================================================================================
 *         Copyright (c) 2009-2015, AppWork GmbH <e-mail@appwork.org>
 *         Schwabacher Straße 117
 *         90763 Fürth
 *         Germany
 * === Preamble ===
 *     This license establishes the terms under which the [The Product] Source Code & Binary files may be used, copied, modified, distributed, and/or redistributed.
 *     The intent is that the AppWork GmbH is able to provide their utilities library for free to non-commercial projects whereas commercial usage is only permitted after obtaining a commercial license.
 *     These terms apply to all files that have the [The Product] License header (IN the file), a <filename>.license or <filename>.info (like mylib.jar.info) file that contains a reference to this license.
 *
 * === 3rd Party Licences ===
 *     Some parts of the [The Product] use or reference 3rd party libraries and classes. These parts may have different licensing conditions. Please check the *.license and *.info files of included libraries
 *     to ensure that they are compatible to your use-case. Further more, some *.java have their own license. In this case, they have their license terms in the java file header.
 *
 * === Definition: Commercial Usage ===
 *     If anybody or any organization is generating income (directly or indirectly) by using [The Product] or if there's any commercial interest or aspect in what you are doing, we consider this as a commercial usage.
 *     If your use-case is neither strictly private nor strictly educational, it is commercial. If you are unsure whether your use-case is commercial or not, consider it as commercial or contact us.
 * === Dual Licensing ===
 * === Commercial Usage ===
 *     If you want to use [The Product] in a commercial way (see definition above), you have to obtain a paid license from AppWork GmbH.
 *     Contact AppWork for further details: <e-mail@appwork.org>
 * === Non-Commercial Usage ===
 *     If there is no commercial usage (see definition above), you may use [The Product] under the terms of the
 *     "GNU Affero General Public License" (http://www.gnu.org/licenses/agpl-3.0.en.html).
 *
 *     If the AGPL does not fit your needs, please contact us. We'll find a solution.
 * ====================================================================================================================================================
 * ==================================================================================================================================================== */
package org.appwork.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class StringUtils {
    public static boolean contains(final String input, final String contains) {
        if (StringUtils.isEmpty(input) || StringUtils.isEmpty(contains)) {
            return false;
        }
        return input.contains(contains);
    }

    /**
     * WARNING: calls String.trim on each line!
     *
     * @param arg
     * @return
     */
    public static String[] getLines(final String arg) {
        if (arg == null) {
            return new String[] {};
        } else {
            final String[] splits = arg.split("(\r\n|\r|\n)");
            final ArrayList<String> ret = new ArrayList<String>(splits.length);
            for (final String split : splits) {
                ret.add(split.trim());
            }
            return ret.toArray(new String[0]);
        }
    }

    /**
     * @param name
     * @param jdPkgRule
     * @return
     */
    public static boolean endsWithCaseInsensitive(final String name, final String jdPkgRule) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(jdPkgRule)) {
            return false;
        }
        return name.toLowerCase(Locale.ENGLISH).endsWith(jdPkgRule.toLowerCase(Locale.ENGLISH));
    }

    public static boolean startsWithCaseInsensitive(final String name, final String jdPkgRule) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(jdPkgRule)) {
            return false;
        }
        return name.toLowerCase(Locale.ENGLISH).startsWith(jdPkgRule.toLowerCase(Locale.ENGLISH));
    }

    public static boolean containsIgnoreCase(String input, String contains) {
        if (input == null || contains == null) {
            return false;
        }
        return input.toLowerCase(Locale.ENGLISH).contains(contains.toLowerCase(Locale.ENGLISH));
    }

    /**
     * taken from http://stackoverflow.com/questions/4731055/whitespace-matching-regex-java
     */
    final private static String whitespace_chars = "[" /*
                                                        * dummy empty string for homogeneity
                                                        */
            + "\\u0009" // CHARACTER
            // TABULATION
            + "\\u000A" // LINE
            // FEED
            // (LF)
            + "\\u000B" // LINE
            // TABULATION
            + "\\u000C" // FORM
            // FEED
            // (FF)
            + "\\u000D" // CARRIAGE
            // RETURN
            // (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT
            // LINE
            // (NEL)
            + "\\u00A0" // NO-BREAK
            // SPACE
            + "\\u1680" // OGHAM
            // SPACE
            // MARK
            + "\\u180E" // MONGOLIAN
            // VOWEL
            // SEPARATOR
            + "\\u2000" // EN QUAD
            + "\\u2001" // EM QUAD
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM
            // SPACE
            + "\\u2005" // FOUR-PER-EM
            // SPACE
            + "\\u2006" // SIX-PER-EM
            // SPACE
            + "\\u2007" // FIGURE
            // SPACE
            + "\\u2008" // PUNCTUATION
            // SPACE
            + "\\u2009" // THIN
            // SPACE
            + "\\u200A" // HAIR
            // SPACE
            + "\\u2028" // LINE
            // SEPARATOR
            + "\\u2029" // PARAGRAPH
            // SEPARATOR
            + "\\u202F" // NARROW
            // NO-BREAK
            // SPACE
            + "\\u205F" // MEDIUM
            // MATHEMATICAL
            // SPACE
            + "\\u3000" // IDEOGRAPHIC
            // SPACE
            + "]";

    public static String trim(String input) {
        if (input != null) {
            return removeBOM(input.replaceAll("^" + StringUtils.whitespace_chars + "+", "").replaceAll(StringUtils.whitespace_chars + "+$", ""));
        }
        return null;
    }

    public static String removeBOM(final String input) {
        // Strings in Java are UTF-16 BE, BOM
        if (input != null && input.startsWith("\uFEFF")) {
            return input.substring(1);
        } else {
            return input;
        }
    }

    public static String nullOrNonEmpty(String x) {
        if (StringUtils.isNotEmpty(x)) {
            return x;
        } else {
            return null;
        }
    }

    public static String valueOrEmpty(String x) {
        if (x == null) {
            return "";
        } else {
            return x;
        }
    }

    public static String nullify(String string) {
        if (string == null || "null".equals(string)) {
            return null;
        } else {
            return string;
        }
    }

    /**
     * @param x
     * @param y
     * @return
     */
    public static boolean equals(final String x, final String y) {
        if (x == y) {
            return true;
        } else if (x == null && y != null) {
            return false;
        } else if (y == null && x != null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    /**
     * @param pass
     * @param pass2
     * @return
     */
    public static boolean equalsIgnoreCase(final String pass, final String pass2) {
        if (pass == pass2) {
            return true;
        } else {
            return pass != null && pass.equalsIgnoreCase(pass2);
        }
    }

    private static String EMPTY_SPACE_STRING = "                                                                                                                                                                                                                                                             ";

    public static String fillPre(final String string, final String filler, final int minCount) {
        if (string.length() >= minCount) {
            return string;
        }
        final StringBuilder sb = new StringBuilder(minCount);
        int missing = minCount - string.length();
        // the EMPTY_SPACE_STRING approach was 30% faster in my usercase (SimpleFormater(
        if (" ".equals(filler) && missing <= EMPTY_SPACE_STRING.length()) {
            sb.append(EMPTY_SPACE_STRING.substring(0, missing));
            sb.append(string);
        } else {
            missing = missing / filler.length() + (missing % filler.length() == 0 ? 0 : 1);
            for (int i = 0; i < missing; i++) {
                sb.append(filler);
            }
            sb.append(string);
        }
        return sb.toString();
    }

    public static String fillPost(final String string, final String filler, final int minCount) {
        if (string.length() >= minCount) {
            return string;
        }
        final StringBuilder sb = new StringBuilder(minCount);
        int missing = minCount - string.length();
        if (" ".equals(filler) && missing <= EMPTY_SPACE_STRING.length()) {
            sb.append(string);
            sb.append(EMPTY_SPACE_STRING.substring(0, missing));
            return sb.toString();
        } else {
            sb.append(string);
            while (sb.length() < minCount) {
                sb.append(filler);
            }
            return sb.toString();
        }
    }

    /**
     * @param sameSource
     * @param sourceUrl
     * @return
     */
    public static String getCommonalities(final String a, final String b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        int i = 0;
        int max = Math.min(a.length(), b.length());
        for (i = 0; i < max; i++) {
            if (a.charAt(i) != b.charAt(i)) {
                return a.substring(0, i);
            }
        }
        return a.substring(0, i);
    }

    // Keep for compatibility in webinstaller!
    public final static boolean isEmpty(final String ip) {
        return ip == null || ip.length() == 0 || ip.trim().length() == 0;
    }

    // Keep for compatibility in webinstaller!
    public final static boolean isNotEmpty(final String value) {
        return !StringUtils.isEmpty(value);
    }

    /**
     * Returns wether a String is null,empty, or contains whitespace only
     *
     * @param ip
     * @return
     */
    public final static boolean isAllEmpty(final String... values) {
        if (values != null) {
            for (final String value : values) {
                if (value != null && value.trim().length() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param value
     * @return
     */
    public final static boolean isAllNotEmpty(final String... values) {
        if (values != null) {
            for (final String value : values) {
                if (StringUtils.isEmpty(value)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param parameters
     * @param string
     * @return
     */
    public static String join(Object[] parameters, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object s : parameters) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(String.valueOf(s));
        }
        return sb.toString();
    }

    public static String join(String separator, Object... parameters) {
        StringBuilder sb = new StringBuilder();
        for (Object s : parameters) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(String.valueOf(s));
        }
        return sb.toString();
    }

    /**
     * @param signParams
     * @param separator
     * @return
     */
    public static String join(List<? extends Object> params, String separator) {
        return join(params.toArray(new Object[] {}), separator);
    }

    /**
     * @param fcgiPorts
     * @param separator
     * @return
     */
    public static String join(String separator, int... ints) {
        StringBuilder sb = new StringBuilder();
        for (Object s : ints) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(String.valueOf(s));
        }
        return sb.toString();
    }

    /**
     * @param string
     * @param length
     * @return returns a list of the given length and each entry contains string
     */
    public static List<String> createList(String string, int length) {
        ArrayList<String> ret = new ArrayList<String>();
        while (length-- > 0) {
            ret.add(string);
        }
        return ret;
    }

    /**
     * a wrapper around String.replaceAll with nullcheck on the actual text
     *
     * @param string
     * @param string2
     * @param changeLogText
     * @return
     */
    public static String replaceAllByRegex(String regex, String replacement, String text) {
        if (text == null) {
            return text;
        } else {
            return text.replaceAll(regex, replacement);
        }
    }

    /**
     * @param defaultMessage
     * @param i
     * @param string
     * @return
     */
    public static String abr(String defaultMessage, int max, String postfox) {
        if (defaultMessage == null) {
            return null;
        }
        if (defaultMessage.length() <= max) {
            return defaultMessage;
        }
        return defaultMessage.substring(0, max) + postfox;
    }

    /**
     * @param name
     * @return
     */
    public static String toCamelCase(String name, boolean firstUpper) {
        StringBuilder sb = new StringBuilder();
        for (String p : name.split("[\\_\\-\\s]+")) {
            sb.append(p.substring(0, 1).toUpperCase(Locale.ENGLISH) + p.substring(1));
        }
        if (firstUpper) {
            return sb.substring(0, 1).toUpperCase(Locale.ENGLISH) + sb.substring(1);
        } else {
            return sb.substring(0, 1).toLowerCase(Locale.ENGLISH) + sb.substring(1);
        }
    }

    public static String shareAtLeastOne(String[] xArray, String[] yArray, final boolean caseInsensitive) {
        if (xArray == null || yArray == null || xArray.length == 0 || yArray.length == 0) {
            return null;
        }
        final HashSet<String> set = new HashSet<String>();
        for (final String x : xArray) {
            if (caseInsensitive) {
                set.add(x.toLowerCase(Locale.ENGLISH));
            } else {
                set.add(x);
            }
        }
        for (final String y : yArray) {
            final boolean contains;
            if (caseInsensitive) {
                contains = set.contains(y.toLowerCase(Locale.ENGLISH));
            } else {
                contains = set.contains(y);
            }
            if (contains) {
                return y;
            }
        }
        return null;
    }

    /**
     * @param value
     * @param the
     *            split result without empty entries. Returns an empty array for null input
     * @return
     */
    public static String[] splitNoEmpty(String value, String delim) {
        if (value == null) {
            return new String[] {};
        } else {
            final String splits[] = value.split(delim);
            final ArrayList<String> ret = new ArrayList<String>(splits.length);
            for (final String split : splits) {
                if (StringUtils.isNotEmpty(split)) {
                    ret.add(split);
                }
            }
            return ret.toArray(new String[0]);
        }
    }
}
