package server;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Transaction object are marshalled and passed among servers for communication
 */
public class Transaction implements Serializable {

  private final Enum opsType;
  private String transId;
  private final String key;
  private final String value;
  private final int serverIndex;
  private final AtomicLong sequence;

  public Transaction(utils.opsType opsType, String key, String value, int serverIndex) {
    this.opsType = opsType;
    this.key = key;
    this.value = value;
    this.serverIndex = serverIndex;
    this.sequence = new AtomicLong(0);
    this.transId = genTransactionId();
  }

  public String genTransactionId() {
    long timestamp = System.currentTimeMillis();
    long seq = sequence.getAndIncrement();
    return this.transId = Integer.toString(serverIndex) + "-" + timestamp + "-" + seq;
  }
  public String getTransId() {
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
