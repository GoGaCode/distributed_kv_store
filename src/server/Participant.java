package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Participant and coordinator are both part of two phase commit protocol.
 * Participant interface is responsible for
 *  -checking if the participant can commit the transaction.
 *  -committing the transaction.
 *  -aborting the transaction.
 *  -checking if the participant have committed the transaction.
 *  -getting the result of the transaction.
 */
public interface Participant extends Remote {

  boolean canCommit(Transaction transaction) throws RemoteException;

  boolean doCommit(Transaction transaction) throws RemoteException;

  boolean doAbort(Transaction transaction) throws RemoteException;

  boolean haveCommitted(Transaction transaction) throws RemoteException;

  String getResult() throws RemoteException;


}
