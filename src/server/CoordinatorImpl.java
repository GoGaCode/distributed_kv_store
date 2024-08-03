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
  public boolean startTwoPhaseCommit(Proposal proposal) throws RemoteException {
    LoggerUtils.logServer(
        "Starting two-phase commit for " + proposal.getOpsType() + " " + proposal.getKey(),
        this.serverIndex);

    boolean allYes = collectVotes(proposal);

    if (allYes) {
      boolean success = commit(proposal);
      if (success) {
        LoggerUtils.logServer("Proposal committed successfully", this.serverIndex);
        return true;
      } else {
        LoggerUtils.logServer("Proposal failed to commit", this.serverIndex);
        abort(proposal);
        return false;
      }
    }
    return false;
  }

  @Override
  public String startLocalCommit(Proposal proposal) throws RemoteException {
    Participant localParticipant = participants[serverIndex];
    LoggerUtils.logServer(
        "Starting local commit for " + proposal.getOpsType() + " " + proposal.getKey(),
        this.serverIndex);
    localParticipant.doCommit(proposal);
    return localParticipant.getResult();
  }

  @Override
  public boolean getDecision(Proposal proposal) throws RemoteException {
    return false;
  }

  @Override
  public boolean collectVotes(Proposal proposal) throws RemoteException {
    for (Participant participant : participants) {
      if (!participant.canCommit(proposal)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean commit(Proposal proposal) throws RemoteException {

    // All participants agreed to commit
    boolean allYes = true;
    LoggerUtils.logServer("All participants agreed to commit", this.serverIndex);
    for (Participant participant : participants) {
      if (!participant.doCommit(proposal)) {
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
  public boolean abort(Proposal proposal) throws RemoteException {
    for (Participant participant : participants) {
      participant.doAbort(proposal);
      LoggerUtils.logServer("Participant " + participant + " aborted", this.serverIndex);
    }
    return false;
  }
}
