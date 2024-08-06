package server;

import static utils.Constant.*;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import utils.IDGenerator;
import utils.opsType;

public class kvStoreOpsPaxos extends UnicastRemoteObject implements kvStoreOps {

  private Proposer proposer;
  private Learner learner;
  private int serverIndex;
  private IDGenerator idGenerator;

  private boolean running = true;

  public kvStoreOpsPaxos(int serverIndex) throws RemoteException {
    // Initialize the store
    this.serverIndex = serverIndex;
    this.idGenerator = new IDGenerator(serverIndex);
  }

  @Override
  public synchronized boolean put(String key, String value) throws RemoteException {
    Proposal proposal =
        new Proposal(opsType.PUT, key, value, this.serverIndex, this.idGenerator.nextId());
    if (!proposer.prepare(proposal)) {
      return false;
    }
    ;
    proposer.propose(proposal);
    return true;
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    Proposal proposal =
        new Proposal(opsType.GET, key, null, this.serverIndex, this.idGenerator.nextId());
    return learner.learn(proposal);
  }

  @Override
  public synchronized boolean delete(String key) throws RemoteException {
    Proposal proposal =
        new Proposal(opsType.DELETE, key, null, this.serverIndex, this.idGenerator.nextId());
    if (!proposer.prepare(proposal)) {
      return false;
    }
    ;
    proposer.propose(proposal);
    return true;
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

  @Override
  public void run() {

    Registry registry;
    try {
      registry = LocateRegistry.getRegistry(1099);
      registry.list(); // Check if registry already exists

      String kvStoreName = KV_STORE_OPS_PREFIX + serverIndex;
      registry.rebind(kvStoreName, this);
      System.out.println(kvStoreName + " bound to registry.");

      boolean registrationDone = false;
      String learnerName = LEARNER_PREFIX + serverIndex;
      String proposerName = PROPOSER_PREFIX + serverIndex;
      while (!registrationDone) {
        try {
          // Get the learner from the registry
          learner = (Learner) registry.lookup(learnerName);
          System.out.println(learnerName + " retrieved from registry.");

          // Get the proposer from the registry
          proposer = (Proposer) registry.lookup(proposerName);
          System.out.println(proposerName + " retrieved from registry.");
          registrationDone = true;
        } catch (Exception e) {
          System.out.println("Waiting for all the threads to be registered.");
          Thread.sleep(1000);
        }
      }
    } catch (AccessException e) {
      throw new RuntimeException(e);
    } catch (RemoteException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
