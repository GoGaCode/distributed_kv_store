package server;

import utils.LoggerUtils;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import static utils.Constant.INIT_FLAG_KEY;
import static utils.opsType.*;
import static utils.opsType.SET_INIT_FLAG;

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
}
