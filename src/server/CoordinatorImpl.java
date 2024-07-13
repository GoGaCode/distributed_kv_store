package server;

import static utils.Constant.SERVER_COUNT;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import utils.LoggerUtils;

public class CoordinatorImpl extends UnicastRemoteObject implements Coordinator {

  private Participant[] participants = new Participant[SERVER_COUNT];
  private int serverIndex;

  public CoordinatorImpl(int kvStoreIndex) throws RemoteException {
    super();
    this.serverIndex = kvStoreIndex;
  }

  public void setParticipants(Participant[] participants) {
    this.participants = participants;
  }

  @Override
  public boolean startTwoPhaseCommit(Transaction transaction) throws RemoteException {
    LoggerUtils.logServer(
        "Starting two-phase commit for " + transaction.getOpsType() + " " + transaction.getKey(),
        this.serverIndex);

    boolean allYes = collectVotes(transaction);

    if (allYes) {
      boolean success = commit(transaction);
      if (success) {
        LoggerUtils.logServer("Transaction committed successfully", this.serverIndex);
        return true;
      } else {
        LoggerUtils.logServer("Transaction failed to commit", this.serverIndex);
        abort(transaction);
        return false;
      }
    }
    return false;
  }

  @Override
  public String startLocalCommit(Transaction transaction) throws RemoteException {
    Participant localParticipant = participants[serverIndex];
    LoggerUtils.logServer(
        "Starting local commit for " + transaction.getOpsType() + " " + transaction.getKey(),
        this.serverIndex);
    localParticipant.doCommit(transaction);
    return localParticipant.getResult();
  }

  @Override
  public boolean getDecision(Transaction transaction) throws RemoteException {
    return false;
  }

  @Override
  public boolean collectVotes(Transaction transaction) throws RemoteException {
    for (Participant participant : participants) {
      if (!participant.canCommit(transaction)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean commit(Transaction transaction) throws RemoteException {

    // All participants agreed to commit
    boolean allYes = true;
    LoggerUtils.logServer("All participants agreed to commit", this.serverIndex);
    for (Participant participant : participants) {
      if (!participant.doCommit(transaction)) {
        LoggerUtils.logServer("Participant " + participant + " failed to commit", this.serverIndex);
        allYes = false;
        break;
      }
    }
    if (allYes) {
      // All participants committed
      return true;
    }
    return false;
  }

  @Override
  public boolean abort(Transaction transaction) throws RemoteException {
    for (Participant participant : participants) {
      participant.doAbort(transaction);
      LoggerUtils.logServer("Participant " + participant + " aborted", this.serverIndex);
    }
    return false;
  }
}
