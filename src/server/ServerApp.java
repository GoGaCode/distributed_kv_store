package server;

import utils.IDGeneratorImpl;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static utils.Constant.ID_GENERATOR_NAME;
import static utils.Constant.SERVER_COUNT;

/** ServerApp class is responsible for starting the server processes. */
public class ServerApp {
  public static void main(String[] args) throws Exception {
    // Open the server socket
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: ServerApp <rpc-port-num>");
    }
    int baseRpcPortNum = Integer.parseInt(args[0]);
    // Create or retrieve the shared RMI registry on port 1099
    createSharedRegistry();

    // Primary server initiate the registry
    // Secondary server retrieve and add to the registry
    for (int i = 0; i < SERVER_COUNT; i++) {
      startServer(i);
    }
    keepServerRunning();
  }

  private static void startServer(int kvStoreIndex) throws IOException {
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            "java", "-cp", "./", "server.RPCHandler", Integer.toString(kvStoreIndex));
    processBuilder.inheritIO();
    processBuilder.start();
  }

  private static void keepServerRunning() {
    try {
      // This loop keeps the server running until an interrupt signal is received
      while (true) {
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      // Handle the interrupt signal
      System.out.println("Server interrupted, shutting down.");
    }
  }

  private static void createSharedRegistry() {
    try {
      Registry registry;
      try {
        // Try to get an existing registry
        registry = LocateRegistry.getRegistry(1099);
        registry.list(); // This will throw an exception if the registry does not already exist
      } catch (Exception e) {
        // No registry exists, create a new one
        registry = LocateRegistry.createRegistry(1099);
        System.out.println("Created new RMI registry on port 1099.");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
