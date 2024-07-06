package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CoordinatorParticipant extends Remote {

  /**
   * Call from coordinator to participant to ask whether it can
   *  commit a transaction. Participant replies with its vote.
   * @param transactionId the transaction id
   * @return true if the participant can commit the transaction, false otherwise
   * @throws RemoteException
   */
  boolean canCommit(String transactionId) throws RemoteException;

  /**
   * Call from coordinator to participant to tell participant to commit its part of a transaction.
   * @param transactionId
   * @throws RemoteException
   */
  void doCommit(String transactionId) throws RemoteException;

  /**
   * Call from coordinator to participant to tell participant to abort its part of a transaction.
   * @param transactionId
   * @throws RemoteException
   */
  void doAbort(String transactionId) throws RemoteException;

  /**
   * Call from participant to coordinator to confirm that it has committed the transaction.
   * @param transactionId
   * @param participantId
   * @throws RemoteException
   */
  void haveCommitted(String transactionId, String participantId) throws RemoteException;

  /**
   * Call from participant to coordinator to ask for the decision on a transaction
   *   when it has voted Yes but has still had no reply after some delay. Used to
   *   recover from server crash or delayed messages.
   * @param transactionId
   * @return
   * @throws RemoteException
   */
  boolean getDecision(String transactionId) throws RemoteException;
}
