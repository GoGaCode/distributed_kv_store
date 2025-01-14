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
  private static kvStoreOpsImpl kvStoreOps;
  private static ParticipantImpl participant;
  private static CoordinatorImpl coordinator;
  private static final Object lock = new Object();
  private static final Path LOCK_FILE_PATH =
      Paths.get(System.getProperty("java.io.tmpdir"), "rmi_registry.lock");

  private kvStoreOps[] kvStoreOpsList = new kvStoreOps[SERVER_COUNT];
  private Participant[] participants = new Participant[SERVER_COUNT];
  private Coordinator[] coordinators = new Coordinator[SERVER_COUNT];

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

        kvStoreOps = new kvStoreOpsImpl(this.serverIndex);
        participant = new ParticipantImpl(this.serverIndex);
        coordinator = new CoordinatorImpl(this.serverIndex);
        kvStoreOps.setCoordinator(coordinator);

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
        LoggerUtils.logServer("Successfully bind kvStore=" + this.kvStoreOpsName + " to the registry", this.serverIndex);
        // Add the CoordinatorParticipant to the registry
        registry.rebind(this.participantName, participant);
        LoggerUtils.logServer(
            "Successfully bind coordinatorParticipant="
                + this.participantName
                + " to the registry", this.serverIndex);

        // Add the Coordinator to the registry
        registry.rebind(this.coordinatorName, coordinator);
        LoggerUtils.logServer(
            "Successfully bind coordinator=" + this.coordinatorName + " to the registry", this.serverIndex);

      } catch (IOException e) {
        LoggerUtils.logServer("Failed to acquire file lock for registry coordination", this.serverIndex);
        e.printStackTrace();
      }
    }
    try {
      Thread.sleep(1000);
      checkAndSavePeers();
      participant.setCoordinators(coordinators);
      coordinator.setParticipants(participants);

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
                      kvStoreOpsList[i] = (kvStoreOps) this.registry.lookup(KV_STORE_OPS_PREFIX + i);
                      participants[i] = (Participant) this.registry.lookup(PARTICIPANT_PREFIX + i);
                      coordinators[i] = (Coordinator) this.registry.lookup(COORDINATOR_PREFIX + i);
                      LoggerUtils.logServer(
                          this.kvStoreOpsName + "found peer " + KV_STORE_OPS_PREFIX + i, this.serverIndex);
                      LoggerUtils.logServer(
                          this.participantName + "found peer " + PARTICIPANT_PREFIX + i, this.serverIndex);
                      LoggerUtils.logServer(
                          this.coordinatorName + "found peer " + COORDINATOR_PREFIX + i, this.serverIndex);
                    } catch (Exception e) {
                      LoggerUtils.logServer(e.getMessage(), this.serverIndex);
                      String[] bindings = new String[0];
                      try {
                        bindings = registry.list();
                      } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                      }
                      LoggerUtils.logServer("Currently bound objects in the registry:", this.serverIndex);
                      for (String binding : bindings) {
                        LoggerUtils.logServer(binding, this.serverIndex);
                      }
                      allServersStarted = false;
                      // Wait for a short period before trying again
                      try {
                        Thread.sleep(1000);
                        LoggerUtils.logServer(
                            "Pause searching on " + this.kvStoreOpsName + " for 1 second.", this.serverIndex);
                      } catch (InterruptedException ie) {
                        LoggerUtils.logServer("Failed to pause searching on " + this.kvStoreOpsName, this.serverIndex);
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
