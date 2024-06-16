package client;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import utils.LoggerUtils;

public class UDPClient extends ClientAbstract {
    private DatagramSocket socket;
    private InetAddress address;

    public UDPClient(String hostname, Integer portNum) throws IOException {
        super(hostname, portNum);
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(3000); //Time out mechanism
        this.address = InetAddress.getByName(hostname);
    }

    @Override
    public void put(String key, String value) {
        try {
            // Create the put request
            String message = "PUT " + key + " " + value;
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, portNum);
            // Send the put request to the server
            socket.send(packet);
            // Receive the response from the server
            buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            handleServerResponse(response);
        } catch (SocketTimeoutException e) {
            LoggerUtils.logClient( "Server timeout on PUT request for key: " + key);
        } catch (IOException e) {
            LoggerUtils.logClient( e.getMessage());
        }
    }

    @Override
    public String get(String key) {
        try {
            // Create the get request
            String message = "GET " + key;
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, portNum);
            // Send the get request to the server
            socket.send(packet);
            // Receive the response from the server
            buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            handleServerResponse(response);
            return response;
        } catch (SocketTimeoutException e) {
            LoggerUtils.logClient( "Server timeout on GET request for key: " + key);
        } catch (IOException e) {
            LoggerUtils.logClient( e.getMessage());
        }
        return null;
    }

    @Override
    public void delete(String key) {
        try {
            // Create the delete request
            String message = "DELETE " + key;
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, portNum);
            // Send the delete request to the server
            socket.send(packet);
            // Receive the response from the server
            buffer = new byte[1024];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String response = new String(packet.getData(), 0, packet.getLength());
            handleServerResponse(response);
        } catch (SocketTimeoutException e) {
            LoggerUtils.logClient( "Server timeout on DELETE request for key: " + key);

        } catch (IOException e) {
            LoggerUtils.logClient( e.getMessage());
        }
    }

    // Method to close the socket
    public void close() throws IOException{
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
