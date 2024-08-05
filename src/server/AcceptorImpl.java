package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static utils.Constant.*;

public class AcceptorImpl extends UnicastRemoteObject implements Acceptor {

  private long max_transId = 0;
  private Proposal accepted_proposal;
  private Learner learner;
  private int serverIndex;

  public AcceptorImpl(int serverIndex) throws RemoteException {
    super();
    this.serverIndex = serverIndex;
  }

  @Override
  public Proposal Promise(Proposal proposal) throws RemoteException {
    // TODO: ensure ID always increasing
    if (proposal.getTransId() > max_transId) {
      max_transId = proposal.getTransId();
      if (accepted_proposal != null) {
        return accepted_proposal;
      }
      return proposal;
    }
    return null;
  }

  @Override
  public boolean Accept(Proposal proposal) throws RemoteException {
    if (proposal.getTransId() == max_transId) {
      accepted_proposal = proposal;
      // TODO: all learns learn proposal
      if (learner.learn(proposal) != null) {
        accepted_proposal = null;
      }
      ;
      return true;
    }
    return false;
  }

  @Override
  public int getServerIndex() {
    return serverIndex;
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
      String acceptorName = ACCEPTOR_PREFIX + serverIndex;
      registry.rebind(acceptorName, this);
      System.out.println("Acceptor " + acceptorName + " bound to registry.");

      // Get the learner from the registry
      boolean learnerFound = false;
      while (!learnerFound) {
        String learnerName = LEARNER_PREFIX + serverIndex;
        try {
          learner = (Learner) registry.lookup(learnerName);
          learnerFound = true;
        } catch (Exception e) {
          System.out.println(learnerName + " not found in registry. Retrying...");
          Thread.sleep(1000);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
