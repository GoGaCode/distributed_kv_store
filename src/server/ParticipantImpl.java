package server;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import static utils.Constant.INIT_FLAG_KEY;
import static utils.opsType.*;

public class ParticipantImpl extends UnicastRemoteObject implements Participant {


    private Coordinator[] coordinators;
    private Map<String, String> kvStore;
    private int serverIndex;
    private String result;

    protected ParticipantImpl(int serverIndex) throws RemoteException {
        super();
        this.serverIndex = serverIndex;
        this.kvStore = new HashMap<>();
    }

    public void setCoordinators(Coordinator[] coordinators) {
        this.coordinators = coordinators;
    }

    @Override
    public boolean canCommit(Transaction transaction) throws RemoteException {
        return true;
    }

    @Override
    public boolean doCommit(Transaction transaction) throws RemoteException {
        if (transaction.getOpsType().equals(GET)) {
            result = kvStore.get(transaction.getKey());
        } else if (transaction.getOpsType().equals(PUT)) {
            kvStore.put(transaction.getKey(), transaction.getValue());
        } else if (transaction.getOpsType().equals(DELETE)) {
            kvStore.remove(transaction.getKey());
        } else if (transaction.getOpsType().equals(SET_INIT_FLAG)) {
            kvStore.put(INIT_FLAG_KEY, "true");
        }
        return false;
    }

    @Override
    public boolean doAbort(Transaction transaction) throws RemoteException {
        return true;
    }

    @Override
    public boolean haveCommitted(Transaction transaction) throws RemoteException {
        return false;
    }


    public String getResult() {
        return result;
    }
}
