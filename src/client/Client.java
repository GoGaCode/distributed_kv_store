package client;

import java.io.IOException;

/**
* The client must fulfill the following requirements:
* a single client to communicate with the server and perform three basic
* operations:
*       1) PUT (key, value)
*       2) GET (key)
*       3) DELETE (key)
* The client must take the following command line arguments, in the order listed:
*     o The hostname or IP address of the server (it must accept either).
*     o The port number of the server.
* The client should be robust to server failure by using a timeout mechanism to deal with an
*     unresponsive server; if it does not receive a response to a particular request, you should note it in
*     a client log and send the remaining requests.
* You will have to design a simple protocol to communicate packet contents for the three request
*     types along with data passed along as part of the requests (e.g. keys, values, etc.) The client must
*     be robust to malformed or unrequested datagram packets. If it receives such a datagram packet,
*     it should report it in a human*readable way (e.g., “received unsolicited response acknowledging
*     unknown PUT/GET/DELETE with an invalid KEY” * something to that effect to denote an
*     receiving an erroneous request) to your server log.
* Every line the client prints to the client log should be time*stamped with the current system time.
*     You may format the time any way you like as long as your output maintains millisecond
*     precision.
* You must have two instances of your client (or two separate clients):
*     o One that communicates with the server over TCP
*     o One that communicates with the server over UDP
 */

public interface Client {
    void put(String key, String value);
    String get(String key);
    void delete(String key);
    void close() throws IOException;

}
