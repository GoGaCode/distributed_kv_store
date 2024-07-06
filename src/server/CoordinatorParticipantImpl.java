package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class CoordinatorParticipantImpl extends UnicastRemoteObject implements CoordinatorParticipant {

    private Map<String, Boolean> transactionStatus = new HashMap<>();
    private Map<String, CoordinatorParticipantImpl> participants = new HashMap<>();

    protected CoordinatorParticipantImpl() throws RemoteException {
        super();
    }

    // Method to add participants (other replicas)
    public void addParticipant(String id, CoordinatorParticipantImpl participant) {
        participants.put(id, participant);
    }

    // Two-phase commit methods
    @Override
    public boolean canCommit(String transactionId) throws RemoteException {
        return true; // Placeholder logic
    }

    /**
     * If all participants agree to commit, then the coordinator tells all participants to commit.
     * @param transactionId
     * @throws RemoteException
     */
    @Override
    public void doCommit(String transactionId) throws RemoteException {
    }

    /**
     * If any participant disagrees to commit, then the coordinator tells all participants to abort.
     * @param transactionId
     * @throws RemoteException
     */
    @Override
    public void doAbort(String transactionId) throws RemoteException {
        // Abort logic
    }

    /**
     * Confirm that the participant has committed the transaction.
     * @param transactionId
     * @param participantId
     * @throws RemoteException
     */
    @Override
    public void haveCommitted(String transactionId, String participantId) throws RemoteException {
        // Logic to confirm commit
    }

    /**
     * Ask for the decision on a transaction when it has voted Yes but has still had no reply after some delay.
     * @param transactionId
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean getDecision(String transactionId) throws RemoteException {
        return transactionStatus.getOrDefault(transactionId, false);
    }

    public static void main(String[] args) {
        try {
            CoordinatorParticipantImpl server = new CoordinatorParticipantImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("CoordinatorParticipant", server);
            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
