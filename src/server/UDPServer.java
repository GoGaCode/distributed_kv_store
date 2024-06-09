package server;


import utils.LoggerUtils;
import utils.NetworkProtocol;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

public class UDPServer extends ServerAbstract {

    private KeyValueStore keyValueStore;

    public UDPServer(int portNum) {
        super();
        this.keyValueStore = new KeyValueStore();
        this.setProtocolType(NetworkProtocol.UDP);
        this.setPortNum(portNum);
    }

    @Override
    public void run_subroutine() {
        try (DatagramSocket socket = new DatagramSocket(this.portNum)) {
            LoggerUtils.logServer( "Server is listening on port " + this.portNum);

            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String request = new String(packet.getData(), 0, packet.getLength());
                    // Get the client's IP address

                    InetAddress clientAddress = packet.getAddress();
                    String clientIP = clientAddress.getHostAddress();

                    LoggerUtils.logServer( "Received clientIP=" + clientIP + " request: " + request);

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

                    LoggerUtils.logServer( "Response provided: " + response);
                    byte[] responseBytes = response.getBytes();
                    int clientPort = packet.getPort();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                } catch (IOException e) {
                    LoggerUtils.logServer( e.getMessage());
                }
            }
        } catch (IOException e) {
            LoggerUtils.logServer( e.getMessage());
        }
    }
}
