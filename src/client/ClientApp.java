package client;

import java.util.Scanner;
import utils.LoggerUtils;

public class ClientApp {
    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: ClientApp <host-ip> <port> <protocol>");
        }
        String IP = args[0];
        int portNum = Integer.parseInt(args[1]);
        Client clientInstance = null;

        try {
            String protocol = args[2];
            protocol = protocol.toUpperCase();
            // Create client instance based on protocol
            if (protocol.equals("TCP")) {
                clientInstance = new TCPClient(IP, portNum);
            } else if (protocol.equals("UDP")) {
                clientInstance = new UDPClient(IP, portNum);
            } else {
                System.out.println("Invalid protocol: " + protocol + ". Please use TCP or UDP.");
                throw new IllegalArgumentException("Usage: ClientApp <host-ip> <port> <protocol>");
            }

            // Perform multiple operations
            clientInstance.put("key1", "value1");
            clientInstance.put("key2", "value2");
            clientInstance.put("key3", "value3");
            clientInstance.put("key4", "value4");
            clientInstance.put("key5", "value5");

            clientInstance.get("key1");
            clientInstance.get("key2");
            clientInstance.get("key3");
            clientInstance.get("key4");
            clientInstance.get("key5");

            clientInstance.delete("key1");
            clientInstance.delete("key2");
            clientInstance.delete("key3");
            clientInstance.delete("key4");
            clientInstance.delete("key5");

            clientInstance.get("key1");
            clientInstance.get("key2");
            clientInstance.get("key3");
            clientInstance.get("key4");
            clientInstance.get("key5");

            // Continue to listen for console input after initial operations
            Scanner scanner = new Scanner(System.in);
            String command;
            while (true) {
                System.out.print("Enter command (PUT key value / GET key / DELETE key / EXIT): ");
                command = scanner.nextLine();
                String[] parts = command.split(" ");
                String action = parts[0].toUpperCase();

                try {
                    switch (action) {
                        case "PUT":
                            if (parts.length == 3) {
                                String key = parts[1];
                                String value = parts[2];
                                clientInstance.put(key, value);
                                LoggerUtils.logClient("PUT " + key + " " + value);
                            } else {
                                System.out.println("Invalid PUT command. Usage: PUT key value");
                            }
                            break;
                        case "GET":
                            if (parts.length == 2) {
                                String key = parts[1];
                                clientInstance.get(key);
                                LoggerUtils.logClient("GET " + key);
                            } else {
                                System.out.println("Invalid GET command. Usage: GET key");
                            }
                            break;
                        case "DELETE":
                            if (parts.length == 2) {
                                String key = parts[1];
                                clientInstance.delete(key);
                                LoggerUtils.logClient("DELETE " + key);
                            } else {
                                System.out.println("Invalid DELETE command. Usage: DELETE key");
                            }
                            break;
                        case "EXIT":
                            clientInstance.close();
                            scanner.close();
                            System.out.println("Client closed.");
                            return;
                        default:
                            System.out.println("Unknown command. Please use PUT, GET, DELETE, or EXIT.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
