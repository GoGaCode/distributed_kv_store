package client;

import client.impl.ClientTCP;
import client.impl.ClientUDP;
import utils.NetworkProtocol;
import static utils.NetworkProtocol.TCP;
import static utils.NetworkProtocol.UDP;

public class ClientFactory {
    public static ClientAbstract createClient(NetworkProtocol type, String IP, Integer portNum) throws Exception {
        if (type.equals(TCP)) {
            return new ClientTCP(IP, portNum);
        } else if (type.equals(UDP)) {
            return new ClientUDP(IP, portNum);
        }
        return null;
    }
}
