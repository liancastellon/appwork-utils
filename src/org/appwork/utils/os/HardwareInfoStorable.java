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
package org.appwork.utils.os;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.appwork.storage.Storable;
import org.appwork.utils.CompareUtils;
import org.appwork.utils.Hash;
import org.appwork.utils.Regex;
import org.appwork.utils.StringUtils;

/**
 * @author Thomas
 * @date 11.03.2019
 *
 */
public class HardwareInfoStorable implements Storable {
    public static final org.appwork.storage.TypeRef<HardwareInfoStorable> TYPE = new org.appwork.storage.TypeRef<HardwareInfoStorable>(HardwareInfoStorable.class) {
    };

    /**
     *
     */
    public HardwareInfoStorable() {
        // TODO Auto-generated constructor stub
    }

    private String hid;

    /**
     * @param strings
     * @param
     * @return the hwid
     */
    public String toHid(String[] includes, String[] excludes) {
        return Hash.getSHA256(toIDString(includes, excludes));
    }

    /**
     * @param includes
     * @param excludes
     * @return
     */
    public String toIDString(String[] includes, String[] excludes) {
        return toIDString(toFilteredList(includes, excludes));
    }

    protected ArrayList<Entry> toFilteredList(String[] includes, String[] excludes) {
        ArrayList<Entry> copy = new ArrayList<Entry>();
        main: for (Entry e : entries) {
            boolean i = includes == null;
            if (!i) {
                for (String p : includes) {
                    i |= (e.getCategory() + "." + e.key).matches(p);
                }
            }
            if (excludes != null) {
                for (String p : excludes) {
                    if ((e.getCategory() + "." + e.key).matches(p)) {
                        continue main;
                    }
                }
            }
            if (i) {
                copy.add(e);
            }
        }
        return copy;
    }

    private ArrayList<Entry> entries = new ArrayList<Entry>();
    private long             time;

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return the entries
     */
    public ArrayList<Entry> getEntries() {
        return entries;
    }

    /**
     * @param entries
     *            the entries to set
     */
    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public static class Entry implements Storable {
        private String category;
        private String key;
        private String value;
        private int    index;

        /**
         *
         */
        public Entry() {
            // TODO Auto-generated constructor stub
        }

        public Entry(String category, int index, String key, String value) {
            this.category = category;
            this.key = key;
            this.value = value;
            this.index = index;
        }

        /**
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * @param index
         *            the index to set
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * @return the category
         */
        public String getCategory() {
            return category;
        }

        /**
         * @param category
         *            the category to set
         */
        public void setCategory(String category) {
            this.category = category;
        }

        /**
         * @return the key
         */
        public String getKey() {
            return key;
        }

        /**
         * @param key
         *            the key to set
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * @param category
     * @param index
     * @param key
     * @param value
     */
    public void add(String category, int index, String key, String value) {
        entries.add(new Entry(category, index, key, value));
    }

    /**
     * Creates a identifier string. This String should not change without a good reason because it is used to identify systems.
     *
     * @param object
     */
    public String toIDString(ArrayList<Entry> entries) {
        sort(entries);
        StringBuilder sb = new StringBuilder();
        Entry last = null;
        HashSet<String> dupes = new HashSet<String>();
        int maxKey = 0;
        int maxValue = 0;
        for (Entry e : entries) {
            if ("PNPDeviceID".equalsIgnoreCase(e.key) || "DeviceID".equalsIgnoreCase(e.key)) {
                maxKey = Math.max(maxKey, "VendorID".length());
                maxKey = Math.max(maxKey, "DeviceID".length());
                maxKey = Math.max(maxKey, "SubSysID".length());
                maxValue = Math.max(maxValue, 10);
            } else {
                maxKey = Math.max(maxKey, e.key.length());
                maxValue = Math.max(maxValue, e.value.length());
            }
        }
        for (Entry e : entries) {
            if (last == null || !StringUtils.equals(e.category, last.category)) {
                sb.append(StringUtils.fillPost(" == " + e.category + " =", "=", maxKey + maxValue + 6)).append("\r\n");
            }
            if (last == null || !StringUtils.equals(e.category, last.category) || e.getIndex() != last.getIndex()) {
                sb.append(StringUtils.fillPost("   " + (e.getIndex() + 1) + ".", "-", maxKey + maxValue + 6)).append("\r\n");
            }
            // https://devicehunt.com/search/type/pci/vendor/5853/device/1003
            if ("PNPDeviceID".equalsIgnoreCase(e.key) || "DeviceID".equalsIgnoreCase(e.key)) {
                // https://www.keysight.com/main/editorial.jspx?ckey=2039700&id=2039700&nid=-11143.0.00&lc=ger&cc=DE
                String vendor = new Regex(e.value, "[\\\\;]VEN_([a-fA-F0-9]+)").getMatch(0);
                String device = new Regex(e.value, "[\\\\;]DEV_([a-fA-F0-9]+)").getMatch(0);
                String subsys = new Regex(e.value, "[\\\\;]SUBSYS_([a-fA-F0-9]+)").getMatch(0);
                if (StringUtils.isNotEmpty(vendor) && dupes.add("vendor-" + e.index + "=" + vendor)) {
                    sb.append("   " + StringUtils.fillPost("VendorID", " ", maxKey)).append(" = ").append(vendor).append("\r\n");
                }
                if (StringUtils.isNotEmpty(device) && dupes.add("device-" + e.index + "=" + vendor)) {
                    sb.append("   " + StringUtils.fillPost("DeviceID", " ", maxKey)).append(" = ").append(device).append("\r\n");
                    // sb.append("\t" + StringUtils.fillPost(e.key, " ", 30)).append("\t = \t").append(e.value).append("\r\n");
                }
                if (StringUtils.isNotEmpty(vendor) && dupes.add("subsys-" + e.index + "=" + subsys)) {
                    sb.append("   " + StringUtils.fillPost("SubSysID", " ", maxKey)).append(" = ").append(subsys).append("\r\n");
                }
            } else {
                sb.append("   " + StringUtils.fillPost(e.key, " ", maxKey)).append(" = ").append(e.value).append("\r\n");
            }
            last = e;
        }
        return sb.toString();
    }

    protected void sort(ArrayList<Entry> entries) {
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                int ret = CompareUtils.compare(o1.category, o2.category);
                if (ret != 0) {
                    return ret;
                }
                ret = CompareUtils.compare(o1.index, o2.index);
                if (ret != 0) {
                    return ret;
                }
                ret = CompareUtils.compare(o1.key, o2.key);
                if (ret != 0) {
                    return ret;
                }
                return CompareUtils.compare(o1.value, o2.value);
            }
        });
    }

    /**
     * @return
     */
    public String toIDString() {
        // TODO Auto-generated method stub
        return toIDString(entries);
    }
}
