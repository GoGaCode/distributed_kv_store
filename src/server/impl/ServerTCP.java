package server.impl;

import server.ServerAbstract;
import utils.LoggerUtils;
import java.util.logging.Level;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import server.KeyValueStore;
import utils.NetworkProtocol;

public class ServerTCP extends ServerAbstract {
    private KeyValueStore keyValueStore;

    public ServerTCP(int portNum) {
        super();
        this.keyValueStore = new KeyValueStore();
        this.setProtocolType(NetworkProtocol.TCP);
        this.setPortNum(portNum);
    }

    public void run_subroutine() {
        try (ServerSocket serverSocket = new ServerSocket(this.portNum)) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Server is listening on port " + this.portNum);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                     DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                    logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "New client connected");

                    while (true) {
                        try {
                            String request = dis.readUTF();
                            String[] parts = request.split(" ", 3);
                            InetAddress clientAddress = socket.getInetAddress();
                            String clientIP = clientAddress.getHostAddress();
                            String command = parts[0];
                            String key = parts[1];
                            String response;
                            logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Received request from " + clientIP + ": " + request);

                            switch (command.toUpperCase()) {
                                case "PUT":
                                    String value = parts[2];
                                    keyValueStore.put(key, value);
                                    response = "Stored " + key + " -> " + value;
                                    break;
                                case "GET":
                                    value = keyValueStore.get(key);
                                    response = (value != null) ? "Retrieved " + key + " -> " + value : "Key not found";
                                    break;
                                case "DELETE":
                                    keyValueStore.delete(key);
                                    response = "Deleted " + key;
                                    break;
                                default:
                                    response = "Invalid command";
                            }

                            logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Response provided: " + response);
                            dos.writeUTF(response);
                            dos.flush();
                        } catch (EOFException e) {
                            logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Client disconnected.");
                            break;  // Exit the inner while loop
                        }
                    }
                } catch (IOException e) {
                    logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
        }
    }

}
