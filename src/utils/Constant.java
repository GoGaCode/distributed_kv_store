package utils;

public class Constant {
    private Constant() {
    }

    public static final int RPC_PORT_NUM = 1099;
    public static final int SERVER_COUNT = 5;
    public static final String KV_STORE_OPS_PREFIX = "kvStoreInterface";
    public static final String ACCEPTOR_PREFIX = "Acceptor";
    public static final String PROPOSER_PREFIX = "Proposer";
    public static final String INIT_FLAG_KEY = "Coordinator";
    public static final String LEARNER_PREFIX = "Learner";
    public static final String ID_GENERATOR_NAME = "IDGenerator";
}
