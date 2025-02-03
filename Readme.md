## Distributed KV Store
This version of the KV store implements a Paxo algorithm that can stand a single server failure.
Client and use PUT, GET, and DELETE RPC APIs calls to modify the content in the KV store.
The server is containerized and run on local.

### Design
![distributed_kvstore](./distributed_kvStore_design-FlowChart.drawio.png)

### Where are Phase IV Implementations?
- Five kvStore/Server processes are started as 5 processes in `ServerApp`
- Acceptor, Learner, Proposer thread initialization is in `RPCHandler`
- Paxos implementation is in `kvStoreOpsPaxos`, `AccptorImpl`, `LearnerImpl`, `ProposerImpl`
- When ***acceptor fails***, a msg will be logged in the server log
- Once the server and a client instances are started, please check server log to see the Paxos commit in action

### How to run the project
#### Instructions
Navigate to `project1_repo/src`
```shell
cd ~/project1_repo/src
```

In the `project1_repo/src`

Start the server.
```shell
chmod +x deploy.sh
bash deploy.sh
```

Start each client on a different console. Starting more clients is possible by giving a different pod name. Only Protocol and Port number supported are RPC and 1099 respectively. There are total 5 servers, client can connect to any of them by specifying the server number.
```shell
chmod +x run_client.sh
bash run_client.sh my-client-one RPC 1099 1# start the first client, connecting to the 1st server
bash run_client.sh my-client-two RPC 1099 2# start the second client, connecting to the 2nd server
```

The client will initialize the kv store. Then followed by 5 Put, 5 Get, 5 Delete operations to demonstrate the 
working of the client

After the automatic operations to demonstrate the client-server communication, the client will be in interactive mode. The client can be used to perform the following operations, feel free to interact with the server through client.
```shell
Enter command (PUT key value / GET key / DELETE key / EXIT): 
PUT key6 value6
GET key6
DELETE key6
EXIT
```