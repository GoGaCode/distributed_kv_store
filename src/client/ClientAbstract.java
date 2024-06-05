package client;
import utils.NetworkProtocol;
import utils.LoggerUtils;
import java.util.logging.Level;

public abstract class ClientAbstract implements Client {
    // Init the client takes optional argument UDP or TCP as protocol type
    // if nothing provided, use TCP as default
    // Depending on the protocol type the class to use polymorphism to call
    // the appropriate method from subclass
    protected NetworkProtocol protocolType;
    protected Integer portNum;
    protected String IP;
    protected LoggerUtils logger = new LoggerUtils();
    protected static final String LOGGER_NAME = "ClientLogger";
    protected static final String LOG_FILE = "client.log";

    public ClientAbstract(String IP, Integer portNum) {
        this.IP = IP;
        this.portNum = portNum;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public void put(String key, String value) {
        logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "PUT " + key + " " + value);
    }

    public String get(String key) {
        logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "GET " + key);
        return "GET " + key;
    }

    public void delete(String key) {
        logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "DELETE " + key);
    }

    protected void handleServerResponse(String response) {
      if (!(response.contains("Stored")
          || response.contains("Retrieved")
          || response.contains("Deleted")) ||
           response.equals("Invalid command")) {
        logger.log(LOGGER_NAME, LOG_FILE, Level.WARNING, "Unexpected Response from server: " + response);
      }
      else {
          logger.log(LOGGER_NAME, LOG_FILE, Level.INFO, "Response from server: " + response);
      }
    }

    public static class ClientFactory {}
}
