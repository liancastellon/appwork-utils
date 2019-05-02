package org.appwork.storage;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.appwork.utils.GetterSetter;

public class CleanedJSonObject {
    public static interface CompareInstanceProvider {
        /**
         * @param class1
         * @return
         */
        Object createInstance(Class<? extends Object> class1);
    }

    private Object                  object;
    private String                  key;
    private CleanedJSonObject       parent;
    private CompareInstanceProvider compareFactory;

    /**
     * @param compareFactory
     *            the compareFactory to set
     */
    public CleanedJSonObject setCompareFactory(CompareInstanceProvider compareFactory) {
        this.compareFactory = compareFactory;
        return this;
    }

    public CleanedJSonObject(Object responseData, CompareInstanceProvider compareFactory) {
        this.object = responseData;
        this.compareFactory = compareFactory;
    }

    public CleanedJSonObject(Object responseData) {
        this.object = responseData;
    }

    protected CleanedJSonObject(String key, Object responseData, CleanedJSonObject parent) {
        this.object = responseData;
        this.key = key;
        this.parent = parent;
        this.compareFactory = parent.compareFactory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        CleanedJSonObject o = this;
        do {
            if (o.key != null || o.object != null) {
                if (sb.length() > 0) {
                    sb.insert(0, ".");
                }
                sb.insert(0, o.key == null ? o.object.getClass().getSimpleName() : o.key);
            }
            o = o.parent;
        } while (o != null);
        return sb.toString();
    }

    private static final HashMap<Class<?>, Collection<GetterSetter>> GETTER_SETTER_CACHE = new HashMap<Class<?>, Collection<GetterSetter>>();

    public static boolean isBoolean(final Type type) {
        return type == Boolean.class || type == boolean.class;
    }

    public static boolean isNotEmpty(final String ip) {
        return !(ip == null || ip.trim().length() == 0);
    }

    public static String createKey(final String key) {
        final StringBuilder sb = new StringBuilder();
        final char[] ca = key.toCharArray();
        boolean starter = true;
        for (char element : ca) {
            if (starter && Character.isUpperCase(element)) {
                sb.append(Character.toLowerCase(element));
            } else {
                starter = false;
                sb.append(element);
            }
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public Collection<GetterSetter> getGettersSetteres(Class<?> clazz) {
        synchronized (GETTER_SETTER_CACHE) {
            Collection<GetterSetter> ret = GETTER_SETTER_CACHE.get(clazz);
            if (ret != null) {
                return ret;
            }
            final Class<?> org = clazz;
            synchronized (GETTER_SETTER_CACHE) {
                ret = GETTER_SETTER_CACHE.get(clazz);
                if (ret != null) {
                    return ret;
                }
                final HashMap<String, GetterSetter> map = new HashMap<String, GetterSetter>();
                while (clazz != null) {
                    for (final Method m : clazz.getDeclaredMethods()) {
                        if (Modifier.isStatic(m.getModifiers())) {
                            continue;
                        }
                        String key = null;
                        boolean getter = false;
                        if (m.getName().startsWith("is") && isBoolean(m.getReturnType()) && m.getParameterTypes().length == 0) {
                            key = m.getName().substring(2);
                            getter = true;
                        } else if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                            key = m.getName().substring(3);
                            getter = true;
                        } else if (m.getName().startsWith("set") && m.getParameterTypes().length == 1) {
                            key = m.getName().substring(3);
                            getter = false;
                        }
                        if (isNotEmpty(key)) {
                            final String unmodifiedKey = key;
                            key = createKey(key);
                            GetterSetter v = map.get(key);
                            if (v == null) {
                                v = new GetterSetter(key);
                                map.put(key, v);
                            }
                            if (getter) {
                                v.setGetter(m);
                            } else {
                                v.setSetter(m);
                            }
                            Field field;
                            try {
                                field = clazz.getField(unmodifiedKey.substring(0, 1).toLowerCase(Locale.ENGLISH) + unmodifiedKey.substring(1));
                                v.setField(field);
                            } catch (final NoSuchFieldException e) {
                            }
                        }
                    }
                    clazz = getSuperClass(clazz);
                }
                GETTER_SETTER_CACHE.put(org, map.values());
                return GETTER_SETTER_CACHE.get(org);
            }
        }
    }

    protected Class<?> getSuperClass(Class<?> clazz) {
        return clazz.getSuperclass();
    }

    public boolean equals(final Object pass, final Object pass2) {
        if (pass == pass2) {
            return true;
        }
        if (pass == null && pass2 != null) {
            return false;
        }
        if (pass2 == null) {
            return false;
        }
        if (isList(pass) && isList(pass2)) {
            int p1 = getListLength(pass);
            int p2 = getListLength(pass2);
            if (p1 != p2) {
                return false;
            }
            for (int i = 0; i < p1; i++) {
                if (!equals(getListElement(pass, i), getListElement(pass2, i))) {
                    return false;
                }
            }
            return true;
        }
        // TODO:other data types like LISTS
        return pass.equals(pass2);
    }

    /**
     * @param pass
     * @param i
     * @return
     */
    private Object getListElement(Object pass, int i) {
        if (pass.getClass().isArray()) {
            return Array.get(pass, i);
        }
        if (pass instanceof List) {
            return ((List) pass).get(i);
        }
        throw new IllegalStateException();
    }

    /**
     * @param pass
     * @return
     */
    private int getListLength(Object pass) {
        if (pass.getClass().isArray()) {
            return Array.getLength(pass);
        }
        if (pass instanceof List) {
            return ((List) pass).size();
        }
        throw new IllegalStateException();
    }

    /**
     * @param pass2
     * @return
     */
    private boolean isList(Object pass2) {
        if (pass2 == null) {
            return false;
        }
        return pass2.getClass().isArray() || pass2 instanceof List;
    }

    public String serialize() {
        if (false) {
            return JSonStorage.serializeToJson(this.object);
        }
        final Object map = this.getCleanedData();
        return JSonStorage.serializeToJson(map);
    }

    public Object getCleanedData() {
        try {
            // System.out.println(this.toString());
            if (this.object == null) {
                return null;
            }
            if (this.object instanceof Collection) {
                final ArrayList<Object> ret = new ArrayList<Object>();
                int i = 0;
                for (final Object o : (Collection) this.object) {
                    ret.add(new CleanedJSonObject("[" + i++ + "]", o, this).getCleanedData());
                }
                return ret;
            } else if (this.object.getClass().isArray()) {
                final ArrayList<Object> ret = new ArrayList<Object>();
                for (int i = 0; i < Array.getLength(this.object); i++) {
                    ret.add(new CleanedJSonObject("[" + i + "]", Array.get(this.object, i), this).getCleanedData());
                }
                return ret;
            } else if (this.object instanceof Map) {
                final HashMap<String, Object> map = new HashMap<String, Object>();
                for (Entry<String, Object> es : ((Map<String, Object>) object).entrySet()) {
                    map.put(es.getKey(), new CleanedJSonObject(es.getKey(), es.getValue(), this).getCleanedData());
                }
                return map;
            } else if (this.object instanceof Storable) {
                return storableToMap();
            } else {
                return this.object;
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Object> storableToMap() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        Object obj = null;
        Object empty = null;
        if (compareFactory != null) {
            empty = compareFactory.createInstance(object.getClass());
        }
        if (empty == null) {
            final Constructor<?> c = this.object.getClass().getDeclaredConstructor(new Class[] {});
            c.setAccessible(true);
            empty = c.newInstance();
        }
        for (final GetterSetter gs : getGettersSetteres(this.object.getClass())) {
            if ("class".equals(gs.getKey())) {
                continue;
            }
            obj = new CleanedJSonObject(gs.getKey(), gs.get(this.object), this).getCleanedData();
            if (this.equals(obj, gs.get(empty))) {
                continue;
            }
            // if (obj instanceof Storable) {
            map.put(Character.toLowerCase(gs.getKey().charAt(0)) + gs.getKey().substring(1), obj);
            // } else {
            // map.put(Character.toLowerCase(gs.getKey().charAt(0)) + gs.getKey().substring(1), obj);
            // }
        }
        return map;
    }
}
