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
package org.appwork.utils.encoding;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author daniel
 *
 */
public class URLEncode {
    private static final String RFC2396CHARS = "0123456789" + "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "-_.!~*'()";

    public static String encodeRFC2396(final String input) throws UnsupportedEncodingException {
        return encodeRFC2396((CharSequence) input);
    }

    /* http://www.ietf.org/rfc/rfc2396.txt */
    public static String encodeRFC2396(final CharSequence input) throws UnsupportedEncodingException {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final char ch = input.charAt(i);
            if (ch == ' ') {
                sb.append("+");
            } else if (URLEncode.RFC2396CHARS.indexOf(ch) != -1) {
                sb.append(ch);
            } else {
                if (ch > 255) {
                    /* not allowed, replaced by + */
                    sb.append("+");
                } else {
                    /* hex formatted */
                    try {
                        sb.append(URLEncoder.encode("" + ch, "UTF-8"));
                    } catch (UnsupportedEncodingException ignore) {
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String decodeURIComponent(final String input) {
        try {
            return decodeURIComponent(input, "UTF-8", true);
        } catch (UnsupportedEncodingException ignore) {
        } catch (IllegalArgumentException ignore) {
        }
        return null;
    }

    public static String decodeURIComponent(final String input, final String charSet, final boolean ignoreDecodeError) throws UnsupportedEncodingException, IllegalArgumentException {
        return decodeURIComponent((CharSequence) input, charSet, ignoreDecodeError);
    }

    public static String decodeURIComponent(final CharSequence input, final String charSet, final boolean ignoreDecodeError) throws UnsupportedEncodingException, IllegalArgumentException {
        if (input == null) {
            return null;
        } else {
            final StringBuilder ret = new StringBuilder();
            final StringBuilder decode = new StringBuilder("");
            int nextStep = 0;
            for (int i = 0; i < input.length(); i++) {
                final char ch = input.charAt(i);
                if (ch == '%') {
                    decode.append(ch);
                    nextStep = 1;
                } else {
                    switch (nextStep) {
                    case 1:
                    case 2:
                        decode.append(ch);
                        nextStep++;
                        break;
                    default:
                        if (decode.length() > 0) {
                            nextStep = 0;
                            try {
                                ret.append(URLDecoder.decode(decode.toString(), charSet));
                            } catch (IllegalArgumentException e) {
                                if (ignoreDecodeError) {
                                    ret.append(decode);
                                } else {
                                    throw e;
                                }
                            }
                            decode.delete(0, decode.length());
                        }
                        ret.append(ch);
                        break;
                    }
                }
            }
            if (decode.length() > 0) {
                try {
                    ret.append(URLDecoder.decode(decode.toString(), charSet));
                } catch (IllegalArgumentException e) {
                    if (ignoreDecodeError) {
                        ret.append(decode);
                    } else {
                        throw e;
                    }
                }
            }
            return ret.toString();
        }
    }

    public static String encodeURIComponent(final CharSequence input) {
        if (input == null) {
            return null;
        } else {
            final StringBuilder sb = new StringBuilder();
            final StringBuilder encode = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                final char ch = input.charAt(i);
                if (ch == ' ') {
                    if (encode.length() > 0) {
                        try {
                            sb.append(URLEncoder.encode(encode.toString(), "UTF-8"));
                        } catch (UnsupportedEncodingException ignore) {
                        }
                        encode.delete(0, encode.length());
                    }
                    sb.append("%20");
                } else if (URLEncode.RFC2396CHARS.indexOf(ch) != -1) {
                    if (encode.length() > 0) {
                        try {
                            sb.append(URLEncoder.encode(encode.toString(), "UTF-8"));
                        } catch (UnsupportedEncodingException ignore) {
                        }
                        encode.delete(0, encode.length());
                    }
                    sb.append(ch);
                } else {
                    encode.append(ch);
                }
            }
            if (encode.length() > 0) {
                try {
                    sb.append(URLEncoder.encode(encode.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException ignore) {
                }
            }
            return sb.toString();
        }
    }

    public static String encodeURIComponent(final String input) {
        return encodeURIComponent((CharSequence) input);
    }
}
