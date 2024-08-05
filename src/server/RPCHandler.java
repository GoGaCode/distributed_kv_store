package server;

import static utils.Constant.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;

/*
 * RPCHandler class is responsible for
 *  -creating the registry.
 *  -binding the KeyValueStoreImpl object to it.
 */
public class RPCHandler extends HandlerAbstract {

  private static final Object lock = new Object();
  private static final Path LOCK_FILE_PATH =
      Paths.get(System.getProperty("java.io.tmpdir"), "rmi_registry.lock");

  public RPCHandler(int serverIndex) {
    super(serverIndex);
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: RPCHandler <port num>");
    }
    int kvStoreIndex = Integer.parseInt(args[0]);
    RPCHandler rpcHandler = new RPCHandler(kvStoreIndex);
    rpcHandler.run();
    try {
      kvStoreOps kvStoreOpsThread = new kvStoreOpsPaxos(kvStoreIndex);
      Acceptor acceptorThread = new AcceptorImpl(kvStoreIndex);
      Proposer proposerThread = new ProposerImpl(kvStoreIndex);
      Learner learnerThread = new LearnerImpl(kvStoreIndex);
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            kvStoreOpsThread.run(); // Assuming kvStoreOpsThread has a run() method
          } catch (RemoteException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            acceptorThread.run(); // Assuming acceptorThread has a run() method
          } catch (RemoteException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            proposerThread.run(); // Assuming proposerThread has a run() method
          } catch (RemoteException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            learnerThread.run(); // Assuming learnerThread has a run() method
          } catch (RemoteException e) {
            throw new RuntimeException(e);
          }
        }
      }).start();
//      learnerThread.run();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {}
}
