package server;

import static utils.Constant.*;
import static utils.opsType.*;
import static utils.opsType.SET_INIT_FLAG;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import utils.LoggerUtils;

public class LearnerImpl extends UnicastRemoteObject implements Learner{
    private Map<String, String> kvStore;
    private int serverIndex;

    protected LearnerImpl(int serverIndex) throws RemoteException {
        super();
        this.serverIndex = serverIndex;
        this.kvStore = new HashMap<>();
    }

    @Override
    public String learn(Proposal proposal) throws RemoteException {
        String result = null;
        try {
            if (proposal.getOpsType().equals(GET)) {
                result = kvStore.get(proposal.getKey());
                LoggerUtils.logServer("GET " + proposal.getKey() + " -> " + result + " successfully", this.serverIndex);
            } else if (proposal.getOpsType().equals(PUT)) {
                kvStore.put(proposal.getKey(), proposal.getValue());
                LoggerUtils.logServer("PUT " + proposal.getKey() + " -> " + proposal.getValue() + " successfully", this.serverIndex);
                result = "success";
            } else if (proposal.getOpsType().equals(DELETE)) {
                kvStore.remove(proposal.getKey());
                LoggerUtils.logServer("DELETE " + proposal.getKey() + " successfully", this.serverIndex);
                result = "success";
            } else if (proposal.getOpsType().equals(SET_INIT_FLAG)) {
                kvStore.put(INIT_FLAG_KEY, "true");
                result = "success";
            }
            return result;
        } catch (Exception e) {
            String message = "Error in doCommit:" + e.getMessage();
            LoggerUtils.logServer(message, this.serverIndex);
            return null;
        }
    }

    @Override
    public void run() {
        try {
            // Get or create the registry on port 1099
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(1099);
                registry.list(); // Check if registry already exists
            } catch (Exception e) {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Created new RMI registry on port 1099.");
            }

            // Bind this acceptor to the registry
            String learnerName = LEARNER_PREFIX + serverIndex;
            registry.rebind(learnerName, this);
            System.out.println(learnerName + " bounded to registry.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
