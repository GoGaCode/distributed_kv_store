package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import utils.LoggerUtils;
import utils.NetworkProtocol;

public class TCPHandler extends HandlerAbstract {
    private KeyValueStoreImpl keyValueStoreImpl;

    public TCPHandler(int portNum) throws RemoteException {
        super();
        this.keyValueStoreImpl = new KeyValueStoreImpl();
        this.setProtocolType(NetworkProtocol.TCP);
        this.setPortNum(portNum);
    }

    public void run_subroutine() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNum)) {
            LoggerUtils.logServer( "Server is listening on port " + this.portNum);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                     DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                    LoggerUtils.logServer( "New client connected");

                    while (true) {
                        try {
                            String request = dis.readUTF();
                            String[] parts = request.split(" ", 3);
                            InetAddress clientAddress = socket.getInetAddress();
                            String clientIP = clientAddress.getHostAddress();
                            String command = parts[0];
                            String key = parts[1];
                            String response;
                            LoggerUtils.logServer( "Received request from " + clientIP + ": " + request);

                            switch (command.toUpperCase()) {
                                case "PUT":
                                    String value = parts[2];
                                    keyValueStoreImpl.put(key, value);
                                    response = "Stored " + key + " -> " + value;
                                    break;
                                case "GET":
                                    value = keyValueStoreImpl.get(key);
                                    response = (value != null) ? "Retrieved " + key + " -> " + value : "Key not found";
                                    break;
                                case "DELETE":
                                    keyValueStoreImpl.delete(key);
                                    response = "Deleted " + key;
                                    break;
                                default:
                                    response = "Invalid command";
                            }

                            LoggerUtils.logServer( "Response provided: " + response);
                            dos.writeUTF(response);
                            dos.flush();
                        } catch (EOFException e) {
                            LoggerUtils.logServer( "Client disconnected.");
                            break;  // Exit the inner while loop
                        }
                    }
                } catch (IOException e) {
                    LoggerUtils.logServer( e.getMessage());
                }
            }
        } catch (IOException e) {
            LoggerUtils.logServer( e.getMessage());
        }
    }

}
