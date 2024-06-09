package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import utils.LoggerUtils;
import java.util.logging.Level;
import utils.NetworkProtocol;

public abstract class ServerAbstract implements Server {
    protected NetworkProtocol protocolType;
    protected Integer portNum;
    protected String IP;


    protected static final String LOGGER_NAME = "ServerLogger";
    protected static final String LOG_FILE = "server.log";

    public void put(String key, String value) {
        LoggerUtils.logServer( "PUT " + key + " " + value);
    }

    public String get(String key) {
        LoggerUtils.logServer( "GET " + key);
        return "GET " + key;
    }

    public void delete(String key) {
        LoggerUtils.logServer( "DELETE " + key);
    }

    public void getServerIP() {
        try {
            // Get the local host address
            InetAddress localHost = InetAddress.getLocalHost();
            // Get the IP address as a string
            String ipAddress = localHost.getHostAddress();
            LoggerUtils.logServer( "Server IP address: " + ipAddress);
        } catch (UnknownHostException e) {
            LoggerUtils.logServer( e.getMessage());
        }
    }

    public void setProtocolType(NetworkProtocol protocolType) {
        this.protocolType = protocolType;
    }

    public void setPortNum(Integer portNum) {
        this.portNum = portNum;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void run() {
        getServerIP();
        run_subroutine();
    }

    protected abstract void run_subroutine();
}
