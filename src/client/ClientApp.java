package client;

import java.rmi.RemoteException;
import java.util.Scanner;
import utils.LoggerUtils;

public class ClientApp {

  public static void main(String[] args) throws Exception {

    if (args.length != 4) {
      throw new IllegalArgumentException("Usage: bash run_client.sh <client-pod-name> <protocol> <port num>");
    }
    Client clientInstance = null;

    try {
      String hostname = args[0];
      String protocol = args[1];
      int serverIndex = Integer.parseInt(args[3]) % 5; // keep index range from 0 to 4
      protocol = protocol.toUpperCase();
      int portNum = Integer.parseInt(args[2]);
      // Create client instance based on protocol
      if (protocol.equals("RPC")) {
        clientInstance = new RPCClient(portNum, serverIndex);
      } else {
        System.out.println("Invalid protocol: " + protocol + ". Please use RPC.");
        throw new IllegalArgumentException("Usage: ClientApp <host-ip> <port> <protocol>");
      }

      ClientApp.populateKeyValueStore(clientInstance);

      LoggerUtils.logClient("###################DEMO START#####################");
      LoggerUtils.logClient("Starting demo of client get, delete, put operations");
      LoggerUtils.logClient("Client started with protocol: " + protocol);
      // Perform initial GET operations
      clientInstance.setWaitTime(10);
      clientInstance.get("CA");
      clientInstance.get("NY");
      clientInstance.get("TX");
      clientInstance.get("FL");
      clientInstance.get("WA");

      // Perform DELETE operations
      clientInstance.delete("CA");
      clientInstance.delete("NY");
      clientInstance.delete("TX");
      clientInstance.delete("FL");
      clientInstance.delete("WA");

      // Perform GET operations again to verify DELETEs
      clientInstance.get("CA");
      clientInstance.get("NY");
      clientInstance.get("TX");
      clientInstance.get("FL");
      clientInstance.get("WA");

      // Perform PUT operations
      clientInstance.put("CA", "California");
      clientInstance.put("NY", "New York");
      clientInstance.put("TX", "Texas");
      clientInstance.put("FL", "Florida");
      clientInstance.put("WA", "Washington");

      // Perform GET operations again to verify PUTs
      clientInstance.get("CA");
      clientInstance.get("NY");
      clientInstance.get("TX");
      clientInstance.get("FL");
      clientInstance.get("WA");

      LoggerUtils.logClient("###################DEMO ENDS#####################");

      clientInstance.setWaitTime(1000);
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

  private static void populateKeyValueStore(Client clientInstance) throws RemoteException {
    // Synchronize to avoid multiple threads populating the store
    synchronized (ClientApp.class) {
      if (clientInstance.isServerInitialized()) {
        LoggerUtils.logClient(
            "KeyValueStore already initialized with states. Skipping pre-population.");
        return;
      }
      LoggerUtils.logClient("####################INIT START####################");
      LoggerUtils.logClient("Pre-populating KeyValueStore with states");
      String[][] states = {
        {"CA", "California"},
        {"FL", "Florida"},
        {"GA", "Georgia"},
        {"HI", "Hawaii"},
        {"ID", "Idaho"},
        {"IL", "Illinois"},
        {"IN", "Indiana"},
        {"IA", "Iowa"},
        {"KS", "Kansas"},
        {"MA", "Massachusetts"},
        {"MI", "Michigan"},
        {"MN", "Minnesota"},
        {"MS", "Mississippi"},
        {"NH", "New Hampshire"},
        {"NJ", "New Jersey"},
        {"NM", "New Mexico"},
        {"NY", "New York"},
        {"OR", "Oregon"},
        {"PA", "Pennsylvania"},
        {"RI", "Rhode Island"},
        {"SC", "South Carolina"},
        {"VT", "Vermont"},
        {"VA", "Virginia"},
        {"WA", "Washington"},
        {"WV", "West Virginia"},
        {"TX", "Texas"}
      };

      clientInstance.setWaitTime(10);
      for (String[] state : states) {
        clientInstance.put(state[0], state[1]);
      }
      clientInstance.setWaitTime(1000);
        clientInstance.setServerInitialized(true);
    }
    LoggerUtils.logClient("####################INIT ENDS####################");

  }
}
