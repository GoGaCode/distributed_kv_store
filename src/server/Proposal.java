package server;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Proposal object are marshalled and passed among servers for communication
 */
public class Proposal implements Serializable {

  private final Enum opsType;
  private long transId;
  private final String key;
  private final String value;
  private final int serverIndex;
  private final AtomicLong sequence;

  public Proposal(utils.opsType opsType, String key, String value, int serverIndex, long transId) {
    this.opsType = opsType;
    this.key = key;
    this.value = value;
    this.serverIndex = serverIndex;
    this.sequence = new AtomicLong(0);
    this.transId = transId;
  }
  public long getTransId() {
    return transId;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public int getServerIndex() {
    return serverIndex;
  }
  public Enum getOpsType() {
    return opsType;
  }
}
