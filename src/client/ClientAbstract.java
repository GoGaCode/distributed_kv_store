package client;
import server.kvStoreOps;
import utils.LoggerUtils;

import java.rmi.RemoteException;

public abstract class ClientAbstract implements Client {
    // Init the client takes optional argument UDP or TCP as protocol type
    // if nothing provided, use TCP as default
    // Depending on the protocol type the class to use polymorphism to call
    // the appropriate method from subclass
    protected Integer portNum;
    protected String hostname;

    protected LoggerUtils logger = new LoggerUtils();
    protected static final String LOGGER_NAME = "ClientLogger";
    protected static final String LOG_FILE = "client.log";
    protected kvStoreOps kvStore;

    public ClientAbstract() {
    }
    public ClientAbstract(String hostname, Integer portNum) {
        this.hostname = hostname;
        this.portNum = portNum;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public void put(String key, String value) {
        LoggerUtils.logClient( "PUT " + key + " " + value);
    }

    public String get(String key) {
        LoggerUtils.logClient( "GET " + key);
        return "GET " + key;
    }

    public void delete(String key) {
        LoggerUtils.logClient( "DELETE " + key);
    }

    protected void handleServerResponse(String response) {
      if (!(response.contains("Stored")
          || response.contains("Retrieved")
          || response.contains("Deleted")
          || response.contains("Key")) ||
           response.equals("Invalid command")) {
        LoggerUtils.logClient( "Unexpected Response from server: " + response);
      }
      else {
          LoggerUtils.logClient( "Response from server: " + response);
      }
    }

    public void setWaitTime(int waitTime) throws RemoteException {
        LoggerUtils.logClient( "Setting request wait time to " + waitTime + " milli-seconds");
    }

    public static class ClientFactory {}
}
