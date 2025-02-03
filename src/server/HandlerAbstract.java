package server;

import static utils.Constant.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import utils.Constant;
import utils.LoggerUtils;

public abstract class HandlerAbstract implements Server {
    protected Integer portNum;
    protected String IP;

    protected final int serverIndex;
    protected final String kvStoreOpsName;
    protected final String acceptorName;
    protected final String proposerName;
    protected final String learnerName;
    
    protected static final String LOGGER_NAME = "ServerLogger";
    protected static final String LOG_FILE = "server.log";

    public HandlerAbstract(int serverIndex) {
        this.serverIndex = serverIndex;
        this.kvStoreOpsName = KV_STORE_OPS_PREFIX + Integer.toString(serverIndex);
        this.acceptorName = ACCEPTOR_PREFIX + Integer.toString(serverIndex);
        this.proposerName = PROPOSER_PREFIX + Integer.toString(serverIndex);
        this.learnerName = Constant.PROPOSER_PREFIX + Integer.toString(serverIndex);
    }

    public String get(String key) throws RemoteException {
        LoggerUtils.logServer( "GET " + key, this.serverIndex);
        return "GET " + key;
    }

    public void delete(String key) throws RemoteException {
        LoggerUtils.logServer( "DELETE " + key, this.serverIndex);
    }

    public void getServerIP() {
        try {
            // Get the local host address
            InetAddress localHost = InetAddress.getLocalHost();
            // Get the IP address as a string
            String ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            LoggerUtils.logServer( e.getMessage(), this.serverIndex);
        }
    }

}
