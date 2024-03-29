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
package org.appwork.storage.simplejson.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.appwork.loggingv3.LogV3;
import org.appwork.storage.simplejson.Ignore;
import org.appwork.storage.simplejson.Ignores;

/**
 * @author thomas
 *
 */
public class ClassCache {
    private static final WeakHashMap<Class<?>, ClassCache> CACHE        = new WeakHashMap<Class<?>, ClassCache>();
    private static final Object[]                          EMPTY_OBJECT = new Object[] {};
    private static final Class<?>[]                        EMPTY_TYPES  = new Class[] {};

    protected static ClassCache create(final Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
        return create(clazz, null);
    }

    public static class Rules {
        private Class<?> breakAtClass;

        /**
         * @return the breakAtClass
         */
        public Class<?> getBreakAtClass() {
            return breakAtClass;
        }

        /**
         * @param breakAtClass
         *            the breakAtClass to set
         */
        public void setBreakAtClass(Class<?> breakAtClass) {
            this.breakAtClass = breakAtClass;
        }

        public Rules breakAtClass(Class<?> breakAtClass) {
            this.breakAtClass = breakAtClass;
            return this;
        }
    }

    /**
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public static ClassCache create(final Class<? extends Object> clazz, Rules rules) throws SecurityException, NoSuchMethodException {
        final ClassCache cc = new ClassCache(clazz);
        Getter g;
        Setter s;
        Class<? extends Object> cls = clazz;
        final HashSet<String> ignores = new HashSet<String>();
        do {
            if (rules != null && rules.getBreakAtClass() == cls) {
                break;
            }
            final Ignores ig = cls.getAnnotation(Ignores.class);
            if (ig != null) {
                for (final String i : ig.value()) {
                    ignores.add(i);
                }
            }
            for (final Method m : cls.getDeclaredMethods()) {
                if (m.getAnnotation(Ignore.class) != null || ignores.contains(m.toString())) {
                    continue;
                }
                if (m.getName().startsWith("get") && m.getParameterTypes().length == 0 && m.getReturnType() != void.class) {
                    cc.getter.add(g = new Getter(createKey(m.getName().substring(3)), m));
                    cc.getterMap.put(g.getKey(), g);
                    // org.appwork.loggingv3.LogV3.finer(m.toString());
                } else if (m.getName().startsWith("is") && m.getParameterTypes().length == 0 && m.getReturnType() != void.class) {
                    cc.getter.add(g = new Getter(createKey(m.getName().substring(2)), m));
                    cc.getterMap.put(g.getKey(), g);
                    // org.appwork.loggingv3.LogV3.finer(m.toString());
                } else if (m.getName().startsWith("set") && m.getParameterTypes().length == 1) {
                    cc.setter.add(s = new Setter(createKey(m.getName().substring(3)), m));
                    cc.setterMap.put(s.getKey(), s);
                    // org.appwork.loggingv3.LogV3.finer(m.toString());
                }
            }
        } while ((cls = cls.getSuperclass()) != null && cls != Object.class);
        // we do not want to serialize object's getter
        for (final Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0) {
                try {
                    c.setAccessible(true);
                    cc.constructor = c;
                } catch (final java.lang.SecurityException e) {
                    org.appwork.loggingv3.LogV3.log(e);
                }
                break;
            }
        }
        if (cc.constructor == null) {
            //
            final int lastIndex = clazz.getName().lastIndexOf(".");
            final String pkg = lastIndex > 0 ? clazz.getName().substring(0, lastIndex) : "";
            if (pkg.startsWith("java") || pkg.startsWith("sun.")) {
                org.appwork.loggingv3.LogV3.warning("No Null Constructor in " + clazz + " found. De-Json-serial will fail");
            } else {
                throw new NoSuchMethodException(" Class " + clazz + " requires a null constructor. please add private " + clazz.getSimpleName() + "(){}");
            }
        }
        return cc;
    }

    /**
     * @return the parameterizedTypesMap
     */
    private HashMap<Class, ParameterizedType> getParameterizedTypesMap() {
        if (parameterizedTypesMap == null) {
            initTypeHirarchy();
        }
        return parameterizedTypesMap;
    }

    /**
     * @return
     */
    private void initTypeHirarchy() {
        HashMap<Class, ParameterizedType> parameterizedTypesMap = new HashMap<Class, ParameterizedType>();
        HashMap<Type, Type> extendedTypesMap = new HashMap<Type, Type>();
        Type start = clazz;
        while (true) {
            Type sClass = null;
            if (start instanceof Class) {
                sClass = ((Class) start).getGenericSuperclass();
                extendedTypesMap.put(sClass, start);
            } else if (start instanceof ParameterizedType) {
                Type r = ((ParameterizedType) start).getRawType();
                if (r instanceof Class) {
                    sClass = ((Class) r).getGenericSuperclass();
                    parameterizedTypesMap.put((Class) r, (ParameterizedType) start);
                    extendedTypesMap.put(sClass, start);
                } else {
                    LogV3.I().getDefaultLogger().log(new Exception("This should not happen. " + clazz));
                    extendedTypesMap.put(r, start);
                }
            } else {
                // GenericArrayType
                // TypeVariable
            }
            if (sClass != null && sClass instanceof ParameterizedType) {
                extendedTypesMap.put(((ParameterizedType) sClass).getRawType(), start);
            }
            if (sClass == null) {
                break;
            }
            start = sClass;
        }
        this.extendedTypesMap = extendedTypesMap;
        this.parameterizedTypesMap = parameterizedTypesMap;
    }

    /**
     *
     * Jackson maps methodnames to keys like this. setID becomes key "id" , setMethodName becomes "methodName". To keep compatibility
     * between jackson and simplemapper, we should do it the same way
     *
     * @param substring
     * @return
     */
    public static String createKey(final String key) {
        final StringBuilder sb = new StringBuilder();
        final char[] ca = key.toCharArray();
        boolean starter = true;
        for (int i = 0; i < ca.length; i++) {
            if (starter && Character.isUpperCase(ca[i])) {
                sb.append(Character.toLowerCase(ca[i]));
            } else {
                starter = false;
                sb.append(ca[i]);
            }
        }
        return sb.toString();
    }

    /**
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public synchronized static ClassCache getClassCache(final Class<? extends Object> clazz) throws SecurityException, NoSuchMethodException {
        ClassCache cc = ClassCache.CACHE.get(clazz);
        if (cc == null) {
            LogV3.logger(ClassCache.class).finer("ClassCache: " + clazz);
            cc = ClassCache.create(clazz);
            ClassCache.CACHE.put(clazz, cc);
        }
        return cc;
    }

    protected Constructor<? extends Object>   constructor;
    protected final Class<? extends Object>   clazz;
    protected final java.util.List<Getter>    getter;
    protected final java.util.List<Setter>    setter;
    protected final HashMap<String, Getter>   getterMap;
    protected final HashMap<String, Setter>   setterMap;
    private HashMap<Type, Type>               extendedTypesMap;
    private HashMap<Class, ParameterizedType> parameterizedTypesMap;

    /**
     * @param clazz
     */
    protected ClassCache(final Class<? extends Object> clazz) {
        this.clazz = clazz;
        getter = new ArrayList<Getter>();
        setter = new ArrayList<Setter>();
        getterMap = new HashMap<String, Getter>();
        setterMap = new HashMap<String, Setter>();
    }

    /**
     * @return the parentsMap
     */
    private HashMap<Type, Type> getExtendedTypesMap() {
        if (extendedTypesMap == null) {
            initTypeHirarchy();
        }
        return extendedTypesMap;
    }

    public java.util.List<Getter> getGetter() {
        return getter;
    }

    public Getter getGetter(final String key) {
        return getterMap.get(key);
    }

    /**
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    public Object getInstance() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return constructor.newInstance(ClassCache.EMPTY_OBJECT);
    }

    public java.util.List<Setter> getSetter() {
        return setter;
    }

    public Setter getSetter(final String key) {
        return setterMap.get(key);
    }

    /**
     * @return
     */
    public Set<String> getKeys() {
        HashSet<String> ret = new HashSet<String>();
        ret.addAll(getterMap.keySet());
        ret.addAll(setterMap.keySet());
        return ret;
    }

    /**
     * If the parameter cls is type of this classcache (is part of the class hirarchy), this will return the parameterized instance.<br>
     *
     * example: A extends B<String> getParameterizedType(B.class) =B<String>
     *
     * @param cls
     * @return
     */
    public ParameterizedType getParameterizedType(Class cls) {
        return getParameterizedTypesMap().get(cls);
    }

    /**
     * Gets the extended Type. example: class A extends B -> getExtendedType(B.class) = A.class
     *
     * @param cls
     * @return
     */
    public Type getExtendedType(Class cls) {
        return getExtendedTypesMap().get(cls);
    }
}
