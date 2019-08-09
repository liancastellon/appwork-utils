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
 *     The intent is that the AppWork GmbH is able to provide  their utilities library for free to non-commercial projects whereas commercial usage is only permitted after obtaining a commercial license.
 *     These terms apply to all files that have the [The Product] License header (IN the file), a <filename>.license or <filename>.info (like mylib.jar.info) file that contains a reference to this license.
 *
 * === 3rd Party Licences ===
 *     Some parts of the [The Product] use or reference 3rd party libraries and classes. These parts may have different licensing conditions. Please check the *.license and *.info files of included libraries
 *     to ensure that they are compatible to your use-case. Further more, some *.java have their own license. In this case, they have their license terms in the java file header.
 *
 * === Definition: Commercial Usage ===
 *     If anybody or any organization is generating income (directly or indirectly) by using [The Product] or if there's any commercial interest or aspect in what you are doing, we consider this as a commercial usage.
 *     If your use-case is neither strictly private nor strictly educational, it is commercial. If you are unsure whether your use-case is commercial or not, consider it as commercial or contact as.
 * === Dual Licensing ===
 * === Commercial Usage ===
 *     If you want to use [The Product] in a commercial way (see definition above), you have to obtain a paid license from AppWork GmbH.
 *     Contact AppWork for further details: e-mail@appwork.org
 * === Non-Commercial Usage ===
 *     If there is no commercial usage (see definition above), you may use [The Product] under the terms of the
 *     "GNU Affero General Public License" (http://www.gnu.org/licenses/agpl-3.0.en.html).
 *
 *     If the AGPL does not fit your needs, please contact us. We'll find a solution.
 * ====================================================================================================================================================
 * ==================================================================================================================================================== */
package org.appwork.utils.net.httpconnection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.appwork.utils.StringUtils;

/**
 * @author daniel
 * @date Aug 9, 2019
 *
 */
public class SSLSocketStreamOptions {

    protected final AtomicBoolean trustAll   = new AtomicBoolean(false);
    protected final AtomicBoolean sniEnabled = new AtomicBoolean(true);
    protected final AtomicBoolean valid      = new AtomicBoolean(true);

    public boolean isValid() {
        return valid.get();
    }

    public boolean isSNIEnabled() {
        return sniEnabled.get();
    }

    protected final Set<String> disabledCipherSuites = new CopyOnWriteArraySet<String>();

    public SSLSocketStreamOptions() {
        this(true);
    }

    public SSLSocketStreamOptions(boolean isTrustAll) {
        this(true, Arrays.asList(new String[] { "GCM" })); // https://stackoverflow.com/questions/25992131/slow-aes-gcm-encryption-and-decryption-with-java-8u20/27028067#27028067
    }

    public SSLSocketStreamOptions(boolean isTrustAll, List<String> disabledCipherSuites) {
        this.trustAll.set(isTrustAll);
        if (disabledCipherSuites != null) {
            this.disabledCipherSuites.addAll(disabledCipherSuites);
        }
    }

    public Set<String> getDisabledCipherSuites() {
        return disabledCipherSuites;
    }

    public boolean isTrustAll() {
        return trustAll.get();
    }

    public boolean retry(Exception e) {
        if (valid.get()) {
            if (e.getMessage().contains("unrecognized_name")) {
                if (sniEnabled.compareAndSet(true, false)) {
                    return true;
                }
            }
            if (StringUtils.contains(e.getMessage(), "handshake_failure")) {
                // enable all GCM cipherSuites
                boolean retry = false;
                final Iterator<String> it = disabledCipherSuites.iterator();
                while (it.hasNext()) {
                    final String next = it.next();
                    if (StringUtils.containsIgnoreCase(next, "GCM") && disabledCipherSuites.remove(next)) {
                        retry = true;
                    }
                }
                if (retry) {
                    return true;
                }
            }
            if (StringUtils.contains(e.getMessage(), "Could not generate DH keypair")) {
                // disable all DHE,ECDHE cipherSuites
                if (disabledCipherSuites.addAll(Arrays.asList(new String[] { "_DHE", "_ECDHE" }))) {
                    return true;
                }
            }
        }
        valid.set(false);
        return false;
    }
}
