package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Proposer extends Remote {
  /**
   * ID = cnt++;
   * send PREPARE(ID)
   *
   * @param proposal
   * @throws RemoteException
   */
  boolean prepare(Proposal proposal) throws RemoteException;

  /**
   * The proposer now checks to see if it can use its proposal or if it has to use the highest-numbered one it received from among all responses:
   * did I receive PROMISE responses from a majority of acceptors?
   * if yes
   * do any responses contain accepted values (from other proposals)?
   * if yes
   * val = accepted_VALUE    // value from PROMISE message with the highest accepted ID
   * if no
   * val = VALUE     // we can use our proposed value
   * send PROPOSE(ID, val) to at least a majority of acceptors
   *
   * @param proposal@throws RemoteException
   */
  boolean propose(Proposal proposal) throws RemoteException;
}

