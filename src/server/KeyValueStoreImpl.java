package server;


import utils.LoggerUtils;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class KeyValueStoreImpl extends UnicastRemoteObject implements KeyValueStore {
    private Map<String, String> store;

    public KeyValueStoreImpl() throws RemoteException {
        super();
        // Initialize the store
        store = new HashMap<>();
    }

    public synchronized void put(String key, String value) throws RemoteException{
        LoggerUtils.logServer( "Storing " + key + " -> " + value);
        store.put(key, value);
    }

    public synchronized String get(String key) throws RemoteException{
        LoggerUtils.logServer( "Retrieving " + key + " -> " + store.get(key));
        return store.get(key);
    }

    public synchronized void delete(String key) throws RemoteException{
        LoggerUtils.logServer( "Deleting " + key);
        store.remove(key);
    }
}
