package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Coordinator and participant are both part of two phase commit protocol.
 * Coordinator interface is responsible for
 *  -starting the two phase commit protocol.
 *  -starting the local commit.
 *  -getting the decision of the transaction.
 *  -collecting the votes of the transaction.
 *  -committing the transaction.
 *  -aborting the transaction.
 */
public interface Coordinator extends Remote {
  boolean startTwoPhaseCommit(Proposal proposal) throws RemoteException;
  String startLocalCommit(Proposal proposal) throws RemoteException;
  boolean getDecision(Proposal proposal) throws RemoteException;
  boolean collectVotes(Proposal proposal) throws RemoteException;
  boolean commit(Proposal proposal) throws RemoteException;
  boolean abort(Proposal proposal) throws RemoteException;
}
