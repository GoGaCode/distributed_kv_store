package server;

import static utils.Constant.INIT_FLAG_KEY;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import utils.IDGenerator;
import utils.opsType;

public class kvStoreOpsPaxos extends UnicastRemoteObject implements kvStoreOps {

  private Proposer proposer;
  private Acceptor acceptor;
  private Learner learner;
  private int serverIndex;
  private IDGenerator idGenerator;

  public kvStoreOpsPaxos(int serverIndex) throws RemoteException {
    // Initialize the store
    this.serverIndex = serverIndex;
    this.idGenerator = new IDGenerator(serverIndex);
  }

  @Override
  public synchronized boolean put(String key, String value) throws RemoteException {
    Proposal proposal = new Proposal(opsType.PUT, key, value, this.serverIndex, this.idGenerator.nextId());
    if (!proposer.prepare(proposal)){
      // Less than half of the acceptors accepted the proposal
      return false;
    };
    proposer.propose(proposal);
    return true;
  }

  @Override
  public synchronized String get(String key) throws RemoteException {
    Proposal proposal = new Proposal(opsType.GET, key, null, this.serverIndex, this.idGenerator.nextId());
    return learner.learn(proposal);
  }

  @Override
  public synchronized boolean delete(String key) throws RemoteException {
    Proposal proposal = new Proposal(opsType.DELETE, key, null, this.serverIndex, this.idGenerator.nextId());
    if (!proposer.prepare(proposal)){
      return false;
    };
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

  public void setProposer(Proposer proposer) {
    this.proposer = proposer;
  }

  public void setLearner(Learner learner) {
    this.learner = learner;
  }

  public void setAcceptors(Acceptor acceptor) {
    this.acceptor = acceptor;
  }

}
