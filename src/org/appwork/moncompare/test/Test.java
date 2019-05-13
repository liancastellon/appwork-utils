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
package org.appwork.moncompare.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.appwork.moncompare.CompareException;
import org.appwork.moncompare.Condition;
import org.appwork.storage.JSonStorage;
import org.appwork.storage.simplejson.ParserException;
import org.appwork.storage.simplejson.mapper.MapperException;
import org.appwork.utils.Application;

/**
 * @author Thomas
 * @date 06.05.2019
 *
 */
public class Test extends Condition {
    /**
     *
     */
    /**
     *
     */
    public static class EmbededObject {
        private ArrayList<String> list = new ArrayList<String>();
        {
            this.list.add("s1");
            this.list.add("s2");
        }
        private ArrayList<String[]> list2 = new ArrayList<String[]>();
        {
            this.list2.add(new String[] { "a", "b" });
            this.list2.add(new String[] { "a", "b" });
        }
        private boolean b = false;
        private boolean c = false;

        /**
         * @return the b
         */
        public boolean isB() {
            return this.b;
        }

        /**
         * @param b
         *            the b to set
         */
        public void setB(boolean b) {
            this.b = b;
        }

        private int i = 3;
    }

    public static void main(String[] args) throws CompareException, MapperException, ParserException {
        Application.setApplication(".test");
        HashMap<String, Object> test = new HashMap<String, Object>();
        test.put("a", 1);
        test.put("sa", new String[] { "a", "b" });
        test.put("obj", new EmbededObject());
        test.put("list", new int[] { 1, 2, 3, 5, 4, 6, 7, 8, 9 });
        Condition query = new Condition();
        ArrayList<Condition> cond = new ArrayList<Condition>();
        cond.add(new Condition("list", new Condition($IN, new int[] { 1 })));
        cond.add(new Condition("list", new Condition($NIN, new int[] { 5 })));
        cond.add(new Condition("list", new Condition($OR, new Condition[] { new Condition($IN, new int[] { 2 }), new Condition($IN, new int[] { 3 }) })));
        query.append($AND, cond);
        System.out.println(query.matches(test));
        System.out.println(JSonStorage.serializeToJson(query));
        Condition con = new Condition("obj.c", false);
        long started = System.currentTimeMillis();
        boolean success = true;
        for (int i = 0; i < 1000000; i++) {
            success &= con.matches(test);
        }
        System.out.println(success + " " + (System.currentTimeMillis() - started));
        System.out.println(new Condition("obj.getClass.getSimpleName", new Condition("§regex", ".*embed.*").append("§options", "ims")).matches(test));
        System.out.println(new Condition("obj.b", new Condition($EQ, false)).append("obj.a", new Condition($EXISTS, false)).matches(test));
        System.out.println(new Condition("obj.b", new Condition($EQ, false)).append("obj.a", new Condition($EXISTS, false)).matches(test));
        con = new Condition("obj.list", new Condition($EQ, new String[] { "s1", "s2" })).append("obj.list2", new Condition($EQ, new Object[] { new Condition($REGEX, ".*").append($OPTIONS, "m"), "b" }));
        System.out.println(con.matches(test));
        System.out.println(new Condition("a", new Condition($IN, new int[] { 3, 2, 1 })).matches(test));
        System.out.println(new Condition("a", new Condition($NIN, new int[] { 4, 5, 6 })).matches(test));
        System.out.println(new Condition("a", 1).matches(test));
        System.out.println(new Condition($NOT, new Condition("a", 2)).matches(test));
        System.out.println(new Condition($TYPE, HashMap.class.getName()).matches(test));
        System.out.println(new Condition("obj", new Condition($TYPE, new Condition($REGEX, ".*Embed.*"))).matches(test));
        System.out.println(new Condition("a", new Condition($NE, 2)).append("c", new Condition($NE, 1)).matches(test));
        System.out.println(new Condition($OR, new Condition[] { new Condition("a", 2), new Condition("a", new Condition($NE, 2)).append("c", new Condition($NE, 1)) }).matches(test));
        System.out.println(new Condition($AND, new Condition[] { new Condition("a", 1), new Condition("a", new Condition($NE, 2)).append("c", new Condition($NE, 1)) }).matches(test));
        System.out.println(new Condition("a", new Condition($EQ, 1)).matches(test));
        System.out.println(new Condition("a", new Condition($GTE, 1).append($LT, 2)).matches(test));
        System.out.println(new Condition("sa.0", "a").matches(test));
        System.out.println(new Condition("sa", new Condition($IN, new String[] { "h", "b" })).matches(test));
        System.out.println(new Condition("sa", new Condition($NIN, new String[] { "c", "d" })).matches(test));
        String json = JSonStorage.serializeToJson(new Condition("sa", new Condition($IN, new String[] { "h", "b" })));
        System.out.println(json);
        query = JSonStorage.restoreFromString(json, Condition.TYPE);
        System.out.println(query.matches(test));
    }
}
