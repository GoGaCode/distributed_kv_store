package server;


public class ServerApp {
    public static void main(String[] args) throws Exception {
        // Open the server socket
        if (args.length != 3) {
            throw new IllegalArgumentException("Usage: ServerApp <tcp-port-num> <udp-port-num> <rpc-port-num>" );
        }
        int tcpPortNum = Integer.parseInt(args[0]);
        int udpPortNum = Integer.parseInt(args[1]);
        int rpcPortNum = Integer.parseInt(args[2]);

        // Start the threads
        Thread tcpThread = new Thread(new TCPHandler(tcpPortNum));
        Thread udpThread = new Thread(new UDPHandler(udpPortNum));
        Thread rpcThread = new Thread(new RPCHandler(rpcPortNum));
        tcpThread.start();
        udpThread.start();
        rpcThread.start();

        // Execute using threadpool
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//        for (int i = 1; i<=6; i++) {
//            final int taskId= i;
//            if (i % 3 == 0) {
//                executor.submit(new RPCHandler(rpcPortNum));
//            } else if (i % 3 == 1) {
//                executor.submit(new TCPHandler(tcpPortNum));
//            } else {
//                executor.submit(new UDPHandler(udpPortNum));
//            }
//      }

//        executor.shutdown();
        // Join the threads to the main thread to ensure they keep running
        tcpThread.join();
        udpThread.join();
        rpcThread.join();
    }
}
