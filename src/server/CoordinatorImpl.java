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
    LoggerUtils.logServer("Starting two-phase commit for " + transaction.getOpsType() + " " + transaction.getKey());
    boolean allYes = true;
    for (Participant participant : participants) {
      if (!participant.canCommit(transaction)) {
        allYes = false;
        break;
      }
    }

    if (allYes) {
      LoggerUtils.logServer("All participants agreed to commit");
      for (Participant participant : participants) {
        participant.doCommit(transaction);
      }
      return true;
    } else {
      for (Participant participant : participants) {
        participant.doAbort(transaction);
        LoggerUtils.logServer("Participant " + participant + " aborted");
      }
      return false;
    }
  }

  @Override
  public String startLocalCommit(Transaction transaction) throws RemoteException {
    Participant localParticipant = participants[serverIndex];
    LoggerUtils.logServer("Starting local commit for " + transaction.getOpsType() + " " + transaction.getKey());
    localParticipant.doCommit(transaction);
    return localParticipant.getResult();
  }

  @Override
  public boolean getDecision(Transaction transaction) throws RemoteException {
    return false;
  }

  @Override
  public boolean collectVotes(Transaction transaction) throws RemoteException {
    return false;
  }

  @Override
  public boolean commit(Transaction transaction) throws RemoteException {
    return false;
  }

  @Override
  public boolean abort(Transaction transaction) throws RemoteException {
    return false;
  }
}
