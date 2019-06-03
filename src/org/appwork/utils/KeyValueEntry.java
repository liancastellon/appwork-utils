package org.appwork.utils;

import java.util.Map.Entry;

public class KeyValueEntry<KeyType, ValueType> extends ValueEntry<ValueType> {
    private KeyType key;

    public KeyType getKey() {
        return key;
    }

    public void setKey(KeyType key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return key + "=" + getValue();
    }

    /**
     * @param key
     * @param value
     */
    public KeyValueEntry(KeyType key, ValueType value) {
        this.key = key;
        setValue(value);
    }

    /**
     * @param entry
     */
    public KeyValueEntry(Entry<KeyType, ValueType> entry) {
        this(entry.getKey(), entry.getValue());
    }

    /**
     *
     */
    public KeyValueEntry(/* STorable */) {
        // TODO Auto-generated constructor stub
    }
}
