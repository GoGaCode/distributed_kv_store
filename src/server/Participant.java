package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Participant extends Remote {

  boolean canCommit(Transaction transaction) throws RemoteException;

  boolean doCommit(Transaction transaction) throws RemoteException;

  boolean doAbort(Transaction transaction) throws RemoteException;

  boolean haveCommitted(Transaction transaction) throws RemoteException;

  String getResult() throws RemoteException;


}
