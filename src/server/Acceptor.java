package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Acceptor extends Remote, Server {
  /**
   * if (ID <= max_id)
   * do not respond (or respond with a "fail" message)
   * else
   * max_id = ID     // save highest ID we've seen so far
   * if (proposal_accepted == true) // was a proposal already accepted?
   * respond: PROMISE(ID, accepted_ID, accepted_VALUE)
   * else
   * respond: PROMISE(ID)
   *
   * @param proposal
   * @throws RemoteException
   */
  Proposal Promise(Proposal proposal) throws RemoteException;

  /**
   * Each acceptor receives a PROPOSE(ID, VALUE) message from a proposer. If the ID is the highest number it has processed then accept the proposal and propagate the value to the proposer and to all the learners.
   * <p>
   * if (ID == max_id) // is the ID the largest I have seen so far?
   * proposal_accepted = true     // note that we accepted a proposal
   * accepted_ID = ID             // save the accepted proposal number
   * accepted_VALUE = VALUE       // save the accepted proposal data
   * respond: ACCEPTED(ID, VALUE) to the proposer and all learners
   * else
   * do not respond (or respond with a "fail" message)
   * If a majority of acceptors accept ID, value then consensus is reached. Consensus is on the value, not necessarily the ID.
   *
   * @param proposal
   * @throws RemoteException
   */
  boolean Accept(Proposal proposal) throws RemoteException;

  int getServerIndex() throws RemoteException;

}
