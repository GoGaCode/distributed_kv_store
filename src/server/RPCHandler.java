package server;

import static utils.Constant.KEY_VAL_STORE_PREFIX;
import static utils.Constant.RPC_PORT_NUM;

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
  private static KeyValueStoreImpl kvStore;
  private final String kvStoreName;
  private final int kvStoreIndex;

  private static CoordinatorParticipantImpl coordinatorParticipant;

  private final String coordinatorParticipantName;
  private static final Object lock = new Object();
  private static final Path LOCK_FILE_PATH =
      Paths.get(System.getProperty("java.io.tmpdir"), "rmi_registry.lock");

  private static final int TOTAL_SERVERS = 5;
  private KeyValueStore[] kvStores = new KeyValueStore[TOTAL_SERVERS];

  public RPCHandler(int kvStoreIndex, String serverType) {
    super();
    LoggerUtils.logServer("-------Starting Server Begin----------");
    this.kvStoreIndex = kvStoreIndex;
    this.kvStoreName = KEY_VAL_STORE_PREFIX + Integer.toString(kvStoreIndex);
    this.coordinatorParticipantName = "coordinatorParticipant" + Integer.toString(kvStoreIndex);
    initialize(serverType);
    LoggerUtils.logServer("-------Starting Server Successful----------");
  }

  private void initialize(String serverType) {
    synchronized (lock) {
      try (RandomAccessFile raf = new RandomAccessFile(LOCK_FILE_PATH.toFile(), "rw");
          FileChannel channel = raf.getChannel();
          FileLock fileLock = channel.lock()) {

        kvStore = KeyValueStoreImpl.getInstance();
        coordinatorParticipant = new CoordinatorParticipantImpl();
        LoggerUtils.logServer("ServerType=" + serverType);

        if (serverType.equals("primary")) {
          registry = LocateRegistry.createRegistry(RPC_PORT_NUM);
          LoggerUtils.logServer("Registry created on port " + RPC_PORT_NUM);
        } else {
          registry = LocateRegistry.getRegistry();
          LoggerUtils.logServer("Registry found on port " + RPC_PORT_NUM);
        }

        // Add the KVStore to the registry
        registry.rebind(this.kvStoreName, kvStore);
        LoggerUtils.logServer("Successfully bind kvStore=" + this.kvStoreName + " to the registry");
        // Add the CoordinatorParticipant to the registry
        registry.rebind(this.coordinatorParticipantName, coordinatorParticipant);
        LoggerUtils.logServer(
            "Successfully bind coordinatorParticipant="
                + this.coordinatorParticipantName
                + " to the registry");

      } catch (IOException e) {
        LoggerUtils.logServer("Failed to acquire file lock for registry coordination");
        e.printStackTrace();
      }
    }
    try {
      Thread.sleep(1000);
      checkAndSaveKVStores();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }
  }

  // Search for all other kvStores when they are initialized and save it to an array
  private void checkAndSaveKVStores() {
    LoggerUtils.logServer("Searching for other KeyValueStores");
    new Thread(() -> {
      boolean allServersStarted = false;
      while (!allServersStarted) {
        allServersStarted = true;
        for (int i = 0; i < TOTAL_SERVERS; i++) {
          // Save the kvStore object to the array except for the current kvStore
          if (i!=kvStoreIndex && kvStores[i] == null) {
            try {
              kvStores[i] = (KeyValueStore) this.registry.lookup(KEY_VAL_STORE_PREFIX + i);
              LoggerUtils.logServer(this.kvStoreName + "found peer keyValueStore" + i);
            } catch (Exception e) {
                LoggerUtils.logServer("Failed to find keyValueStore" + i + " w error " + e.getMessage());
              String[] bindings = new String[0];
              try {
                bindings = registry.list();
              } catch (RemoteException ex) {
                throw new RuntimeException(ex);
              }
              LoggerUtils.logServer("Currently bound objects in the registry:");
                for (String binding : bindings) {
                  LoggerUtils.logServer(binding);
                }
              allServersStarted = false;
              // Wait for a short period before trying again
              try {
                Thread.sleep(1000);
                LoggerUtils.logServer("Pause searching on "+ this.kvStoreName + " for 1 second.");
              } catch (InterruptedException ie) {
                LoggerUtils.logServer("Failed to pause searching on "+ this.kvStoreName);
                ie.printStackTrace();
              }
            }
          }
        }
      }
      LoggerUtils.logServer("All KV Stores are connected.");
    }).start();
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
