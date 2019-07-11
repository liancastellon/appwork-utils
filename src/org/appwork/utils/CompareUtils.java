package org.appwork.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.appwork.exceptions.WTFException;
import org.appwork.storage.simplejson.mapper.ClassCache;
import org.appwork.storage.simplejson.mapper.Getter;
import org.appwork.utils.reflection.Clazz;

public class CompareUtils {
    /**
     * @param x
     * @param y
     * @return
     */
    public static int compare(boolean x, boolean y) {
        return (x == y) ? 0 : (x ? 1 : -1);
    }

    /**
     * @param height
     * @param height2
     * @return
     */
    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    /**
     *
     * @param x
     * @param y
     * @return <0 if x<y >0 if x>y 0 if x==y
     */
    public static int compare(double x, double y) {
        // since 1.4
        return Double.compare(x, y);
    }

    /**
     * @param projection
     * @param projection2
     * @return
     */
    public static int compare(Comparable x, Comparable y) {
        if (x == y) {
            return 0;
        }
        if (x == null) {
            return -1;
        }
        if (y == null) {
            return 1;
        }
        return x.compareTo(y);
    }

    /**
     * @param hash
     * @param hash2
     * @deprecated use Arrays.equals instead
     * @return
     */
    public static boolean equals(byte[] hash, byte[] hash2) {
        return Arrays.equals(hash, hash2);
    }

    /**
     * returns true of both objects are null or their.equals method match
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        // Objects.equals(a, b) is 1.7+
        return a.equals(b);
    }

    /**
     * this.method does not go rekursive. the values of the maps are compared via org.appwork.utils.CompareUtils.equalsObjects(Object,
     * Object) (both null or equals
     *
     * @param extensions
     * @param extensions2
     * @return
     */
    public static boolean equals(Map<?, ?> a, Map<?, ?> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.size() != b.size()) {
            return false;
        }
        for (Entry<?, ?> es : a.entrySet()) {
            if (!equals(es.getValue(), b.get(es.getKey()))) {
                return false;
            }
        }
        return false;
    }

    public static boolean equalsDeep(final Object objectX, final Object objectY) {
        if (objectX == objectY) {
            return true;
        } else if (objectX == null && objectY != null) {
            return false;
        } else if (objectY == null) {
            return false;
        } else if (objectX.equals(objectY)) {
            // if equals says these objects equal, we trust
            return true;
        } else if (ReflectionUtils.isList(objectX) && ReflectionUtils.isList(objectY)) {
            final int l1 = ReflectionUtils.getListLength(objectX);
            final int l2 = ReflectionUtils.getListLength(objectY);
            if (l1 != l2) {
                return false;
            } else {
                for (int i = 0; i < l1; i++) {
                    if (!equalsDeep(ReflectionUtils.getListElement(objectX, i), ReflectionUtils.getListElement(objectY, i))) {
                        return false;
                    }
                }
                return true;
            }
        } else if (objectX instanceof Map && objectY instanceof Map) {
            for (Entry<?, ?> es : ((Map<?, ?>) objectX).entrySet()) {
                if (!equalsDeep(es.getValue(), ((Map<?, ?>) objectY).get(es.getKey()))) {
                    return false;
                }
            }
            return true;
        } else if (objectX.getClass() == objectY.getClass()) {
            if (Clazz.isPrimitive(objectX.getClass()) || Clazz.isEnum(objectX.getClass()) || Clazz.isString(objectX.getClass())) {
                // if true, this would have exited in the } else if (objectX.equals(objectY)) { block above
                return false;
            }
            ClassCache cc;
            try {
                cc = ClassCache.getClassCache(objectX.getClass());
                for (Getter c : cc.getGetter()) {
                    if (!equalsDeep(c.getValue(objectX), c.getValue(objectY))) {
                        return false;
                    }
                }
                return true;
            } catch (SecurityException e) {
                throw new WTFException(e);
            } catch (NoSuchMethodException e) {
                return false;
            } catch (IllegalArgumentException e) {
                throw new WTFException(e);
            } catch (IllegalAccessException e) {
                throw new WTFException(e);
            } catch (InvocationTargetException e) {
                throw new WTFException(e);
            }
        } else {
            return objectX.equals(objectY);
        }
    }

    /**
     * @param number
     * @param number2
     * @return
     */
    public static int compareNumber(Number a, Number b) {
        if (Clazz.isFixedPointNumber(a.getClass()) && Clazz.isFixedPointNumber(b.getClass())) {
            return compare(a.longValue(), b.longValue());
        } else {
            return compare(a.doubleValue(), b.doubleValue());
        }
    }

    /**
     * @param value
     * @param query
     * @return
     */
    public static boolean equalsNumber(Number a, Number b) {
        if (Clazz.isFixedPointNumber(a.getClass()) && Clazz.isFixedPointNumber(b.getClass())) {
            return a.longValue() == b.longValue();
        } else {
            return a.doubleValue() == b.doubleValue();
        }
    }
}
