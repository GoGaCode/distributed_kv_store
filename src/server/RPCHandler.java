package server;

import static utils.Constant.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import utils.LoggerUtils;

/*
 * RPCHandler class is responsible for
 *  -creating the registry.
 *  -binding the KeyValueStoreImpl object to it.
 */
public class RPCHandler extends HandlerAbstract {

  private static Registry registry;
  private static kvStoreOpsPaxos kvStoreOps;
  private static AcceptorImpl acceptor;
  private static ProposerImpl proposer;

  private static LearnerImpl learner;
  private static final Object lock = new Object();
  private static final Path LOCK_FILE_PATH =
      Paths.get(System.getProperty("java.io.tmpdir"), "rmi_registry.lock");

  private kvStoreOps[] kvStoreOpsList = new kvStoreOps[SERVER_COUNT];
  private Acceptor[] acceptors = new Acceptor[SERVER_COUNT];
  private Proposer[] proposers = new Proposer[SERVER_COUNT];
  private Learner[] learners = new Learner[SERVER_COUNT];

  public RPCHandler(int serverIndex, String serverType) {
    super(serverIndex);
    LoggerUtils.logServer("-------Starting Server Begin----------", serverIndex);
    initialize(serverType);
    LoggerUtils.logServer("-------Starting Server Successful----------", this.serverIndex);
  }

  private void initialize(String serverType) {
    synchronized (lock) {
      try (RandomAccessFile raf = new RandomAccessFile(LOCK_FILE_PATH.toFile(), "rw");
          FileChannel channel = raf.getChannel();
          FileLock fileLock = channel.lock()) {

        kvStoreOps = new kvStoreOpsPaxos(this.serverIndex);
        acceptor = new AcceptorImpl(this.serverIndex);
        proposer = new ProposerImpl(this.serverIndex);
        learner = new LearnerImpl(this.serverIndex);
        kvStoreOps.setProposer(proposer);

        LoggerUtils.logServer("ServerType=" + serverType, this.serverIndex);

        if (serverType.equals("primary")) {
          registry = LocateRegistry.createRegistry(RPC_PORT_NUM);
          LoggerUtils.logServer("Registry created on port " + RPC_PORT_NUM, this.serverIndex);
        } else {
          registry = LocateRegistry.getRegistry();
          LoggerUtils.logServer("Registry found on port " + RPC_PORT_NUM, this.serverIndex);
        }

        // Add the KVStore to the registry
        registry.rebind(this.kvStoreOpsName, kvStoreOps);
        LoggerUtils.logServer(
            "Successfully bind kvStore=" + this.kvStoreOpsName + " to the registry",
            this.serverIndex);
        // Add the CoordinatorParticipant to the registry
        registry.rebind(this.acceptorName, acceptor);
        LoggerUtils.logServer(
            "Successfully bind coordinatorParticipant=" + this.acceptorName + " to the registry",
            this.serverIndex);

        // Add the Coordinator to the registry
        registry.rebind(this.proposerName, proposer);
        LoggerUtils.logServer(
            "Successfully bind coordinator=" + this.proposerName + " to the registry",
            this.serverIndex);
        registry.rebind(this.learnerName, learner);

      } catch (IOException e) {
        LoggerUtils.logServer(
            "Failed to acquire file lock for registry coordination", this.serverIndex);
        e.printStackTrace();
      }
    }
    try {
      Thread.sleep(1000);
      checkAndSavePeers();
      proposer.setAcceptors(acceptors);
      acceptor.setLearner(learner);
      kvStoreOps.setProposer(proposer);
      kvStoreOps.setAcceptors(acceptor);
      kvStoreOps.setLearner(learner);
//      acceptor.setCoordinators(coordinators);
//      proposer.setParticipants(participants);

    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
  }

  // Search for all other kvStores when they are initialized and save it to an array
  private void checkAndSavePeers() {
    LoggerUtils.logServer("Searching for other KeyValueStores", this.serverIndex);
    new Thread(
            () -> {
              boolean allServersStarted = false;
              while (!allServersStarted) {
                allServersStarted = true;
                for (int i = 0; i < SERVER_COUNT; i++) {
                  if (kvStoreOpsList[i] == null) {
                    try {
                      kvStoreOpsList[i] =
                          (kvStoreOps) this.registry.lookup(KV_STORE_OPS_PREFIX + i);
                      acceptors[i] = (Acceptor) this.registry.lookup(ACCEPTOR_PREFIX + i);
                      proposers[i] = (Proposer) this.registry.lookup(PROPOSER_PREFIX + i);
                      learners[i] = (Learner) this.registry.lookup(LEARNER_PREFIX + i);
                      LoggerUtils.logServer(
                          this.kvStoreOpsName + "found peer " + KV_STORE_OPS_PREFIX + i,
                          this.serverIndex);
                      LoggerUtils.logServer(
                          this.acceptorName + "found peer " + ACCEPTOR_PREFIX + i,
                          this.serverIndex);
                      LoggerUtils.logServer(
                          this.proposerName + "found peer " + PROPOSER_PREFIX + i,
                          this.serverIndex);
                      LoggerUtils.logServer(
                          this.learnerName + "found peer " + LEARNER_PREFIX + i, this.serverIndex);
                    } catch (Exception e) {
                      LoggerUtils.logServer(e.getMessage(), this.serverIndex);
                      String[] bindings;
                      try {
                        bindings = this.registry.list();
                      } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                      }
                      LoggerUtils.logServer(
                          "Currently bound objects in the registry:", this.serverIndex);
                      for (String binding : bindings) {
                        LoggerUtils.logServer(binding, this.serverIndex);
                      }
                      allServersStarted = false;
                      // Wait for a short period before trying again
                      try {
                        Thread.sleep(1000);
                        LoggerUtils.logServer(
                            "Pause searching on " + this.kvStoreOpsName + " for 1 second.",
                            this.serverIndex);
                      } catch (InterruptedException ie) {
                        LoggerUtils.logServer(
                            "Failed to pause searching on " + this.kvStoreOpsName,
                            this.serverIndex);
                        ie.printStackTrace();
                      }
                    }
                  }
                }
              }
              LoggerUtils.logServer("All KV Stores are connected.", this.serverIndex);
            })
        .start();
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: RPCHandler <port num>");
    }
    String serverType = args[0];
    int kvStoreIndex = Integer.parseInt(args[1]);
    RPCHandler rpcHandler = new RPCHandler(kvStoreIndex, serverType);
    rpcHandler.run();
  }
}
