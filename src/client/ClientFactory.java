package client;

import utils.NetworkProtocol;
import static utils.NetworkProtocol.TCP;
import static utils.NetworkProtocol.UDP;

public class ClientFactory {
    public static ClientAbstract createClient(NetworkProtocol type, String hostname, Integer portNum) throws Exception {
        if (type.equals(TCP)) {
            return new TCPClient(hostname, portNum);
        } else if (type.equals(UDP)) {
            return new UDPClient(hostname, portNum);
        }
        return null;
    }
}
