package server;

import java.util.HashMap;
import java.util.Map;

public class KeyValueStore {
    private Map<String, String> store;

    public KeyValueStore() {
        store = new HashMap<>();
    }

    public void put(String key, String value) {
        store.put(key, value);
    }

    public String get(String key) {
        return store.get(key);
    }

    public void delete(String key) {
        store.remove(key);
    }
}
