package server;

import java.io.IOException;

/**
 * ServerApp class is responsible for starting the server processes.
 */
public class ServerApp {
  public static void main(String[] args) throws Exception {
    // Open the server socket
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: ServerApp <rpc-port-num>");
    }
    int baseRpcPortNum = Integer.parseInt(args[0]);

    // Primary server initiate the registry
    // Secondary server retrieve and add to the registry
    String serverType = "primary";
    for (int i = 0; i < 5; i++) {
      startServer(i, serverType);
      serverType = "secondary";
    }
    keepServerRunning();
  }

  private static void startServer(int kvStoreIndex, String serverType) throws IOException {
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            "java", "-cp", "./", "server.RPCHandler", serverType, Integer.toString(kvStoreIndex));
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
}
