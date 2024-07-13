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
  boolean startTwoPhaseCommit(Transaction transaction) throws RemoteException;
  String startLocalCommit(Transaction transaction) throws RemoteException;
  boolean getDecision(Transaction transaction) throws RemoteException;
  boolean collectVotes(Transaction transaction) throws RemoteException;
  boolean commit(Transaction transaction) throws RemoteException;
  boolean abort(Transaction transaction) throws RemoteException;
}
