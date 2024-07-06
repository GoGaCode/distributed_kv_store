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

  public RPCHandler(int kvStoreIndex) throws RemoteException {
    super();
    this.kvStoreName = "keyValueStore" + Integer.toString(kvStoreIndex);
    initialize();
  }

  private void initialize() throws RemoteException {
    synchronized (lock) {
      if (!initialized) {
        try {
          kvStore = KeyValueStoreImpl.getInstance();
          registry = LocateRegistry.createRegistry(RPC_PORT_NUM);
          registry.rebind(this.kvStoreName, kvStore);

          LoggerUtils.logServer("RPC Server" + this.kvStoreName + " started on port: " + RPC_PORT_NUM);
          initialized = true;
        } catch (RemoteException e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
  }

  @Override
  protected void run_subroutine() {

  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: RPCHandler <port num>");
    }
    int kvStoreIndex = Integer.parseInt(args[0]);
    RPCHandler rpcHandler = new RPCHandler(kvStoreIndex);
    rpcHandler.run();
  }

}

