##  Project Phase I

### How to run the project

Following is executed on first console

Compile the code using the following commands
```shell
javac server.java
javac client.java
```

```shell
java server --port 32000 # Starts the server on port 32000
```

Following is executed on second console
```shell
java client --server_ip 128.111.49.44 --port 32000 --protocol TCP # Initializes the client with the specified parameters
java client demo # Performs 5 PUT, 5 GET, 5 DELETE operations
```

## -------------End of Project Phase I-------------

Note: The rest is extra work that I did for personal learning

##  Project Phase II Requirements
For this project, you will extend Project #1 in two distinct ways.
1) You need to enable your client and server to communicate using Remote Procedure Calls (RPC) instead of sockets. If you’ve implemented Project #1 in Java, you may want to look into and leverage Java RMI for RPC communication. However, there are multiple other RPC frameworks you can leverage (with their own IDLs) to provide the stubs/skeletons necessary across the network. An additional example that enables the use of multiple languages is Apache Thrift (http://thrift.apache.org/
   (Links to an external site.)
2) You need to make your server multi-threaded such that you can handle multiple outstanding client requests at once. You may decide how to thread your server. One approach may be to use thread pools similar to other servers, although there are certainly many ways to do this. The key result is that your servers should be able to handle requests from multiple running instances of you client doing concurrent PUT, GET, and DELETE operations. Due to the addition of multi-threading, you will need to handle mutual exclusion.
   As in project #1, you should use your client to pre-populate the Key-Value store with data and a set of keys. The composition of data is up to you in terms of what you want to store. Once the key-value store is populated, your client must do at least 5 of each operation: 5 PUTs, 5 GETs, 5 DELETEs.

##  Project Phase III Requirements
Assignment Overview
For this project, you will extend Project #2 in two distinct ways.
1) Replicate your Key-Value Store ServerMain across 5 distinct servers. In project #2, you used a single
   instance of a Key-Value Store server. Now, to increase ServerMain bandwidth and ensure availability, you need to replicate your key-value store at each of 5 different instances of your servers. Note that your client code should not have to change radically, only in that your clients should be able to contact any of the five KV replica servers instead of a single server and get consistent data back from any of the replicas (in the case of GETs). You client should also be able to issue PUT operations and DELETE operations to any of the five replicas.
2) On PUT or DELETE operations you need to ensure each of the replicated KV stores at each replica is consistent. To do this, you need to implement a two-phase protocol for updates. We will assume no servers will fail such that 2 Phase Commit will not stall, although you may want to defensively code your 2PC protocol with timeouts to be sure. Consequently, whenever a client issues a PUT or a DELETE to *any* server replica, that receiving replica will ensure the updates have been received (via ACKs) and commited (via Go messages with accompanying ACKs).

##  Project Phase IV Requirements
For this project, you will extend Project #3 by adding fault tolerance and achieving consensus of updates amongst replicated state machine KV-store servers using Paxos as described in in the Lamport paper, "Paxos made simple" that you will deploy and test on the clustered system available through the University.
1) In Project #3 you replicated your Key-Value Store ServerMain from Project #2 across 5 distinct servers and used 2PC to ensure consistency across replicas on KV-store operations (minimally PUT & DELETE). However, as we've discussed in class two-phase commit protocols are not fault tolerant. Your new goal for Project #4 is to integrate the capability to ensure continual operation of your KV-store despite replica failures. To achieve this goal you will implement Paxos to realize fault-tolerant consensus amongst your replicated servers. Functionally, you must implement and integrate the Paxos roles we described in class, and as described in the Lamport papers, including the Proposers, Acceptors, and Learners. The goal here is to focus on the Paxos implementation and algorithmic steps involved in realizing consensus in event ordering. ClientMain threads may generate requests to any of the replicas at any time. To minimize the potential for live lock, you may choose to use leader election amongst the proposers, however, that is not a strict requirement of the project.
2) A second requirement for this project is that the acceptors must be configured to "fail" at random times. Each of the roles within Paxos may be implemented at threads or processes - that's up to you to determine how to implement (I'd use threads). Assuming you use threads for each role, at a minimum the acceptor threads should "fail" periodically, which could be done as simply as having a timeout that kills off the thread (or returns) after some random period of time. A new acceptor thread could then be restarted after another delay which should resume the functions of the previous acceptor thread, even though it clearly won't have the same state as the previously killed thread. Once this is completed, you may earn extra credit for the project if all roles are constructed to randomly fail and restart, but only the failure/restart of the acceptor is required. This should make it clear how Paxos overcomes replicated server failures.

### Planned Eventual Directory Structure to Accommodate Future Phases
```css {Planned Project Structure}
src/
├── client/
│   ├── ClientAbstract.java
│   ├── ClientFactory.java
│   ├── ClientInterface.java
│   ├── impl/
│   │   ├── ClientTCP.java
│   │   ├── ClientUDP.java
│   ├── rpc/
│       ├── RPCClient.java
├── server/
│   ├── ServerAbstract.java
│   ├── ServerFactory.java
│   ├── ServerInterface.java
│   ├── impl/
│   │   ├── ServerTCP.java
│   │   ├── ServerUDP.java
│   ├── multithreaded/
│       ├── MultiThreadedServer.java
│   ├── replication/
│       ├── ReplicatedServer.java
│   ├── paxos/
│       ├── Proposer.java
│       ├── Acceptor.java
│       ├── Learner.java
├── utils/
│   ├── KeyValueStore.java
│   ├── Logger.java
│   ├── ProtocolUtils.java
├── NetworkProtocol.java
├── Main.java
├── Dockerfile.client
├── Dockerfile.server
├── kubernetes/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
```