package server;

import static server.Constant.RPC_PORT_NUM;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import utils.LoggerUtils;

public class RPCHandler extends HandlerAbstract {

  private static Registry registry;
  private static KeyValueStoreImpl kvStore;
  private static boolean initialized = false;
  private static final Object lock = new Object();

  private String kvStoreName;

  public RPCHandler(int kvStoreIndex, String serverType) throws RemoteException {
    super();
    this.kvStoreName = "keyValueStore" + Integer.toString(kvStoreIndex);
    initialize(serverType);
  }

  private void initialize(String serverType) throws RemoteException {
    synchronized (lock) {
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
    }
  }

  @Override
  protected void run_subroutine() {}

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
