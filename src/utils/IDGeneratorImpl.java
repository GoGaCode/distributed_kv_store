package utils;

import server.IDGenerator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGeneratorImpl extends UnicastRemoteObject implements IDGenerator {
    private final AtomicInteger counter;

    public IDGeneratorImpl() throws RemoteException {
        super();
        this.counter = new AtomicInteger(0);
    }

    @Override
    public int getNextID() throws RemoteException {
        return counter.getAndIncrement();
    }

    public static void main(String[] args) {
        try {
            IDGeneratorImpl idGenerator = new IDGeneratorImpl(); // Starting ID from 1
            java.rmi.registry.LocateRegistry.createRegistry(1099).rebind("IDGenerator", idGenerator);
            System.out.println("IDGenerator bound in registry");

            // Example usage
            for (int i = 0; i < 10; i++) {
                System.out.println("Generated ID: " + idGenerator.getNextID());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
