package server;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDGenerator extends Remote {
    int getNextID() throws RemoteException;
}