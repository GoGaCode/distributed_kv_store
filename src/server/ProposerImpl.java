package server;

import utils.IDGenerator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static utils.Constant.SERVER_COUNT;

public class ProposerImpl extends UnicastRemoteObject implements Proposer{

    private IDGenerator idGenerator;
    private Acceptor[] acceptors = new Acceptor[SERVER_COUNT];
    private int serverIndex;
    private Proposal accepted_proposals[] = new Proposal[SERVER_COUNT];

    public ProposerImpl(Integer serverIndex) throws RemoteException {
        this.idGenerator = new IDGenerator(serverIndex);
        this.serverIndex = serverIndex;
    }

    private IDGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    public boolean prepare(Proposal proposal) throws RemoteException {
        int promiseCount = 0;
        for (Acceptor acceptor : acceptors) {
            Proposal accepted_proposal = acceptor.Promise(proposal);
            if (accepted_proposal != null){
                promiseCount++;
                accepted_proposals[acceptor.getServerIndex()] = accepted_proposal;
                // If other proposal being accepted, update the proposal
            };
        }
        if  (promiseCount < Math.ceil(SERVER_COUNT/2)){
            return false;
        }
        return true;
    }

    @Override
    public boolean propose(Proposal proposal) throws RemoteException {
        // Send ID generated through idGenerator to all other instances
        // TODO: check if majority accepted
        for (Proposal accepted_proposal : accepted_proposals) {
            if (accepted_proposal != proposal){
                proposal = accepted_proposal;
                break;
            }
        }
        int accepted_count = 0;
        for (Acceptor acceptor : acceptors) {
            if (acceptor.Accept(proposal)){
                accepted_count++;
            };
        }
        return true;
    }

    public void setAcceptors(Acceptor[] acceptors) {
        this.acceptors = acceptors;
    }
}
