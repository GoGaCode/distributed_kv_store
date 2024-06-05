import client.Client;
import client.impl.ClientTCP;
import client.impl.ClientUDP;
import java.util.Scanner;


public class ClientMain {
    public static void main(String[] args) throws Exception {

        if (args.length !=2) {
            throw new IllegalArgumentException("Usage: Client <port>");
        }
        String IP = args[0];
        int portNum = Integer.parseInt(args[1]);
//        ClientTCP client = new ClientTCP(IP, portNum);


        try {
            // ClientTCP clientInstance = new ClientTCP(IP, portNum);
            ClientUDP clientInstance = new ClientUDP(IP, portNum);
            Scanner scanner = new Scanner(System.in);
            // Perform multiple operations
            clientInstance.put("key1", "value1");
            clientInstance.put("key2", "value2");
            clientInstance.put("key3", "value3");
            clientInstance.put("key4", "value4");
            clientInstance.put("key5", "value5");

            System.out.println("GET: key1 => " + clientInstance.get("key1"));
            System.out.println("GET: key2 => " + clientInstance.get("key2"));
            System.out.println("GET: key3 => " + clientInstance.get("key3"));
            System.out.println("GET: key4 => " + clientInstance.get("key4"));
            System.out.println("GET: key5 => " + clientInstance.get("key5"));

            clientInstance.delete("key1");
            clientInstance.delete("key2");
            clientInstance.delete("key3");
            clientInstance.delete("key4");
            clientInstance.delete("key5");

            System.out.println("GET after DELETE: key1 => " + clientInstance.get("key1"));
            System.out.println("GET after DELETE: key2 => " + clientInstance.get("key2"));
            System.out.println("GET after DELETE: key3 => " + clientInstance.get("key3"));
            System.out.println("GET after DELETE: key4 => " + clientInstance.get("key4"));
            System.out.println("GET after DELETE: key5 => " + clientInstance.get("key5"));

            clientInstance.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
