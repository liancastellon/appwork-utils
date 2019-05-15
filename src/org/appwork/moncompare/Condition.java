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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.appwork.loggingv3.LogV3;
import org.appwork.storage.JSonStorage;
import org.appwork.storage.Storable;
import org.appwork.storage.StorageException;
import org.appwork.storage.simplejson.JSonFactory;
import org.appwork.storage.simplejson.ParserException;
import org.appwork.storage.simplejson.mapper.JSonMapper;
import org.appwork.storage.simplejson.mapper.MapperException;
import org.appwork.utils.CompareUtils;
import org.appwork.utils.ReflectionUtils;
import org.appwork.utils.reflection.Clazz;

/**
 * A compare class inspired by the mongodb queries. https://docs.mongodb.com/manual/reference/operator/query/in/
 *
 * @author Thomas
 * @date 06.05.2019
 *
 */
public class Condition extends HashMap<String, Object> implements Storable {
    /**
     *
     */
    public static final String $NOT                      = "§not";
    /**
     * Type is not derived from mongodb. This method can be used to query on java datatypes.
     */
    public static final String $TYPE                     = "§type";
    /**
     *
     */
    public static final String $AND                      = "§and";
    public static final String $IGNORE_GETTER_EXCEPTIONS = "§ignoreGetterErrors";
    /**
     *
     */
    public static final String $OR                       = "§or";
    /**
     *
     */
    public static final String $NIN                      = "§nin";
    /**
     *
     */
    public static final String $LT                       = "§lt";
    /**
     *
     */
    public static final String $LTE                      = "§lte";
    /**
     * Regex options https://docs.mongodb.com/manual/reference/operator/query/regex/#op._S_options
     */
    public static final String $OPTIONS                  = "§options";
    /**
     *
     */
    public static final String $EXISTS                   = "§exists";
    /**
     * The $in operator selects the documents where the value of a field equals any value in the specified array. To specify an $in
     * expression, use the following prototype:
     *
     * If the field holds an array, then the $in operator selects the documents whose field holds an array that contains at least one
     * element that matches a value in the specified array (e.g. <value1>, <value2>, etc.)
     *
     *
     */
    public static final String $IN                       = "§in";
    /**
     * $gte selects the documents where the value of the field is greater than or equal to (i.e. >=) a specified value (e.g. value.)
     */
    public static final String $GTE                      = "§gte";
    /**
     * $gt selects those documents where the value of the field is greater than (i.e. >) the specified value.
     */
    public static final String $GT                       = "§gt";
    /**
     * $ne selects the documents where the value of the field is not equal to the specified value. This includes documents that do not
     * contain the field.
     */
    public static final String $NE                       = "§ne";
    /**
     * Specifies equality condition. The $eq operator matches documents where the value of a field equals the specified value. WARNING:
     * There is a difference to MongoDb $EQ: If the specified <value> is a document, the order of the fields in the document DOES NOT
     * MATTER! <br>
     * Match an Array Value If the specified <value> is an array, MongoDB matches documents where the <field> matches the array exactly or
     * the <field> contains an element that matches the array exactly. The order of the elements matters. For an example, see Equals an
     * Array Value.
     */
    public static final String $EQ                       = "§eq";
    public static final String $REGEX                    = "§regex";

    /**
     * @author Thomas
     * @date 10.05.2019
     *
     */
    public static class AccessNotFound implements AccessMethod {
        @Override
        public Object getValue(Object value, String key) throws CannotGetValueException {
            return KEY_DOES_NOT_EXIST;
        }
    }

    /**
     * @author Thomas
     * @date 10.05.2019
     *
     */
    public static class AccessByField implements AccessMethod {
        private final Field   field;
        private final boolean isStatic;

        /**
         * @param field
         */
        public AccessByField(Field field) {
            this.field = field;
            isStatic = Modifier.isStatic(field.getModifiers());
        }

        @Override
        public Object getValue(Object value, String key) throws CannotGetValueException {
            try {
                if (isStatic) {
                    return field.get(null);
                } else {
                    return field.get(value);
                }
            } catch (IllegalAccessException e) {
                throw new CannotGetValueException(e);
            }
        }
    }

    /**
     * @author Thomas
     * @date 10.05.2019
     *
     */
    public static class AccessListElement implements AccessMethod {
        @Override
        public Object getValue(Object value, String key) throws CannotGetValueException {
            try {
                int index = Integer.parseInt(key);
                return ReflectionUtils.getListElement(value, index);
            } catch (Throwable e) {
                throw new CannotGetValueException(e);
            }
        }
    }

    /**
     * @author Thomas
     * @date 10.05.2019
     *
     */
    public static class AccessMyMethod implements AccessMethod {
        private final Method  method;
        private final boolean isStatic;

        /**
         * @param method
         */
        public AccessMyMethod(Method method) {
            this.method = method;
            isStatic = Modifier.isStatic(method.getModifiers());
        }

        @Override
        public Object getValue(Object value, String key) throws CannotGetValueException {
            try {
                return method.invoke(isStatic ? null : value, new Object[] {});
            } catch (IllegalAccessException e) {
                throw new CannotGetValueException(e);
            } catch (IllegalArgumentException e) {
                throw new CannotGetValueException(e);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                throw new CannotGetValueException(e);
            } catch (RuntimeException e) {
                throw new CannotGetValueException(e);
            }
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class NotOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            return !((Condition) query).matches(value);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class TypeOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            return container.equals(value.getClass().getName(), query);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class AndOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            for (int i = 0; i < ReflectionUtils.getListLength(query); i++) {
                if (!((Condition) ReflectionUtils.getListElement(query, i)).matches(value)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class OrOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            for (int i = 0; i < ReflectionUtils.getListLength(query); i++) {
                if (((Condition) ReflectionUtils.getListElement(query, i)).matches(value)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class RegexOp implements Operator {
        /**
         *
         */
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            Pattern pattern = (Pattern) container.getCache(RegexOp.class);
            if (pattern == null) {
                String options = (String) container.get($OPTIONS);
                int flags = 0;
                if (options != null) {
                    for (int i = 0; i < options.length(); i++) {
                        switch (options.charAt(i)) {
                        case 'i':
                            flags |= Pattern.CASE_INSENSITIVE;
                            break;
                        case 'm':
                            flags |= Pattern.MULTILINE;
                            break;
                        case 's':
                            flags |= Pattern.DOTALL;
                            break;
                        default:
                            throw new CompareException("Unsupported Regex Option");
                        }
                    }
                }
                if (query instanceof Pattern) {
                    pattern = (Pattern) query;
                } else if (query instanceof String) {
                    pattern = Pattern.compile((String) query, flags);
                }
                if (pattern == null) {
                    throw new CompareException("Operator expects a String or a Pattern(is not serializable) type as parameter");
                }
                container.putCache(RegexOp.class, pattern);
            }
            return pattern.matcher(String.valueOf(value)).matches();
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class NinOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            // the field does not exist.
            if (value == KEY_DOES_NOT_EXIST) {
                return true;
            }
            if (!ReflectionUtils.isList(query)) {
                throw new CompareException("Operator expects an array as parameter");
            }
            if (!ReflectionUtils.isList(value)) {
                // value is not a list
                for (int i = 0; i < ReflectionUtils.getListLength(query); i++) {
                    if (container.equals(value, ReflectionUtils.getListElement(query, i))) {
                        return false;
                    }
                }
                return true;
            } else {
                for (int i = 0; i < ReflectionUtils.getListLength(query); i++) {
                    for (int j = 0; j < ReflectionUtils.getListLength(value); j++) {
                        if (container.equals(ReflectionUtils.getListElement(value, j), ReflectionUtils.getListElement(query, i))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     */
    public static class NotEqualsOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return true;
            }
            return !CompareUtils.equals(query, value);
        }
    }

    /**
     * https://docs.mongodb.com/manual/reference/operator/query/exists/
     *
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class ExistsOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            boolean exists = Boolean.TRUE.equals(query);
            // {$exists:true} == {$exists:1}
            exists |= query instanceof Number && ((Number) query).intValue() != 0;
            if (exists) {
                return value != KEY_DOES_NOT_EXIST;
            } else {
                return value == KEY_DOES_NOT_EXIST;
            }
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class GteOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            if (value instanceof Number && query instanceof Number) {
                boolean ret = CompareUtils.compareNumber(((Number) value), ((Number) query)) >= 0;
                return ret;
            }
            throw new CompareException("Unsupported query: " + query + " on " + value);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class GtOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            if (value instanceof Number && query instanceof Number) {
                return CompareUtils.compareNumber(((Number) value), ((Number) query)) > 0;
            }
            throw new CompareException("Unsupported query: " + query + " on " + value);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class LteOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            if (value instanceof Number && query instanceof Number) {
                return CompareUtils.compareNumber(((Number) value), ((Number) query)) <= 0;
            }
            throw new CompareException("Unsupported query: " + query + " on " + value);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class LtOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            if (value instanceof Number && query instanceof Number) {
                boolean ret = CompareUtils.compareNumber(((Number) value), ((Number) query)) < 0;
                return ret;
            }
            throw new CompareException("Unsupported query: " + query + " on " + value);
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class InOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (value == KEY_DOES_NOT_EXIST) {
                return false;
            }
            if (!ReflectionUtils.isList(query)) {
                throw new CompareException("Operator expects an array as parameter");
            }
            if (!ReflectionUtils.isList(value)) {
                // Use the $in Operator to Match Values
                for (int j = 0; j < ReflectionUtils.getListLength(query); j++) {
                    if (container.equals(value, ReflectionUtils.getListElement(query, j))) {
                        return true;
                    }
                }
                return false;
            } else {
                for (int i = 0; i < ReflectionUtils.getListLength(query); i++) {
                    for (int j = 0; j < ReflectionUtils.getListLength(value); j++) {
                        if (container.equals(ReflectionUtils.getListElement(value, j), ReflectionUtils.getListElement(query, i))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * @author Thomas
     * @date 07.05.2019
     *
     */
    public static class EqOp implements Operator {
        @Override
        public boolean matches(Condition container, Object query, Object value) throws CompareException {
            if (container.equals(query, value)) {
                return true;
            }
            if (ReflectionUtils.isList(value)) {
                // Match an Array Value
                // If the specified <value> is an array, MongoDB matches documents where the <field> matches the array exactly or the
                // <field> contains an element that matches the array exactly. The order of the elements matters. For an example, see
                // Equals
                // an Array Value.
                for (int i = 0; i < ReflectionUtils.getListLength(value); i++) {
                    if (container.equals(ReflectionUtils.getListElement(value, i), query)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static final Object  KEY_DOES_NOT_EXIST = new Object() {
        /*
         * (non- Javadoc)
         *
         * @see java. util. AbstractMap# toString ()
         */
        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return "KEY_DOES_NOT_EXIST";
        }
    };
    private static final Class[] EMPTY              = new Class[] {};

    /**
     *
     */
    public Condition() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param string
     * @param i
     */
    public Condition(String key, Object o) {
        append(key, o);
    }

    /**
     * @param key
     * @param o
     */
    public Condition append(String key, Object o) {
        put(key, o);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public Object put(String key, Object value) {
        clearCache();
        return super.put(key, value);
    }

    protected Object getCache(final Object key) {
        return useAccessCache ? cache.get(key) : null;
    }

    protected void putCache(final Object key, Object object) {
        if (useAccessCache) {
            synchronized (this) {
                final HashMap<Object, Object> newCache = new HashMap<Object, Object>(cache);
                newCache.put(key, object);
                cache = newCache;
            }
        }
    }

    protected void clearCache() {
        synchronized (this) {
            if (cache.size() > 0) {
                cache = new HashMap<Object, Object>();
            }
            if (accessCache.size() > 0) {
                accessCache = new HashMap<Condition.KeyOnClass, Condition.AccessMethod>();
            }
        }
    }

    protected volatile HashMap<Object, Object>           cache          = new HashMap<Object, Object>();
    protected volatile HashMap<KeyOnClass, AccessMethod> accessCache    = new HashMap<KeyOnClass, AccessMethod>();
    protected final boolean                              useAccessCache = true;

    protected AccessMethod getAccessMethod(final KeyOnClass key) {
        return useAccessCache ? accessCache.get(key) : null;
    }

    protected void putAccessMethod(final KeyOnClass key, AccessMethod method) {
        if (useAccessCache) {
            synchronized (this) {
                final HashMap<KeyOnClass, AccessMethod> newCache = new HashMap<KeyOnClass, AccessMethod>(accessCache);
                newCache.put(key, method);
                accessCache = newCache;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        clearCache();
        super.putAll(m);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#clear()
     */
    @Override
    public void clear() {
        clearCache();
        super.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @Override
    public Object remove(Object key) {
        clearCache();
        return super.remove(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean replace(String key, Object oldValue, Object newValue) {
        clearCache();
        return super.replace(key, oldValue, newValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.HashMap#replaceAll(java.util.function.BiFunction)
     */
    @Override
    public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
        clearCache();
        super.replaceAll(function);
    }

    private static HashSet<String>           IGNORE = new HashSet<String>();
    static {
        IGNORE.add($OPTIONS);
        IGNORE.add($IGNORE_GETTER_EXCEPTIONS);
    }
    private static HashMap<String, Operator> OPS    = new HashMap<String, Operator>();
    static {
        OPS.put($GTE, new GteOp());
        OPS.put($GT, new GtOp());
        OPS.put($LTE, new LteOp());
        OPS.put($LT, new LtOp());
        OPS.put($EQ, new EqOp());
        // https://docs.mongodb.com/manual/reference/operator/query/in/
        OPS.put($IN, new InOp());
        OPS.put($EXISTS, new ExistsOp());
        OPS.put($NE, new NotEqualsOp());
        OPS.put($NIN, new NinOp());
        OPS.put($REGEX, new RegexOp());
        OPS.put($OR, new OrOp());
        OPS.put($AND, new AndOp());
        OPS.put($TYPE, new TypeOp());
        OPS.put($NOT, new NotOp());
    }

    /**
     * @param value
     * @return
     * @throws CompareException
     */
    public boolean matches(Object obj) throws CompareException {
        fixInternalMapToCondition();
        for (java.util.Map.Entry<String, Object> es : entrySet()) {
            if (IGNORE.contains(es.getKey())) {
                // for internal use only.
                continue;
            }
            Operator op = OPS.get(es.getKey());
            if (op != null) {
                if (!op.matches(this, es.getValue(), obj)) {
                    return false;
                } else {
                    continue;
                }
            }
            Object value = value(obj, es.getKey().split("[\\.\\:]"));
            if (es.getValue() instanceof Condition) {
                if (!((Condition) es.getValue()).matches(value)) {
                    return false;
                }
            } else {
                if (!OPS.get($EQ).matches(this, es.getValue(), value)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void fixInternalMapToCondition() throws CompareException {
        for (java.util.Map.Entry<String, Object> es : entrySet()) {
            // convert HashMap to Condition. Internal HashMaps may be created by deserializing Conditions. This is fixed in the first run
            if (es.getValue() instanceof Map && !(es.getValue() instanceof Condition)) {
                Condition condition;
                try {
                    condition = parse(JSonStorage.serializeToJson(es.getValue()));
                    replace(es.getKey(), es.getValue(), condition);
                    es.setValue(condition);
                } catch (StorageException e) {
                    throw new CompareException("Parsing Error ", e);
                } catch (MapperException e) {
                    throw new CompareException("Parsing Error ", e);
                } catch (ParserException e) {
                    throw new CompareException("Parsing Error ", e);
                }
            }
        }
    }

    /**
     * @param value
     * @param value2
     * @return
     * @throws CompareException
     */
    private boolean equals(Object value, Object query) throws CompareException {
        if (value == query) {
            return true;
        } else if (value == null && query != null) {
            return false;
        } else if (query == null) {
            return false;
        } else if (value instanceof Number && query instanceof Number) {
            return CompareUtils.equalsNumber((Number) value, (Number) query);
        } else if (ReflectionUtils.isList(value) && ReflectionUtils.isList(query)) {
            final int l1 = ReflectionUtils.getListLength(value);
            final int l2 = ReflectionUtils.getListLength(query);
            if (l1 != l2) {
                return false;
            } else {
                for (int i = 0; i < l1; i++) {
                    if (!equals(ReflectionUtils.getListElement(value, i), ReflectionUtils.getListElement(query, i))) {
                        return false;
                    }
                }
                return true;
            }
        } else if (query instanceof Condition) {
            return ((Condition) query).matches(value);
        } else {
            // TODO:other data types like LISTS
            return value.equals(query);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.AbstractMap#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return JSonStorage.serializeToJson(this);
    }

    /**
     * @param value
     * @param key
     * @return
     * @throws CompareException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public class KeyOnClass {
        private final Class<? extends Object> class1;
        private final String                  key;
        private final int                     hashCode;

        /**
         * @param class1
         * @param key
         */
        public KeyOnClass(Class<? extends Object> class1, String key) {
            this.class1 = class1;
            this.key = key;
            this.hashCode = class1.hashCode() + key.hashCode();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (!(obj instanceof KeyOnClass)) {
                return false;
            } else if (!class1.equals(((KeyOnClass) obj).class1)) {
                return false;
            } else if (!key.equals(((KeyOnClass) obj).key)) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    public interface AccessMethod {
        /**
         * @param obj
         * @return
         * @throws CompareException
         */
        public abstract Object getValue(Object value, String key) throws CannotGetValueException;
    }

    private Object value(Object obj, String... keys) throws CompareException {
        Object value = obj;
        for (final String key : keys) {
            if (value == null) {
                return KEY_DOES_NOT_EXIST;
            }
            final KeyOnClass cacheKey = new KeyOnClass(value.getClass(), key);
            AccessMethod accessMethod = getAccessMethod(cacheKey);
            if (accessMethod != null) {
                try {
                    value = accessMethod.getValue(value, key);
                } catch (CannotGetValueException e) {
                    if (Boolean.TRUE.equals(get($IGNORE_GETTER_EXCEPTIONS))) {
                        return KEY_DOES_NOT_EXIST;
                    } else {
                        throw new CompareException(e);
                    }
                }
                continue;
            }
            if (value instanceof Map) {
                Object newValue = ((Map) value).get(key);
                if (newValue == null) {
                    if (((Map) value).containsKey(key)) {
                        return null;
                    } else {
                        return KEY_DOES_NOT_EXIST;
                    }
                }
                value = newValue;
                continue;
            }
            if (ReflectionUtils.isList(value)) {
                accessMethod = new AccessListElement();
                putAccessMethod(cacheKey, accessMethod);
                try {
                    value = accessMethod.getValue(value, key);
                } catch (CannotGetValueException e) {
                    if (Boolean.TRUE.equals(get($IGNORE_GETTER_EXCEPTIONS))) {
                        return KEY_DOES_NOT_EXIST;
                    } else {
                        throw new CompareException(e);
                    }
                }
                continue;
            }
            // Search for methods that have the exact key name
            Class<? extends Object> cls = value.getClass();
            Method method = null;
            while (cls != null) {
                try {
                    method = cls.getDeclaredMethod(key, EMPTY);
                    if (isForbiddenMethod(method)) {
                        method = null;
                    } else {
                        break;
                    }
                } catch (SecurityException e) {
                    throw new CompareException("Cannot get value", e);
                } catch (IllegalArgumentException e) {
                    throw new CompareException("Cannot get value", e);
                } catch (NoSuchMethodException e) {
                }
                cls = cls.getSuperclass();
            }
            if (method == null) {
                // check getters with the key. get/is<Key>
                cls = value.getClass();
                method = null;
                while (cls != null) {
                    try {
                        method = cls.getDeclaredMethod("is" + Character.toUpperCase(key.charAt(0)) + key.substring(1), EMPTY);
                        if (isForbiddenMethod(method)) {
                            method = null;
                        } else {
                            break;
                        }
                    } catch (SecurityException e) {
                        throw new CompareException("Cannot get value", e);
                    } catch (IllegalArgumentException e) {
                        throw new CompareException("Cannot get value", e);
                    } catch (NoSuchMethodException e) {
                    }
                    try {
                        method = cls.getDeclaredMethod("get" + Character.toUpperCase(key.charAt(0)) + key.substring(1), EMPTY);
                        if (isForbiddenMethod(method)) {
                            method = null;
                        } else {
                            break;
                        }
                    } catch (SecurityException e) {
                        throw new CompareException("Cannot get value", e);
                    } catch (IllegalArgumentException e) {
                        throw new CompareException("Cannot get value", e);
                    } catch (NoSuchMethodException e) {
                    }
                    cls = cls.getSuperclass();
                }
            }
            if (method != null) {
                accessMethod = new AccessMyMethod(method);
                putAccessMethod(cacheKey, accessMethod);
                try {
                    value = accessMethod.getValue(value, key);
                } catch (CannotGetValueException e) {
                    if (Boolean.TRUE.equals(get($IGNORE_GETTER_EXCEPTIONS))) {
                        return KEY_DOES_NOT_EXIST;
                    } else {
                        throw new CompareException(e);
                    }
                }
                continue;
            }
            // check fields
            Field field = null;
            cls = value.getClass();
            while (cls != null) {
                try {
                    field = cls.getDeclaredField(key);
                    if (isForbiddenField(field)) {
                        field = null;
                    } else {
                        break;
                    }
                } catch (SecurityException e) {
                    throw new CompareException("Cannot get value", e);
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                    throw new CompareException("Cannot get value", e);
                }
                cls = cls.getSuperclass();
            }
            if (field != null) {
                accessMethod = new AccessByField(field);
                putAccessMethod(cacheKey, accessMethod);
                try {
                    value = accessMethod.getValue(value, key);
                } catch (CannotGetValueException e) {
                    if (Boolean.TRUE.equals(get($IGNORE_GETTER_EXCEPTIONS))) {
                        return KEY_DOES_NOT_EXIST;
                    } else {
                        throw new CompareException(e);
                    }
                }
                continue;
            }
            if (useAccessCache) {
                // only put Accessnotfound in the cache if the class it not a map or list and the key is not found in the class declaration.
                putAccessMethod(cacheKey, new AccessNotFound());
            }
            return KEY_DOES_NOT_EXIST;
        }
        return value;
    }

    /**
     * @param field
     * @return
     */
    private boolean isForbiddenField(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            return true;
        }
        return false;
    }

    /**
     * @param method
     * @return
     */
    private boolean isForbiddenMethod(Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return true;
        }
        if (Clazz.isVoid(method.getReturnType())) {
            return true;
        }
        return false;
    }

    public static final org.appwork.storage.TypeRef<Condition> TYPE = new org.appwork.storage.TypeRef<Condition>(Condition.class) {
    };

    /**
     * @param json
     * @return
     * @throws ParserException
     * @throws MapperException
     */
    public static Condition parse(String json) throws MapperException, ParserException {
        JSonMapper mapper = new JSonMapper() {
            {
                autoMapJsonObjectClass = Condition.class;
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
                        return Condition.class;
                    }
                }
                return super.mapClasses(class1);
            }
        };
        return mapper.jsonToObject(new JSonFactory(json).parse(), TYPE);
    }

    /**
     * @param request
     * @return
     */
    public boolean matchesWithoutExceptions(Object test) {
        try {
            return matches(test);
        } catch (CompareException e) {
            LogV3.log(e);
            return false;
        }
    }
}
