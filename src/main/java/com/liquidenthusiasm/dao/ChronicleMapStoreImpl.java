package com.liquidenthusiasm.dao;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.openhft.chronicle.map.ChronicleMap;

public abstract class ChronicleMapStoreImpl<T> extends AbstractStore<T> {

    private static final Logger log = LoggerFactory.getLogger(ChronicleMapStoreImpl.class);

    private ChronicleMap<String, T> cmstore;

    public ChronicleMapStoreImpl(Class<T> storedClass, String averageKey, T averageValue, int expectedEntryCount) {
        cmstore = ChronicleMap.of(String.class, storedClass)
            .entries(expectedEntryCount)
            .averageKey(averageKey)
            .averageValue(averageValue)
            .create();
    }

    public ChronicleMapStoreImpl(Class<T> storedClass, String filename, String averageKey, T averageValue, int expectedEntryCount)
        throws IOException {
        cmstore = ChronicleMap.of(String.class, storedClass)
            .entries(expectedEntryCount)
            .averageKey(averageKey)
            .averageValue(averageValue)
            .createPersistedTo(new File(filename));
    }

    @Override public void shutdownStore() {
        cmstore.close();
    }

    @Override public void save(T entry) {
        cmstore.put(generateKey(entry), entry);
    }

    @Override public T get(String key) {
        return cmstore.get(key);
    }
}
