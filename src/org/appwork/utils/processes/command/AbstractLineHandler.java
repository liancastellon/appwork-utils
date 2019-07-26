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
package org.appwork.utils.processes.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

import org.appwork.loggingv3.LogV3;
import org.appwork.utils.logging2.LogInterface;
import org.appwork.utils.net.LineParsingInputStream;
import org.appwork.utils.net.LineParsingOutputStream.NEWLINE;
import org.appwork.utils.processes.LineHandler;

/**
 * @author Thomas
 * @date 08.11.2018
 *
 */
public abstract class AbstractLineHandler implements LineHandler, OutputHandler {
    private final LogInterface logger;

    /**
     *
     */
    public AbstractLineHandler() {
        logger = LogV3.defaultLogger();
    }

    public class LineReaderThread extends Thread implements AsyncInputStreamHandler {
        private final LineParsingInputStream is;
        private final AtomicBoolean          processExitedFlag  = new AtomicBoolean(false);
        private volatile long                processReadCurrent = 0;

        /**
         * @param charset
         * @param lh
         * @param inputStream
         * @throws UnsupportedEncodingException
         * @throws InterruptedException
         */
        public LineReaderThread(final LineHandler lineHandler, Charset charset, final InputStream inputStream) throws UnsupportedEncodingException, InterruptedException {
            super(inputStream.getClass().getSimpleName());
            setDaemon(true);
            this.is = new LineParsingInputStream(inputStream, charset) {
                @Override
                protected void onNextLine(NEWLINE newLine, long line, StringBuilder sb, int startIndex, int endIndex) {
                    lineHandler.handleLine(sb.substring(startIndex, endIndex), LineReaderThread.this);
                }
            };
        }

        @Override
        public void run() {
            final byte[] buf = new byte[8192];
            while (true) {
                try {
                    final int read = is.read(buf);
                    if (read <= 0) {
                        if (processExitedFlag.get()) {
                            return;
                        } else {
                            Thread.sleep(50);
                        }
                    } else {
                        processReadCurrent += read;
                    }
                } catch (IOException e) {
                    if (!processExitedFlag.get()) {
                        logger.log(e);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        @Override
        public void interrupt() {
            try {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            } finally {
                super.interrupt();
            }
        }

        protected void notifyProcessExited() {
            if (processExitedFlag.compareAndSet(false, true)) {
                synchronized (processExitedFlag) {
                    processExitedFlag.notifyAll();
                }
            }
        }

        /**
         * @throws InterruptedException
         * @throws IOException
         *
         */
        public void waitFor() throws InterruptedException {
            notifyProcessExited();
            long processReadLast = processReadCurrent;
            long timeStamp = System.currentTimeMillis();
            try {
                while (isAlive()) {
                    final long now = processReadCurrent;
                    if (processReadLast == now) {
                        if (System.currentTimeMillis() - timeStamp > 10000) {
                            break;
                        }
                    } else {
                        processReadLast = now;
                        timeStamp = System.currentTimeMillis();
                    }
                    Thread.sleep(50);
                }
            } finally {
                interrupt();
            }
        }

        @Override
        public void onExit(int exitCode) {
            notifyProcessExited();
        }
    }

    @Override
    public void onExitCode(int exitCode) {
    }

    @Override
    public AsyncInputStreamHandler createAsyncStreamHandler(CommandErrInputStream inputStream, Charset charset) throws UnsupportedEncodingException, InterruptedException {
        return new LineReaderThread(this, charset, inputStream);
    }

    @Override
    public AsyncInputStreamHandler createAsyncStreamHandler(CommandStdInputStream inputStream, Charset charset) throws UnsupportedEncodingException, InterruptedException {
        return new LineReaderThread(this, charset, inputStream);
    }
}
