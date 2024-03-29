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
package org.appwork.utils.net.httpconnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.appwork.utils.Application;
import org.appwork.utils.JVMVersion;
import org.appwork.utils.Regex;
import org.appwork.utils.StringUtils;

/**
 * @author daniel
 *
 */
public class JavaSSLSocketStreamFactory implements SSLSocketStreamFactory {
    private static final JavaSSLSocketStreamFactory INSTANCE = new JavaSSLSocketStreamFactory();

    public static final JavaSSLSocketStreamFactory getInstance() {
        return INSTANCE;
    }

    public static SSLSocketFactory getSSLSocketFactory(final boolean useSSLTrustAll) throws IOException {
        return getSSLSocketFactory(useSSLTrustAll, null);
    }

    /*
     * https://risdenk.github.io/2018/03/26/oracle-jdk-missing-ciphers-libsunec.so.html
     *
     *
     * TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
     * TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA
     */
    public static void isCipherSuiteSupported(final String... cipherSuites) throws SSLException {
        try {
            final SSLContext context = SSLContext.getDefault();
            final SSLParameters parameters = context.getSupportedSSLParameters();
            final Set<String> supportedCipherSuites = new HashSet<String>(Arrays.asList(parameters.getCipherSuites()));
            for (final String cipherSuite : cipherSuites) {
                if (!supportedCipherSuites.contains(cipherSuite)) {
                    throw new SSLException(cipherSuite + " is unsupported!");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new SSLException(e);
        } catch (RuntimeException e) {
            throw new SSLException(e);
        }
    }

    public static SSLSocketFactory getSSLSocketFactory(final boolean useSSLTrustAll, final Set<String> disabledCipherSuites) throws IOException {
        final SSLSocketFactory factory;
        if (useSSLTrustAll) {
            factory = TrustALLSSLFactory.getSSLFactoryTrustALL();
        } else {
            factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
        return new SSLSocketFactory() {
            /**
             * remove SSL because of POODLE Vulnerability
             *
             * https://www.us-cert.gov/ncas/alerts/TA14-290A
             *
             * @param socket
             */
            private Socket removeSSLProtocol(final Socket socket) {
                if (socket != null && socket instanceof SSLSocket) {
                    final SSLSocket sslSocket = (SSLSocket) socket;
                    final long javaVersion = Application.getJavaVersion();
                    if (javaVersion >= JVMVersion.JAVA_11) {
                        sslSocket.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" });
                    } else if (javaVersion >= JVMVersion.JAVA18) {
                        sslSocket.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" });
                    } else if (javaVersion >= JVMVersion.JAVA17) {
                        sslSocket.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" });
                    } else {
                        sslSocket.setEnabledProtocols(new String[] { "TLSv1" });
                    }
                }
                return socket;
            }

            private Socket disableCipherSuit(final Socket socket) {
                if (socket != null && socket instanceof SSLSocket) {
                    if (disabledCipherSuites != null) {
                        final SSLSocket sslSocket = (SSLSocket) socket;
                        final ArrayList<String> cipherSuits = new ArrayList<String>(Arrays.asList(sslSocket.getEnabledCipherSuites()));
                        final Iterator<String> it = cipherSuits.iterator();
                        boolean updateCipherSuites = false;
                        cipher: while (it.hasNext()) {
                            final String next = it.next();
                            if (disabledCipherSuites != null) {
                                for (final String cipherBlacklistEntry : disabledCipherSuites) {
                                    if (StringUtils.containsIgnoreCase(next, cipherBlacklistEntry)) {
                                        it.remove();
                                        updateCipherSuites = true;
                                        continue cipher;
                                    }
                                }
                            }
                        }
                        if (updateCipherSuites) {
                            sslSocket.setEnabledCipherSuites(cipherSuits.toArray(new String[0]));
                        }
                    }
                }
                return socket;
            }

            @Override
            public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
                return disableCipherSuit(this.removeSSLProtocol(factory.createSocket(arg0, arg1, arg2, arg3)));
            }

            @Override
            public String[] getDefaultCipherSuites() {
                return factory.getDefaultCipherSuites();
            }

            @Override
            public String[] getSupportedCipherSuites() {
                return factory.getSupportedCipherSuites();
            }

            @Override
            public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
                return disableCipherSuit(this.removeSSLProtocol(factory.createSocket(arg0, arg1)));
            }

            @Override
            public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
                return disableCipherSuit(this.removeSSLProtocol(factory.createSocket(arg0, arg1)));
            }

            @Override
            public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
                return disableCipherSuit(this.removeSSLProtocol(factory.createSocket(arg0, arg1, arg2, arg3)));
            }

            @Override
            public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
                return disableCipherSuit(this.removeSSLProtocol(factory.createSocket(arg0, arg1, arg2, arg3)));
            }

        };
    }

    protected void verifySSLHostname(final SSLSocket sslSocket, final String host, final boolean trustAll) throws IOException {
        if (!trustAll) {
            final SSLSession sslSession = sslSocket.getSession();
            if (sslSession != null && sslSession.getPeerCertificates().length > 0) {
                final Certificate certificate = sslSession.getPeerCertificates()[0];
                if (certificate instanceof X509Certificate) {
                    final String hostname = host.toLowerCase(Locale.ENGLISH);
                    final ArrayList<String> subjects = new ArrayList<String>();
                    final X509Certificate x509 = (X509Certificate) certificate;
                    subjects.add(new Regex(x509.getSubjectX500Principal().getName(), "CN=(.*?)(,| |$)").getMatch(0));
                    try {
                        final Collection<List<?>> subjectAlternativeNames = x509.getSubjectAlternativeNames();
                        if (subjectAlternativeNames != null) {
                            for (final List<?> subjectAlternativeName : subjectAlternativeNames) {
                                final Integer generalNameType = (Integer) subjectAlternativeName.get(0);
                                switch (generalNameType) {
                                case 1:// rfc822Name
                                case 2:// dNSName
                                    subjects.add(subjectAlternativeName.get(1).toString());
                                    break;
                                }
                            }
                        }
                    } catch (CertificateParsingException e) {
                        e.printStackTrace();
                    }
                    for (String subject : subjects) {
                        if (subject != null) {
                            subject = subject.toLowerCase(Locale.ENGLISH);
                            if (StringUtils.equals(subject, hostname)) {
                                return;
                            } else if (subject.startsWith("*.") && hostname.length() > subject.length() - 1 && hostname.endsWith(subject.substring(1)) && hostname.substring(0, hostname.length() - subject.length() + 1).indexOf('.') < 0) {
                                /**
                                 * http://en.wikipedia.org/wiki/ Wildcard_certificate
                                 */
                                return;
                            }
                        }
                    }
                    throw new SSLHandshakeException("HTTPS hostname wrong:  hostname is <" + hostname + ">");
                }
            }
        }
    }

    @Override
    public SSLSocketStreamInterface create(final SocketStreamInterface socketStream, final String host, final int port, final boolean autoClose, SSLSocketStreamOptions options) throws IOException {
        final boolean isTrustAll = (options == null || options.isTrustAll());
        final Set<String> disabledCipherSuites = options != null ? options.getDisabledCipherSuites() : null;
        final boolean sniEnabled = !StringUtils.isEmpty(host) && (options == null || options.isSNIEnabled());
        final SSLSocket sslSocket = (SSLSocket) getSSLSocketFactory(isTrustAll, disabledCipherSuites).createSocket(socketStream.getSocket(), sniEnabled ? host : "", port, autoClose);
        if (!sniEnabled) {
            final SSLParameters sslParams = sslSocket.getSSLParameters();
            sslParams.setServerNames(new ArrayList<SNIServerName>(0));
            sslSocket.setSSLParameters(sslParams);
        }
        return new SSLSocketStreamInterface() {
            @Override
            public Socket getSocket() {
                return sslSocket;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return sslSocket.getOutputStream();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return sslSocket.getInputStream();
            }

            @Override
            public void close() throws IOException {
                sslSocket.close();
            }

            @Override
            public String getCipherSuite() {
                return "JVM:" + sslSocket.getSession().getCipherSuite();
            }
        };
    }
}
