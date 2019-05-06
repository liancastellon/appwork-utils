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
package org.appwork.moncompare;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.appwork.storage.Storable;
import org.appwork.storage.simplejson.JSonFactory;
import org.appwork.storage.simplejson.ParserException;
import org.appwork.storage.simplejson.mapper.JSonMapper;
import org.appwork.storage.simplejson.mapper.MapperException;
import org.appwork.utils.CompareUtils;

/**
 * A compare class inspired by the mongodb queries. https://docs.mongodb.com/manual/reference/operator/query/in/
 *
 * @author Thomas
 * @date 06.05.2019
 *
 */
public class Doc extends HashMap<String, Object> implements Storable {
    private static final Class[]  CLASS_ARRAY  = new Class[] {};
    private static final Object[] OBJECT_ARRAY = new Object[] {};

    /**
     *
     */
    public Doc() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param string
     * @param i
     */
    public Doc(String key, Object o) {
        append(key, o);
    }

    /**
     * @param key
     * @param o
     */
    public Doc append(String key, Object o) {
        put(key, o);
        return this;
    }

    private static HashMap<String, Operator> OPS = new HashMap<String, Operator>();
    static {
        OPS.put("$gte", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                if (test instanceof Number && query instanceof Number) {
                    return CompareUtils.compare(((Number) test).doubleValue(), ((Number) query).doubleValue()) >= 0;
                }
                throw new CompareException("Unsupported query: " + query + " on " + test);
            }
        });
        OPS.put("$gt", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                if (test instanceof Number && query instanceof Number) {
                    return CompareUtils.compare(((Number) test).doubleValue(), ((Number) query).doubleValue()) > 0;
                }
                throw new CompareException("Unsupported query: " + query + " on " + test);
            }
        });
        OPS.put("$lte", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                if (test instanceof Number && query instanceof Number) {
                    return CompareUtils.compare(((Number) test).doubleValue(), ((Number) query).doubleValue()) <= 0;
                }
                throw new CompareException("Unsupported query: " + query + " on " + test);
            }
        });
        OPS.put("$lt", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                if (test instanceof Number && query instanceof Number) {
                    return CompareUtils.compare(((Number) test).doubleValue(), ((Number) query).doubleValue()) < 0;
                }
                throw new CompareException("Unsupported query: " + query + " on " + test);
            }
        });
        OPS.put("$eq", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                return CompareUtils.equals(query, test);
            }
        });
        // https://docs.mongodb.com/manual/reference/operator/query/in/
        OPS.put("$in", new Operator() {
            @Override
            public boolean matches(Doc container, Object query, Object test) throws CompareException {
                // TODO List support
                if (!container.isList(query)) {
                    query = new Object[] { query };
                }
                if (!container.isList(test)) {
                    throw new CompareException("This operator requires an array");
                }
                for (int i = 0; i < container.getListLength(query); i++) {
                    for (int j = 0; j < container.getListLength(test); j++) {
                        if (container.equals(container.getListElement(query, i), container.getListElement(test, j))) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * @param object
     * @param i
     * @return
     */
    private final Object getListElement(Object object, int i) {
        if (object.getClass().isArray()) {
            return Array.get(object, i);
        } else {
            if (object instanceof List) {
                return ((List) object).get(i);
            } else {
                throw new IllegalStateException(object + " is no List");
            }
        }
    }

    /**
     * @param object
     * @return
     */
    private final int getListLength(final Object object) {
        if (object.getClass().isArray()) {
            return Array.getLength(object);
        } else if (object instanceof List) {
            return ((List) object).size();
        } else {
            throw new IllegalStateException(object + " is no List");
        }
    }

    /**
     * @param object
     * @return
     */
    private final boolean isList(final Object object) {
        return object != null && (object.getClass().isArray() || object instanceof List);
    }

    /**
     * @param test
     * @return
     * @throws CompareException
     */
    public boolean matches(Object test) throws CompareException {
        for (java.util.Map.Entry<String, Object> es : entrySet()) {
            Operator op = OPS.get(es.getKey());
            if (op != null) {
                if (!op.matches(this, es.getValue(), test)) {
                    return false;
                } else {
                    continue;
                }
            }
            Object value = value(test, es.getKey());
            if (es.getValue() instanceof Doc) {
                if (!((Doc) es.getValue()).matches(value)) {
                    return false;
                }
            } else {
                if (!equals(value, es.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param value
     * @param value2
     * @return
     */
    private boolean equals(Object objValue, Object queryValue) {
        return CompareUtils.equals(objValue, queryValue);
    }

    /**
     * @param test
     * @param key
     * @return
     * @throws CompareException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private Object value(Object test, String key) throws CompareException {
        if (test instanceof Map) {
            return ((Map) test).get(key);
        }
        Method method = null;
        try {
            method = test.getClass().getMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1), CLASS_ARRAY);
        } catch (NoSuchMethodException e) {
            ;
        } catch (SecurityException e) {
        }
        if (method == null) {
            try {
                test.getClass().getMethod("is" + Character.toUpperCase(key.charAt(0)) + key.substring(1), CLASS_ARRAY);
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e) {
            }
        }
        if (method == null) {
            return null;
        }
        method.setAccessible(true);
        try {
            return method.invoke(test, OBJECT_ARRAY);
        } catch (IllegalAccessException e) {
            throw new CompareException("Cannot get value", e);
        } catch (IllegalArgumentException e) {
            throw new CompareException("Cannot get value", e);
        } catch (InvocationTargetException e) {
            throw new CompareException("Cannot get value", e);
        }
    }

    public static final org.appwork.storage.TypeRef<Doc> TYPE = new org.appwork.storage.TypeRef<Doc>(Doc.class) {
    };

    /**
     * @param json
     * @return
     * @throws ParserException
     * @throws MapperException
     */
    public static Doc parse(String json) throws MapperException, ParserException {
        JSonMapper mapper = new JSonMapper() {
            {
                autoMapJsonObjectClass = Doc.class;
            }

            /*
             * (non-Javadoc)
             *
             * @see org.appwork.storage.simplejson.mapper.JSonMapper#mapClasses(java.lang.Class)
             */
            @Override
            protected Class<?> mapClasses(Class<?> class1) throws MapperException {
                if (class1.isInterface()) {
                    if (Map.class.isAssignableFrom(class1)) {
                        return Doc.class;
                    }
                }
                return super.mapClasses(class1);
            }
        };
        return mapper.jsonToObject(new JSonFactory(json).parse(), TYPE);
    }
}
