//package server;
//
//
//import static utils.Constant.INIT_FLAG_KEY;
//import static utils.opsType.*;
//
//import java.rmi.RemoteException;
//import java.rmi.server.UnicastRemoteObject;
//import java.util.HashMap;
//import java.util.Map;
//import utils.LoggerUtils;
//
//public class AcceptorImpl extends UnicastRemoteObject implements Participant {
//
//
//    private Coordinator[] coordinators;
//    private Map<String, String> kvStore;
//    private int serverIndex;
//    private String result;
//
//    protected ParticipantImpl(int serverIndex) throws RemoteException {
//        super();
//        this.serverIndex = serverIndex;
//        this.kvStore = new HashMap<>();
//    }
//
//    public void setCoordinators(Coordinator[] coordinators) {
//        this.coordinators = coordinators;
//    }
//
//    @Override
//    public boolean canCommit(Proposal proposal) throws RemoteException {
//        return true;
//    }
//
//    @Override
//    public boolean doCommit(Proposal proposal) throws RemoteException {
//        try {
//        if (proposal.getOpsType().equals(GET)) {
//            result = kvStore.get(proposal.getKey());
//            LoggerUtils.logServer("GET " + proposal.getKey() + " -> " + result + " successfully", this.serverIndex);
//        } else if (proposal.getOpsType().equals(PUT)) {
//            kvStore.put(proposal.getKey(), proposal.getValue());
//            LoggerUtils.logServer("PUT " + proposal.getKey() + " -> " + proposal.getValue() + " successfully", this.serverIndex);
//        } else if (proposal.getOpsType().equals(DELETE)) {
//            kvStore.remove(proposal.getKey());
//            LoggerUtils.logServer("DELETE " + proposal.getKey() + " successfully", this.serverIndex);
//        } else if (proposal.getOpsType().equals(SET_INIT_FLAG)) {
//            kvStore.put(INIT_FLAG_KEY, "true");
//        }
//        return true;
//        } catch (Exception e) {
//            String message = "Error in doCommit:" + e.getMessage();
//            LoggerUtils.logServer(message, this.serverIndex);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean doAbort(Proposal proposal) throws RemoteException {
//        if (proposal.getOpsType().equals(PUT))  {
//            kvStore.remove(proposal.getKey());
//            LoggerUtils.logServer("Aborted PUT " + proposal.getKey() + " -> " + proposal.getValue(), this.serverIndex);
//        } else if (proposal.getOpsType().equals(DELETE)) {
//            kvStore.put(proposal.getKey(), proposal.getValue());
//            LoggerUtils.logServer("Aborted DELETE " + proposal.getKey(), this.serverIndex);
//        } else if (proposal.getOpsType().equals(SET_INIT_FLAG)) {
//            kvStore.remove(INIT_FLAG_KEY);
//        }
//        return true;
//    }
//
//    @Override
//    public boolean haveCommitted(Proposal proposal) throws RemoteException {
//        return false;
//    }
//
//
//    public String getResult() {
//        return result;
//    }
//}
