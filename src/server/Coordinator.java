package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Coordinator extends Remote {
  boolean startTwoPhaseCommit(Transaction transaction) throws RemoteException;
  String startLocalCommit(Transaction transaction) throws RemoteException;
  boolean getDecision(Transaction transaction) throws RemoteException;
  boolean collectVotes(Transaction transaction) throws RemoteException;
  boolean commit(Transaction transaction) throws RemoteException;
  boolean abort(Transaction transaction) throws RemoteException;
}
