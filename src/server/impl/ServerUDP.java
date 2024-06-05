package server.impl;

import server.ServerAbstract;
import server.KeyValueStore;
import utils.LoggerUtils;
import java.util.logging.Level;
import utils.NetworkProtocol;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.InetAddress;
import java.io.IOException;

public class ServerUDP extends ServerAbstract {

    private KeyValueStore keyValueStore;

    public ServerUDP(int portNum) {
        super();
        this.keyValueStore = new KeyValueStore();
        this.setProtocolType(NetworkProtocol.UDP);
        this.setPortNum(portNum);
    }

    @Override
    public void run_subroutine() {
        try (DatagramSocket socket = new DatagramSocket(this.portNum)) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Server is listening on port " + this.portNum);

            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String request = new String(packet.getData(), 0, packet.getLength());
                    // Get the client's IP address

                    InetAddress clientAddress = packet.getAddress();
                    String clientIP = clientAddress.getHostAddress();

                    logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Received clientIP=" + clientIP + " request: " + request);

                    String[] parts = request.split(" ", 3);
                    String command = parts[0];
                    String key = parts[1];
                    String response;

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
                    byte[] responseBytes = response.getBytes();
                    int clientPort = packet.getPort();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                } catch (IOException e) {
                    logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
        }
    }
}
