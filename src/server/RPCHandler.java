package server;

import static utils.Constant.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import utils.IDGeneratorImpl;

/*
 * RPCHandler class is responsible for
 *  -creating the registry.
 *  -binding the KeyValueStoreImpl object to it.
 */
public class RPCHandler extends HandlerAbstract {

  private IDGenerator idGenerator;
  Registry registry;

  public RPCHandler(int serverIndex) throws RemoteException {
    super(serverIndex);
    this.idGenerator = new IDGeneratorImpl();
    registry = LocateRegistry.getRegistry(1099);
    registry.rebind(ID_GENERATOR_NAME, idGenerator);
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: RPCHandler <port num>");
    }
    int kvStoreIndex = Integer.parseInt(args[0]);
    RPCHandler rpcHandler = new RPCHandler(kvStoreIndex);
    rpcHandler.run();
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  kvStoreOps kvStoreOpsThread = new kvStoreOpsPaxos(kvStoreIndex);
                  kvStoreOpsThread.run(); // Assuming kvStoreOpsThread has a run() method
                } catch (RemoteException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .start();

    //      // Schedule the shutdown after 2 minutes
    //      Timer timer = new Timer();
    //      timer.schedule(new TimerTask() {
    //        @Override
    //        public void run() {
    //          ((kvStoreOpsPaxos) kvStoreOpsThread).shutdown();
    //        }
    //      }, 1 * 30 * 1000); // 30 sec

    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  Proposer proposerThread = new ProposerImpl(kvStoreIndex);
                  proposerThread.run(); // Assuming proposerThread has a run() method
                } catch (RemoteException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .start();

    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  Learner learnerThread = new LearnerImpl(kvStoreIndex);
                  learnerThread.run(); // Assuming learnerThread has a run() method
                } catch (RemoteException e) {
                  throw new RuntimeException(e);
                }
              }
            })
        .start();

    while (true) {
      new Thread(
              new Runnable() {
                @Override
                public void run() {
                  try {
                    Acceptor acceptorThread = new AcceptorImpl(kvStoreIndex);
                    acceptorThread.run(); // Assuming acceptorThread has a run() method
                  } catch (RemoteException e) {
                    throw new RuntimeException(e);
                  }
                }
              })
          .start();
      Thread.sleep(getRandomNumberBetween(3000, 10000));
    }
  }

  public static int getRandomNumberBetween(int x, int y) {
    if (x > y) {
      throw new IllegalArgumentException("x should be less than or equal to y");
    }
    Random random = new Random();
    return random.nextInt(y - x + 1) + x;
  }

  @Override
  public void run() {}
}
