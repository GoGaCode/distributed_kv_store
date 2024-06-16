package client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.KeyValueStore;
import utils.LoggerUtils;

public class RPCClient extends ClientAbstract {
    private Registry registry;
    private KeyValueStore kvStore;

    public RPCClient(int portNum) {
        super();
        try {
            registry = LocateRegistry.getRegistry("my-server", portNum);
            kvStore = (KeyValueStore) registry.lookup("KeyValueStore");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, String value) {
        super.put(key, value);
        try {
            kvStore.put(key, value);
            } catch (RemoteException e) {
              e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        super.get(key);
        try {
            String response = kvStore.get(key);
            LoggerUtils.logClient("Key retrieved: " + response);
            return response;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(String key) {
        super.delete(key);
        try{
            kvStore.delete(key);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {

    }

}
