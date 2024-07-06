package server;

import java.io.IOException;

public class ServerApp {
  public static void main(String[] args) throws Exception {
    // Open the server socket
    if (args.length != 1) {
      throw new IllegalArgumentException(
          "Usage: ServerApp <rpc-port-num>");
    }
    int baseRpcPortNum = Integer.parseInt(args[0]);

    int kvStoreIndex = 0;
    for (int i = 0; i < 1; i++) {
      kvStoreIndex = kvStoreIndex + i;
      startServer(kvStoreIndex);
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

}
