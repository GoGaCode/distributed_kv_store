package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface kvStoreOps extends Remote, Server {
  boolean put(String key, String value) throws RemoteException;

  String get(String key) throws RemoteException;

  boolean delete(String key) throws RemoteException;

  boolean isInitialized() throws RemoteException;

  void setInitialized(boolean initialized) throws RemoteException;
}
