import java.net.ServerSocket;
import java.net.*;
import java.io.*;
import server.Server;
import server.impl.ServerTCP;
import server.impl.ServerUDP;

public class ServerMain {
    public static void main(String[] args) throws Exception{
        // Open the server socket
        if (args.length !=1) {
            throw new IllegalArgumentException("Usage: Server <port>");
        }
        int portNum = Integer.parseInt(args[0]);
        // Server server = new ServerTCP(portNum);
         Server server = new ServerUDP(portNum);
        server.run();
        ;}
}
