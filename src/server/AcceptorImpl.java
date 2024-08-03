package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AcceptorImpl extends UnicastRemoteObject implements Acceptor {

    private long max_transId = 0;
    private Proposal accepted_proposal;
    private Learner learner;
    private int serverIndex;

    @Override
    public Proposal Promise(Proposal proposal) throws RemoteException {
        // TODO: ensure ID always increasing
        if (proposal.getTransId() > max_transId) {
            max_transId = proposal.getTransId();
            if (accepted_proposal != null) {
                return accepted_proposal;
            }
            return proposal;
        }
        return null;
    }

    @Override
    public boolean Accept(Proposal proposal) throws RemoteException {
        if (proposal.getTransId() == max_transId) {
            accepted_proposal = proposal;
            // TODO: all learns learn proposal
            if (learner.learn(proposal) != null){
                accepted_proposal = null;
            };
            return true;
        }
        return false;
    }

    @Override
    public int getServerIndex() {
        return serverIndex;
    }


    public void setLearner(Learner learner) {
        this.learner = learner;
    }

    public AcceptorImpl(int serverIndex) throws RemoteException {
        this.serverIndex = serverIndex;
    }

}
