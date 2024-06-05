package client.impl;

import client.ClientAbstract;
import utils.LoggerUtils;

import java.net.SocketTimeoutException;
import java.util.logging.Level;

import java.io.*;
import java.net.Socket;

public class ClientTCP extends ClientAbstract {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    public ClientTCP(String IP, Integer portNum) throws IOException {
        super(IP, portNum);
        // Create a socket object
        this.socket = new Socket(IP, portNum);
        this.socket.setSoTimeout(3000);

        // Create I/O streams for communication with the server
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dis = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void put(String key, String value) {
        try {
            // Send the put request to the server
            dos.writeUTF("PUT " + key + " " + value);
            dos.flush();
            // Read the response from the server
            String response = dis.readUTF();
            handleServerResponse(response);
        } catch (SocketTimeoutException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.WARNING, "Server timeout on PUT request for key: " + key);
        } catch (IOException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
        }
    }

    @Override
    public String get(String key) {
        try {
            // Send the get request to the server
            dos.writeUTF("GET " + key);
            dos.flush();
            // Read the response from the server
            String response = dis.readUTF();
            handleServerResponse(response);
            return response;
        } catch (SocketTimeoutException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.WARNING, "Server timeout on GET request for key: " + key);
        } catch (IOException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
        }
        return null;
    }

    @Override
    public void delete(String key) {
        try {
            // Send the delete request to the server
            dos.writeUTF("DELETE " + key);
            dos.flush();
            // Read the response from the server
            String response = dis.readUTF();
            handleServerResponse(response);
        } catch (SocketTimeoutException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.WARNING, "Server timeout on DELETE request for key: " + key);
        } catch (IOException e) {
            logger.log(LOGGER_NAME, LOG_FILE, Level.SEVERE, e.getMessage());
        }
    }

    // Method to close the socket
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
