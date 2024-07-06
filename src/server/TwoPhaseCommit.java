package server;

import java.rmi.RemoteException;
import java.util.Map;

public class TwoPhaseCommit {

    private final CoordinatorParticipant coordinator;
    private final Map<String, CoordinatorParticipant> participants;
    private final KeyValueStoreImpl kvStore;

    public TwoPhaseCommit(CoordinatorParticipant coordinator, Map<String, CoordinatorParticipant> participants, KeyValueStoreImpl kvStore) {
        this.coordinator = coordinator;
        this.participants = participants;
        this.kvStore = kvStore;
    }

    public boolean put(String key, String value) throws RemoteException {
        String transactionId = "put-" + key;
        return executeTwoPhaseCommit(transactionId, () -> kvStore.put(key, value));
    }

    public boolean delete(String key) throws RemoteException {
        String transactionId = "delete-" + key;
        return executeTwoPhaseCommit(transactionId, () -> kvStore.delete(key));
    }

    private boolean executeTwoPhaseCommit(String transactionId, CommitAction action) throws RemoteException {
        if (canCommit(transactionId)) {
            action.execute();
            doCommit(transactionId);
            return true;
        } else {
            doAbort(transactionId);
            return false;
        }
    }

    private boolean canCommit(String transactionId) throws RemoteException {
        if (!coordinator.canCommit(transactionId)) {
            return false;
        }
        for (CoordinatorParticipant participant : participants.values()) {
            if (!participant.canCommit(transactionId)) {
                return false;
            }
        }
        return true;
    }

    private void doCommit(String transactionId) throws RemoteException {
        coordinator.doCommit(transactionId);
        for (CoordinatorParticipant participant : participants.values()) {
            participant.doCommit(transactionId);
        }
    }

    private void doAbort(String transactionId) throws RemoteException {
        coordinator.doAbort(transactionId);
        for (CoordinatorParticipant participant : participants.values()) {
            participant.doAbort(transactionId);
        }
    }

    @FunctionalInterface
    interface CommitAction {
        void execute() throws RemoteException;
    }
}
