package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Learner extends Remote, Server{
    /**
     * reply with an ACCEPTED message & send ACCEPTED(ID, VALUE) to all learners
     *
     * @param proposal
     * @throws RemoteException
     */
    String learn(Proposal proposal) throws RemoteException;

}
