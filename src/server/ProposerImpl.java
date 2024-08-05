package server;

import static utils.Constant.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import utils.IDGenerator;

public class ProposerImpl extends UnicastRemoteObject implements Proposer {

    private IDGenerator idGenerator;
    private int serverIndex;
    private Acceptor[] acceptors = new Acceptor[SERVER_COUNT];
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
            String proposerName = PROPOSER_PREFIX + serverIndex;
            registry.rebind(proposerName, this);
            System.out.println(proposerName + " bound to registry.");

            boolean acceptorAllFound = false;
            while (!acceptorAllFound){
                // Populate acceptors
                try{
                for (int i = 0; i < SERVER_COUNT; i++) {
                    String acceptorName =  ACCEPTOR_PREFIX + i;
                    acceptors[i] = (Acceptor) registry.lookup(acceptorName);
                }
                acceptorAllFound = true;
                } catch (Exception e) {
                    System.out.println("Waiting for all acceptors to be bound to registry.");
                    Thread.sleep(1000);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
