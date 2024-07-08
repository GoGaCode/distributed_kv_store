package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import utils.LoggerUtils;
import utils.httpType;

public class HttpOpsImpl extends UnicastRemoteObject implements HttpOps {

  private Coordinator coordinator;
  protected static int waitTime = 1000; // in milliseconds
  private int serverIndex;

  public HttpOpsImpl(int serverIndex) throws RemoteException {
    // Initialize the store
    this.serverIndex = serverIndex;
  }

  @Override
  public synchronized boolean put(String key, String value) throws RemoteException {
    Transaction transaction = new Transaction(httpType.PUT, key, value, this.serverIndex);
    if (coordinator.startTwoPhaseCommit(transaction)) {
      LoggerUtils.logServer("Storing " + key + " -> " + value + " successfully");
      return true;
    } else {
      LoggerUtils.logServer("Storing " + key + " -> " + value + " failed");
      return false;
    }
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    Transaction transaction = new Transaction(httpType.GET, key, null, this.serverIndex);
    return coordinator.startLocalCommit(transaction);
  }

  public void setCoordinator(Coordinator coordinator) {
    this.coordinator = coordinator;
  }

  @Override
  public synchronized boolean delete(String key) throws RemoteException {
    Transaction transaction = new Transaction(httpType.DELETE, key, null, this.serverIndex);
    if (coordinator.startTwoPhaseCommit(transaction)) {
      LoggerUtils.logServer("Deleting " + key + " successfully");
      return true;
    } else {
      LoggerUtils.logServer("Deleting " + key + " failed");
      return false;
    }
  }

  public void setWaitTime(int waitTime) {
    HttpOpsImpl.waitTime = waitTime;
  }
}
