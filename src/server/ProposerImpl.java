package server;

import static utils.Constant.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import utils.IDGeneratorImpl;

public class ProposerImpl extends UnicastRemoteObject implements Proposer {

  private IDGenerator idGenerator;
  private int serverIndex;
  private Proposal accepted_proposals[] = new Proposal[SERVER_COUNT];
  Registry registry = LocateRegistry.getRegistry(1099);

  public ProposerImpl(Integer serverIndex) throws RemoteException {
    this.idGenerator = new IDGeneratorImpl();
    this.serverIndex = serverIndex;
    this.registry = LocateRegistry.getRegistry(1099);
    try {
      this.idGenerator = (IDGenerator) registry.lookup(ID_GENERATOR_NAME);
    } catch (NotBoundException e) {
      throw new RuntimeException(e);
    }
  }

  private IDGenerator getIdGenerator() {
    return idGenerator;
  }

  @Override
  public boolean prepare(Proposal proposal) throws RemoteException {
    int promiseCount = 0;
    for (int i = 0; i < SERVER_COUNT; i++) {
      String acceptorName = ACCEPTOR_PREFIX + i;
      Acceptor acceptor = null;
      try {
        acceptor = (Acceptor) registry.lookup(acceptorName);
      } catch (NotBoundException e) {
        throw new RuntimeException(e);
      }
      Proposal accepted_proposal = acceptor.Promise(proposal);
      if (accepted_proposal != null) {
        promiseCount++;
        accepted_proposals[acceptor.getServerIndex()] = accepted_proposal;
        // If other proposal being accepted, update the proposal
      }
      ;
    }
    if (promiseCount < Math.ceil(SERVER_COUNT / 2)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean propose(Proposal proposal) throws RemoteException {
    // Send ID generated through idGenerator to all other instances
    // TODO: check if majority accepted
    for (Proposal accepted_proposal : accepted_proposals) {
      if (accepted_proposal != proposal) {
        proposal = accepted_proposal;
        break;
      }
    }
    int accepted_count = 0;
    for (int i = 0; i < SERVER_COUNT; i++) {
      String acceptorName = ACCEPTOR_PREFIX + i;
      Acceptor acceptor = null;
      try {
        acceptor = (Acceptor) registry.lookup(acceptorName);
        if (acceptor.Accept(proposal)) {
          accepted_count++;
        }
        ;
      } catch (NotBoundException e) {
        throw new RuntimeException(e);
      }
    }
    return true;
  }

  @Override
  public void run() {
    try {
      // Get or create the registry on port 1099
      Registry registry;
      try {
        registry = LocateRegistry.getRegistry(1099);
        registry.list(); // Check if registry already exists
      } catch (Exception e) {
        registry = LocateRegistry.createRegistry(1099);
        System.out.println("Created new RMI registry on port 1099.");
      }
      // Bind this acceptor to the registry
      String proposerName = PROPOSER_PREFIX + serverIndex;
      registry.rebind(proposerName, this);
      System.out.println(proposerName + " bound to registry.");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
