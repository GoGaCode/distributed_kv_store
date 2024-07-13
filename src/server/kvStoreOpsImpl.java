package server;

import static utils.Constant.INIT_FLAG_KEY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import utils.LoggerUtils;
import utils.opsType;

public class kvStoreOpsImpl extends UnicastRemoteObject implements kvStoreOps {

  private Coordinator coordinator;
  private int serverIndex;

  public kvStoreOpsImpl(int serverIndex) throws RemoteException {
    // Initialize the store
    this.serverIndex = serverIndex;
  }

  @Override
  public synchronized boolean put(String key, String value) throws RemoteException {
    Transaction transaction = new Transaction(opsType.PUT, key, value, this.serverIndex);
    if (coordinator.startTwoPhaseCommit(transaction)) {
      LoggerUtils.logServer("Storing " + key + " -> " + value + " successfully", this.serverIndex);
      return true;
    } else {
      LoggerUtils.logServer("Storing " + key + " -> " + value + " failed", this.serverIndex);
      return false;
    }
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    Transaction transaction = new Transaction(opsType.GET, key, null, this.serverIndex);
    return coordinator.startLocalCommit(transaction);
  }

  public void setCoordinator(Coordinator coordinator) {
    this.coordinator = coordinator;
  }

  @Override
  public synchronized boolean delete(String key) throws RemoteException {
    Transaction transaction = new Transaction(opsType.DELETE, key, null, this.serverIndex);
    if (coordinator.startTwoPhaseCommit(transaction)) {
      LoggerUtils.logServer("Deleting " + key + " successfully", this.serverIndex);
      return true;
    } else {
      LoggerUtils.logServer("Deleting " + key + " failed", this.serverIndex);
      return false;
    }
  }

  @Override
  public boolean isInitialized() throws RemoteException {
    String value = this.get(INIT_FLAG_KEY);
    return "true".equals(value);
  }

  @Override
  public void setInitialized(boolean initialized) throws RemoteException {
    this.put(INIT_FLAG_KEY, "true");
  }
}
