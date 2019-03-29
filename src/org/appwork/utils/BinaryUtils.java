package org.appwork.utils;

public class BinaryUtils {
    /**
     * Merges all arrays into a single one
     *
     * @param prefix
     * @param ret
     * @return
     */
    public static byte[] mergeArrays(byte[]... parts) {
        int index = 0;
        for (byte[] b : parts) {
            index += b.length;
        }
        byte[] total = new byte[index];
        index = 0;
        for (byte[] b : parts) {
            System.arraycopy(b, 0, total, index, b.length);
            index += b.length;
        }
        return total;
    }

    /**
     * 
     * @throws IndexOutOfBoundsException
     * @return true if a and b match for the first <length> bytes starting at <offsetA/offsetB>
     */
    public static boolean matches(byte[] a, byte[] b, int offsetA, int offsetB, int length) {
        for (int i = offsetA; i < offsetA + length; i++) {
            if (a[i] != b[i - offsetA + offsetB]) {
                return false;
            }
        }
        return true;
    }
}
