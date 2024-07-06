package server;

import static server.Constant.RPC_PORT_NUM;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import utils.LoggerUtils;

public class RPCHandler extends HandlerAbstract {

  private static Registry registry;
  private static KeyValueStoreImpl kvStore;
  private static final Object lock = new Object();
  private static final Path LOCK_FILE_PATH = Paths.get(System.getProperty("java.io.tmpdir"), "rmi_registry.lock");

  private String kvStoreName;

  public RPCHandler(int kvStoreIndex, String serverType) {
    super();
    this.kvStoreName = "keyValueStore" + Integer.toString(kvStoreIndex);
    initialize(serverType);
  }

  private void initialize(String serverType) {
    synchronized (lock) {
      try (RandomAccessFile raf = new RandomAccessFile(LOCK_FILE_PATH.toFile(), "rw");
          FileChannel channel = raf.getChannel();
          FileLock fileLock = channel.lock()) {

        kvStore = KeyValueStoreImpl.getInstance();
        LoggerUtils.logServer("ServerType=" + serverType);

        if (serverType.equals("primary")) {
          registry = LocateRegistry.createRegistry(RPC_PORT_NUM);
          LoggerUtils.logServer("Registry created on port " + RPC_PORT_NUM);
        } else {
          registry = LocateRegistry.getRegistry();
          LoggerUtils.logServer("Registry found on port " + RPC_PORT_NUM);
        }

        registry.rebind(this.kvStoreName, kvStore);
        LoggerUtils.logServer("Successfully bind kvStore=" + this.kvStoreName + " to the registry");

      } catch (IOException e) {
        LoggerUtils.logServer("Failed to acquire file lock for registry coordination");
        e.printStackTrace();
      }
    }
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
