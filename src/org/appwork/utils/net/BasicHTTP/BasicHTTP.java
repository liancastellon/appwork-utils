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
package org.appwork.utils.net.BasicHTTP;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.appwork.net.protocol.http.HTTPConstants;
import org.appwork.txtresource.TranslationFactory;
import org.appwork.utils.Application;
import org.appwork.utils.Exceptions;
import org.appwork.utils.Interruptible;
import org.appwork.utils.InterruptibleThread;
import org.appwork.utils.logging2.LogInterface;
import org.appwork.utils.net.ChunkedOutputStream;
import org.appwork.utils.net.DownloadProgress;
import org.appwork.utils.net.URLHelper;
import org.appwork.utils.net.UploadProgress;
import org.appwork.utils.net.httpconnection.HTTPConnection;
import org.appwork.utils.net.httpconnection.HTTPConnection.RequestMethod;
import org.appwork.utils.net.httpconnection.HTTPConnectionFactory;
import org.appwork.utils.net.httpconnection.HTTPProxy;

public class BasicHTTP implements Interruptible {
    public static void main(final String[] args) throws MalformedURLException, IOException, InterruptedException {
        final BasicHTTP client = new BasicHTTP();
        System.out.println(client.getPage(new URL("http://ipcheck0.jdownloader.org")));
    }

    private HashSet<Integer>              allowedResponseCodes;
    private final HashMap<String, String> requestHeader;
    protected volatile HTTPConnection     connection;
    private int                           connectTimeout = 15000;
    private int                           readTimeout    = 30000;
    private HTTPProxy                     proxy          = HTTPProxy.NONE;
    protected LogInterface                logger         = null;
    private final Object                  lock           = new Object();

    public BasicHTTP() {
        this.requestHeader = new HashMap<String, String>();
    }

    /**
     * @throws IOException
     *
     */
    protected void checkResponseCode() throws InvalidResponseCode {
        if (this.allowedResponseCodes != null && !this.allowedResponseCodes.contains(this.connection.getResponseCode())) {
            throw this.createInvalidResponseCodeException();
        }
    }

    public void clearRequestHeader() {
        this.requestHeader.clear();
    }

    /**
     * @return
     */
    protected InvalidResponseCode createInvalidResponseCodeException() {
        return new InvalidResponseCode(this.connection);
    }

    /**
     * @param url
     * @param progress
     * @param file
     * @throws InterruptedException
     * @throws IOException
     */
    public void download(final URL url, final DownloadProgress progress, final File file) throws BasicHTTPException, InterruptedException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, true);
            try {
                this.download(url, progress, 0, fos, file.length());
            } catch (final BasicHTTPException e) {
                throw e;
            } catch (final InterruptedException e) {
                throw e;
            } catch (final Exception e) {
                // we cannot say if read or write
                throw new BasicHTTPException(this.connection, e);
            }
        } catch (final FileNotFoundException e) {
            throw new BasicHTTPException(this.connection, new WriteIOException(e));
        } finally {
            try {
                fos.close();
            } catch (final Throwable t) {
            }
        }
    }

    public byte[] download(final URL url, final DownloadProgress progress, final long maxSize) throws BasicHTTPException, InterruptedException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            this.download(url, progress, maxSize, baos, -1);
        } catch (final BasicHTTPException e) {
            throw e;
        } catch (final InterruptedException e) {
            throw e;
        } finally {
            try {
                baos.close();
            } catch (final Throwable t) {
            }
        }
        return baos.toByteArray();
    }

    public void download(final URL url, final DownloadProgress progress, final long maxSize, final OutputStream baos, final long resumePosition) throws BasicHTTPException, InterruptedException {
        download(url, progress, maxSize, baos, resumePosition, System.currentTimeMillis() + (60 * 60 * 1000l));
    }

    @Override
    public void interrupt(Thread arg0) {
        final HTTPConnection con = connection;
        if (con != null) {
            try {
                con.disconnect();
            } catch (Throwable e) {
                if (this.logger != null) {
                    logger.log(e);
                }
            }
        }
    }

    /**
     *
     * Please do not forget to close the output stream.
     *
     * @param url
     * @param progress
     * @param maxSize
     * @param baos
     * @throws IOException
     * @throws InterruptedException
     */
    protected void download(final URL url, final DownloadProgress progress, final long maxSize, final OutputStream baos, final long resumePosition, final long redirectTimeoutTimeStamp) throws BasicHTTPException, InterruptedException {
        synchronized (this.lock) {
            InputStream input = null;
            connection = null;
            final boolean addedInterruptible = Boolean.TRUE.equals(InterruptibleThread.add(this));
            try {
                try {
                    this.connection = HTTPConnectionFactory.createHTTPConnection(url, this.proxy);
                    this.setAllowedResponseCodes(this.connection);
                    this.connection.setConnectTimeout(this.getConnectTimeout());
                    this.connection.setReadTimeout(this.getReadTimeout());
                    this.connection.setRequestProperty("Accept-Language", TranslationFactory.getDesiredLanguage());
                    this.connection.setRequestProperty("User-Agent", "AppWork " + Application.getApplication());
                    for (final Entry<String, String> next : this.requestHeader.entrySet()) {
                        this.connection.setRequestProperty(next.getKey(), next.getValue());
                    }
                    if (resumePosition > 0) {
                        this.connection.setRequestProperty("Range", "bytes=" + resumePosition + "-");
                    }
                    this.connection.setRequestProperty("Connection", "Close");
                    if (progress != null) {
                        progress.onConnect(connection);
                    }
                    this.connection.connect();
                    if (progress != null) {
                        progress.onConnected(connection);
                    }
                    final boolean ranged = this.connection.getRequestProperty("Range") != null;
                    if (ranged && this.connection.getResponseCode() == 200) {
                        throw new BadRangeResponse(this.connection);
                    }
                    if (this.connection.getResponseCode() == 301 || this.connection.getResponseCode() == 302 || this.connection.getResponseCode() == 303 || this.connection.getResponseCode() == 307) {
                        final String red = this.connection.getHeaderField("Location");
                        if (red != null) {
                            try {
                                this.connection.disconnect();
                            } catch (final Throwable e) {
                            } finally {
                                if (progress != null) {
                                    progress.onDisconnected(connection);
                                }
                            }
                            if (redirectTimeoutTimeStamp > 0 && System.currentTimeMillis() >= redirectTimeoutTimeStamp) {
                                throw new RedirectTimeoutException(connection);
                            }
                            if (this.connection.getResponseCode() == 302) {
                                Thread.sleep(100);
                            } else {
                                Thread.sleep(5000);
                            }
                            this.download(new URL(URLHelper.parseLocation(url, red)), progress, maxSize, baos, resumePosition, redirectTimeoutTimeStamp);
                            return;
                        }
                        throw new InvalidRedirectException(connection);
                    }
                    this.checkResponseCode();
                    input = this.connection.getInputStream();
                    if (this.connection.getCompleteContentLength() >= 0) {
                        /* contentLength is known */
                        if (maxSize > 0 && this.connection.getCompleteContentLength() > maxSize) {
                            throw new BadResponseLengthException(connection, maxSize);
                        }
                        if (progress != null) {
                            progress.setTotal(this.connection.getCompleteContentLength());
                        }
                    } else {
                        /* no contentLength is known */
                    }
                    final byte[] b = new byte[512 * 1024];
                    int len = 0;
                    long loaded = Math.max(0, resumePosition);
                    if (progress != null) {
                        progress.setLoaded(loaded);
                    }
                    while (true) {
                        try {
                            if ((len = input.read(b)) == -1) {
                                break;
                            }
                        } catch (IOException e) {
                            throw new ReadIOException(e);
                        }
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        if (len > 0) {
                            if (progress != null) {
                                progress.onBytesLoaded(b, len);
                            }
                            try {
                                baos.write(b, 0, len);
                            } catch (IOException e) {
                                throw new WriteIOException(e);
                            }
                            loaded += len;
                            if (maxSize > 0 && loaded > maxSize) {
                                throw new BadResponseLengthException(connection, maxSize);
                            }
                            if (progress != null) {
                                progress.increaseLoaded(len);
                            }
                        }
                    }
                    if (this.connection.getCompleteContentLength() >= 0) {
                        if (loaded != this.connection.getCompleteContentLength()) {
                            throw new IncompleteResponseException(connection, loaded);
                        }
                    }
                } catch (final ReadIOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, e));
                } catch (final WriteIOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, e));
                } catch (final BasicHTTPException e) {
                    throw handleInterrupt(e);
                } catch (final IOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, new ReadIOException(e)));
                } finally {
                    if (addedInterruptible) {
                        InterruptibleThread.remove(this);
                    }
                    try {
                        input.close();
                    } catch (final Exception e) {
                    }
                    try {
                        if (this.logger != null) {
                            this.logger.info(this.connection.toString());
                        }
                    } catch (final Throwable e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                if (progress != null) {
                    progress.onException(connection, e);
                }
                throw e;
            } catch (BasicHTTPException e) {
                if (progress != null) {
                    progress.onException(connection, e);
                }
                throw e;
            } finally {
                if (connection != null) {
                    try {
                        this.connection.disconnect();
                    } catch (final Throwable e) {
                    } finally {
                        if (progress != null) {
                            progress.onDisconnected(connection);
                        }
                    }
                }
            }
        }
    }

    public HashSet<Integer> getAllowedResponseCodes() {
        return this.allowedResponseCodes;
    }

    public HTTPConnection getConnection() {
        return this.connection;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public LogInterface getLogger() {
        return this.logger;
    }

    public String getPage(final URL url) throws IOException, InterruptedException {
        synchronized (this.lock) {
            BufferedReader in = null;
            InputStreamReader isr = null;
            connection = null;
            final boolean addedInterruptible = Boolean.TRUE.equals(InterruptibleThread.add(this));
            try {
                this.connection = HTTPConnectionFactory.createHTTPConnection(url, this.proxy);
                this.setAllowedResponseCodes(this.connection);
                this.connection.setConnectTimeout(this.getConnectTimeout());
                this.connection.setReadTimeout(this.getReadTimeout());
                this.connection.setRequestProperty("Accept-Language", TranslationFactory.getDesiredLanguage());
                this.connection.setRequestProperty("User-Agent", "AppWork " + Application.getApplication());
                this.connection.setRequestProperty("Accept-Charset", "UTF-8");
                for (final Entry<String, String> next : this.requestHeader.entrySet()) {
                    this.connection.setRequestProperty(next.getKey(), next.getValue());
                }
                this.connection.setRequestProperty("Connection", "Close");
                int lookupTry = 0;
                while (true) {
                    try {
                        this.connection.connect();
                        break;
                    } catch (final UnknownHostException e) {
                        if (++lookupTry > 3) {
                            throw e;
                        }
                        /* dns lookup failed, short wait and try again */
                        Thread.sleep(200);
                    }
                }
                this.checkResponseCode();
                in = new BufferedReader(isr = new InputStreamReader(this.connection.getInputStream(), "UTF-8"));
                String str;
                final StringBuilder sb = new StringBuilder();
                while ((str = in.readLine()) != null) {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    if (sb.length() > 0) {
                        sb.append("\r\n");
                    }
                    sb.append(str);
                }
                return sb.toString();
            } catch (final ReadIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final WriteIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final BasicHTTPException e) {
                throw handleInterrupt(e);
            } catch (final IOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, new ReadIOException(e)));
            } finally {
                if (addedInterruptible) {
                    InterruptibleThread.remove(this);
                }
                try {
                    in.close();
                } catch (final Throwable e) {
                }
                try {
                    isr.close();
                } catch (final Throwable e) {
                }
                try {
                    if (this.logger != null) {
                        this.logger.info(this.connection.toString());
                    }
                } catch (final Throwable e) {
                }
                try {
                    this.connection.disconnect();
                } catch (final Throwable e) {
                }
            }
        }
    }

    public HTTPProxy getProxy() {
        return this.proxy;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    /**
     * @return
     */
    public HashMap<String, String> getRequestHeader() {
        return this.requestHeader;
    }

    public String getRequestHeader(final String key) {
        return this.requestHeader.get(key);
    }

    public String getResponseHeader(final String string) {
        synchronized (this.lock) {
            if (this.connection == null) {
                return null;
            }
            return this.connection.getHeaderField(string);
        }
    }

    public HTTPConnection openGetConnection(final URL url) throws IOException, InterruptedException {
        return this.openGetConnection(url, this.readTimeout);
    }

    public HTTPConnection openGetConnection(final URL url, final int readTimeout) throws BasicHTTPException, InterruptedException {
        boolean close = true;
        synchronized (this.lock) {
            connection = null;
            final boolean addedInterruptible = Boolean.TRUE.equals(InterruptibleThread.add(this));
            try {
                this.connection = HTTPConnectionFactory.createHTTPConnection(url, this.proxy);
                this.setAllowedResponseCodes(this.connection);
                this.connection.setConnectTimeout(this.getConnectTimeout());
                this.connection.setReadTimeout(readTimeout < 0 ? this.readTimeout : readTimeout);
                this.connection.setRequestProperty("Accept-Language", TranslationFactory.getDesiredLanguage());
                this.connection.setRequestProperty("User-Agent", "AppWork " + Application.getApplication());
                this.connection.setRequestProperty("Accept-Charset", "UTF-8");
                for (final Entry<String, String> next : this.requestHeader.entrySet()) {
                    this.connection.setRequestProperty(next.getKey(), next.getValue());
                }
                this.connection.setRequestProperty("Connection", "Close");
                int lookupTry = 0;
                while (true) {
                    try {
                        this.connection.connect();
                        break;
                    } catch (final UnknownHostException e) {
                        if (++lookupTry > 3) {
                            throw e;
                        }
                        /* dns lookup failed, short wait and try again */
                        Thread.sleep(200);
                    }
                }
                close = false;
                this.checkResponseCode();
                return this.connection;
            } catch (final ReadIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final WriteIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final BasicHTTPException e) {
                throw handleInterrupt(e);
            } catch (final IOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, new ReadIOException(e)));
            } finally {
                if (addedInterruptible) {
                    InterruptibleThread.remove(this);
                }
                try {
                    if (this.logger != null) {
                        this.logger.info(this.connection.toString());
                    }
                } catch (final Throwable e) {
                }
                try {
                    if (close) {
                        this.connection.disconnect();
                    }
                } catch (final Throwable e2) {
                }
            }
        }
    }

    @SuppressWarnings("resource")
    public HTTPConnection openPostConnection(final URL url, final UploadProgress progress, final InputStream is, final HashMap<String, String> header, final long contentLength) throws BasicHTTPException, InterruptedException {
        boolean close = true;
        synchronized (this.lock) {
            final byte[] buffer = new byte[64000];
            connection = null;
            final boolean addedInterruptible = Boolean.TRUE.equals(InterruptibleThread.add(this));
            try {
                this.connection = HTTPConnectionFactory.createHTTPConnection(url, this.proxy);
                this.setAllowedResponseCodes(this.connection);
                this.connection.setConnectTimeout(this.getConnectTimeout());
                this.connection.setReadTimeout(this.getReadTimeout());
                this.connection.setRequestMethod(RequestMethod.POST);
                this.connection.setRequestProperty("Accept-Language", TranslationFactory.getDesiredLanguage());
                this.connection.setRequestProperty("User-Agent", "AppWork " + Application.getApplication());
                this.connection.setRequestProperty("Connection", "Close");
                /* connection specific headers */
                if (header != null) {
                    for (final Entry<String, String> next : header.entrySet()) {
                        if (HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH.equalsIgnoreCase(next.getKey())) {
                            continue;
                        } else {
                            this.connection.setRequestProperty(next.getKey(), next.getValue());
                        }
                    }
                }
                for (final Entry<String, String> next : this.requestHeader.entrySet()) {
                    if (HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH.equalsIgnoreCase(next.getKey())) {
                        continue;
                    } else {
                        this.connection.setRequestProperty(next.getKey(), next.getValue());
                    }
                }
                if (contentLength >= 0) {
                    this.connection.setRequestProperty(HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH, Long.toString(contentLength));
                } else {
                    this.connection.setRequestProperty(HTTPConstants.HEADER_RESPONSE_TRANSFER_ENCODING, HTTPConstants.HEADER_RESPONSE_TRANSFER_ENCODING_CHUNKED);
                }
                int lookupTry = 0;
                try {
                    while (true) {
                        try {
                            this.connection.connect();
                            break;
                        } catch (final UnknownHostException e) {
                            if (++lookupTry > 3) {
                                throw e;
                            }
                            /* dns lookup failed, short wait and try again */
                            Thread.sleep(200);
                        }
                    }
                } catch (final IOException e) {
                    throw new BasicHTTPException(this.connection, new ReadIOException(e));
                }
                final OutputStream outputStream;
                if (contentLength < 0) {
                    outputStream = new ChunkedOutputStream(this.connection.getOutputStream());
                } else {
                    outputStream = this.connection.getOutputStream();
                }
                while (true) {
                    final int read;
                    try {
                        read = is.read(buffer);
                    } catch (IOException e) {
                        throw new ReadIOException(e);
                    }
                    if (read == -1) {
                        break;
                    }
                    try {
                        outputStream.write(buffer, 0, read);
                    } catch (final IOException e) {
                        throw new WriteIOException(e);
                    }
                    if (progress != null) {
                        progress.onBytesUploaded(buffer, read);
                        progress.increaseUploaded(read);
                    }
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
                try {
                    if (outputStream instanceof ChunkedOutputStream) {
                        ((ChunkedOutputStream) outputStream).sendEOF();
                    } else {
                        outputStream.flush();
                    }
                } catch (final IOException e) {
                    throw new WriteIOException(e);
                }
                this.connection.finalizeConnect();
                this.checkResponseCode();
                close = false;
                return this.connection;
            } catch (final ReadIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final WriteIOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, e));
            } catch (final BasicHTTPException e) {
                throw handleInterrupt(e);
            } catch (final IOException e) {
                throw handleInterrupt(new BasicHTTPException(this.connection, new ReadIOException(e)));
            } finally {
                if (addedInterruptible) {
                    InterruptibleThread.remove(this);
                }
                try {
                    if (this.logger != null) {
                        this.logger.info(this.connection.toString());
                    }
                } catch (final Throwable e) {
                }
                try {
                    if (close) {
                        this.connection.disconnect();
                    }
                } catch (final Throwable e2) {
                }
            }
        }
    }

    public byte[] postPage(final URL url, final byte[] byteData) throws BasicHTTPException, InterruptedException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.postPage(url, byteData, baos, null, null);
        return baos.toByteArray();
    }

    /**
     * @param url
     * @param byteData
     * @param baos
     * @return
     * @throws InterruptedException
     * @throws BasicHTTPException
     */
    public void postPage(final URL url, byte[] byteData, final OutputStream baos, final DownloadProgress uploadProgress, final DownloadProgress downloadProgress) throws InterruptedException, BasicHTTPException {
        synchronized (this.lock) {
            OutputStream outputStream = null;
            connection = null;
            final boolean addedInterruptible = Boolean.TRUE.equals(InterruptibleThread.add(this));
            try {
                try {
                    this.connection = HTTPConnectionFactory.createHTTPConnection(url, this.proxy);
                    this.setAllowedResponseCodes(this.connection);
                    this.connection.setConnectTimeout(this.getConnectTimeout());
                    this.connection.setReadTimeout(this.getReadTimeout());
                    this.connection.setRequestMethod(RequestMethod.POST);
                    this.connection.setRequestProperty("Accept-Language", TranslationFactory.getDesiredLanguage());
                    this.connection.setRequestProperty("User-Agent", "AppWork " + Application.getApplication());
                    if (byteData == null) {
                        byteData = new byte[0];
                    }
                    this.connection.setRequestProperty(HTTPConstants.HEADER_RESPONSE_CONTENT_LENGTH, byteData.length + "");
                    for (final Entry<String, String> next : this.requestHeader.entrySet()) {
                        this.connection.setRequestProperty(next.getKey(), next.getValue());
                    }
                    this.connection.setRequestProperty("Connection", "Close");
                    int lookupTry = 0;
                    if (uploadProgress != null) {
                        uploadProgress.onConnect(connection);
                    }
                    while (true) {
                        try {
                            this.connection.connect();
                            if (uploadProgress != null) {
                                uploadProgress.onConnected(connection);
                            }
                            break;
                        } catch (final UnknownHostException e) {
                            if (++lookupTry > 3) {
                                throw e;
                            }
                            /* dns lookup failed, short wait and try again */
                            Thread.sleep(200);
                        }
                    }
                    outputStream = this.connection.getOutputStream();
                    // writer = new OutputStream(outputStream);
                    if (uploadProgress != null) {
                        uploadProgress.setTotal(byteData.length);
                    }
                    if (this.connection.getCompleteContentLength() >= 0) {
                        /* contentLength is known */
                        if (downloadProgress != null) {
                            downloadProgress.setTotal(this.connection.getCompleteContentLength());
                        }
                    } else {
                        /* no contentLength is known */
                    }
                    // write upload in 50*1024 steps
                    if (byteData.length > 0) {
                        int offset = 0;
                        while (true) {
                            final int part = Math.min(50 * 1024, byteData.length - offset);
                            if (part == 0) {
                                if (uploadProgress != null) {
                                    uploadProgress.setLoaded(byteData.length);
                                }
                                break;
                            }
                            outputStream.write(byteData, offset, part);
                            if (Thread.interrupted()) {
                                throw new InterruptedException();
                            }
                            outputStream.flush();
                            offset += part;
                            if (uploadProgress != null) {
                                uploadProgress.increaseLoaded(part);
                            }
                        }
                    }
                    outputStream.flush();
                    this.connection.finalizeConnect();
                    this.checkResponseCode();
                    final byte[] b = new byte[32767];
                    long loaded = 0;
                    final InputStream input = this.connection.getInputStream();
                    while (true) {
                        final int len;
                        try {
                            if ((len = input.read(b)) == -1) {
                                break;
                            }
                        } catch (final IOException e) {
                            throw new ReadIOException(e);
                        }
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }
                        if (len > 0) {
                            try {
                                baos.write(b, 0, len);
                            } catch (final IOException e) {
                                throw new WriteIOException(e);
                            }
                            loaded += len;
                            if (downloadProgress != null) {
                                downloadProgress.increaseLoaded(len);
                            }
                        }
                    }
                    if (this.connection.getCompleteContentLength() >= 0) {
                        if (loaded != this.connection.getCompleteContentLength()) {
                            throw new IncompleteResponseException(connection, loaded);
                        }
                    }
                    return;
                } catch (final ReadIOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, e));
                } catch (final WriteIOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, e));
                } catch (final BasicHTTPException e) {
                    throw handleInterrupt(e);
                } catch (final IOException e) {
                    throw handleInterrupt(new BasicHTTPException(this.connection, new ReadIOException(e)));
                } finally {
                    if (addedInterruptible) {
                        InterruptibleThread.remove(this);
                    }
                    try {
                        outputStream.close();
                    } catch (final Throwable e) {
                    }
                    try {
                        if (this.logger != null) {
                            this.logger.info(this.connection.toString());
                        }
                    } catch (final Throwable e) {
                    }
                }
            } catch (InterruptedException e) {
                if (uploadProgress != null) {
                    uploadProgress.onException(connection, e);
                }
                throw e;
            } catch (BasicHTTPException e) {
                if (uploadProgress != null) {
                    uploadProgress.onException(connection, e);
                }
                throw e;
            } finally {
                if (connection != null) {
                    try {
                        this.connection.disconnect();
                    } catch (final Throwable e) {
                    } finally {
                        if (uploadProgress != null) {
                            uploadProgress.onDisconnected(connection);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param <E>
     * @param basicHTTPException
     */
    private <E extends Throwable> E handleInterrupt(E exception) throws InterruptedException, E {
        if (Thread.interrupted()) {
            throw Exceptions.addSuppressed(new InterruptedException("Connection Closed by Interrupt"), exception);
        } else {
            return exception;
        }
    }

    public String postPage(final URL url, final String data) throws BasicHTTPException, InterruptedException {
        byte[] byteData;
        try {
            byteData = data.getBytes("UTF-8");
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.postPage(url, byteData, baos, null, null);
            return new String(baos.toByteArray(), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new BasicHTTPException(this.connection, e);
        }
    }

    public void putRequestHeader(final String key, final String value) {
        this.requestHeader.put(key, value);
    }

    protected void setAllowedResponseCodes(final HTTPConnection connection) {
        final HashSet<Integer> loc = this.getAllowedResponseCodes();
        if (loc != null) {
            final ArrayList<Integer> allowed = new ArrayList<Integer>(loc);
            final int[] ret = new int[allowed.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = allowed.get(i);
            }
            connection.setAllowedResponseCodes(ret);
        }
    }

    public void setAllowedResponseCodes(final int... codes) {
        this.allowedResponseCodes = new HashSet<Integer>();
        for (final int i : codes) {
            this.allowedResponseCodes.add(i);
        }
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = Math.max(1000, connectTimeout);
    }

    public void setLogger(final LogInterface logger) {
        this.logger = logger;
    }

    public void setProxy(final HTTPProxy proxy) {
        this.proxy = proxy;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = Math.max(1000, readTimeout);
    }
}
