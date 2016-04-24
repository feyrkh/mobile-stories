package com.liquidenthusiasm.dao;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStore<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractStore.class);

    private static final Map<Class, AbstractStore> stores = new HashMap<>();

    public static AbstractStore getStore(Class storeClass) {
        AbstractStore store = stores.get(storeClass);
        if (store == null) {
            throw new RuntimeException("Must create store for " + storeClass.getName() + " first!");
        }
        return store;
    }

    public static <StoreType> void setStore(Class<StoreType> storeClass, AbstractStore<StoreType> store) {
        deleteStore(storeClass);
        stores.put(storeClass, store);
    }

    public static void deleteStore(Class storeClass) {
        AbstractStore store = stores.get(storeClass);
        if (store != null) {
            store.shutdownStore();
        }
        stores.remove(storeClass);

    }

    public abstract void shutdownStore();

    public abstract void save(T entry);

    public abstract T get(String key);

    public abstract String generateKey(T entry);

    public static void clearStores() {
        stores.clear();
    }

    public static boolean hasStore(Class cls) {
        return stores.containsKey(cls);
    }
}
