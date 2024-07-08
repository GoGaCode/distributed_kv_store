package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HttpOps extends Remote {
    boolean put(String key, String value) throws RemoteException;
    String get(String key) throws RemoteException;
    boolean delete(String key) throws RemoteException;
    void setWaitTime(int waitTime) throws RemoteException;
}
