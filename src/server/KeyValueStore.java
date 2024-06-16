package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface KeyValueStore extends Remote {
    void put(String key, String value) throws RemoteException;
    String get(String key) throws RemoteException;
    void delete(String key) throws RemoteException;
}
