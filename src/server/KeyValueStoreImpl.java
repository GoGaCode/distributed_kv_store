package server;

import utils.LoggerUtils;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class KeyValueStoreImpl extends UnicastRemoteObject implements KeyValueStore {
  // Using singleton pattern to allow only one instance of the store
  private Map<String, String> store;
  private static int waitTime = 5000; // in mili-seconds
  private static KeyValueStoreImpl kvStore;

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }

  private KeyValueStoreImpl() throws RemoteException {
    super();
    // Initialize the store
    store = new HashMap<>();
  }

  public static KeyValueStoreImpl getInstance() throws RemoteException {
    if (kvStore == null) {
      kvStore = new KeyValueStoreImpl();
    }
    return kvStore;
  }

  public synchronized void put(String key, String value) throws RemoteException {
    LoggerUtils.logServer("Storing " + key + " -> " + value);
    store.put(key, value);
    sleepForSeconds(waitTime);
  }

  public synchronized String get(String key) throws RemoteException {
    LoggerUtils.logServer("Retrieving " + key + " -> " + store.get(key));
    sleepForSeconds(waitTime);
    return store.get(key);
  }

  public synchronized void delete(String key) throws RemoteException {
    LoggerUtils.logServer("Deleting " + key);
    store.remove(key);
    sleepForSeconds(waitTime);
  }

  public static void sleepForSeconds(int seconds) {
    try {
      Thread.sleep(waitTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }
}
