package server;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        // Open the server socket
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: ServerApp <tcp-port-num> <udp-port-num>");
        }
        int tcpPortNum = Integer.parseInt(args[0]);
        int udpPortNum = Integer.parseInt(args[1]);

        Server tcpServer = new TCPServer(tcpPortNum);
        Server udpServer = new UDPServer(udpPortNum);

        // Create threads for TCP and UDP servers
        Thread tcpThread = new Thread(() -> {
            try {
                tcpServer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread udpThread = new Thread(() -> {
            try {
                udpServer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Start the threads
        tcpThread.start();
        udpThread.start();

        // Join the threads to the main thread to ensure they keep running
        tcpThread.join();
        udpThread.join();
    }
}
