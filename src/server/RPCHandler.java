package server;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RPCHandler extends HandlerAbstract{

    private KeyValueStore kvStore;
    private Registry registry;
    public RPCHandler(int portNum) throws RemoteException {
        super();
        try {
            kvStore = new KeyValueStoreImpl();
            registry = LocateRegistry.createRegistry(portNum);
            registry.rebind("KeyValueStore", kvStore);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

//    public synchronized void put(String key, String value) throws RemoteException {
//        kvStore.put(key, value);
//    }

//    public synchronized String get(String key) throws RemoteException {
//        return kvStore.get(key);
//    }

//    public synchronized void delete(String key) throws RemoteException {
//       kvStore.delete(key);
//    }


    

    @Override
    protected void run_subroutine() {

    }
}
