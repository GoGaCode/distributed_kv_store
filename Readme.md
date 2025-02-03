## Where are Phase IV Implementations?
- Five kvStore/Server processes are started as 5 processes in `ServerApp`
- Acceptor, Learner, Proposer thread initialization is in `RPCHandler`
- Paxos implementation is in `kvStoreOpsPaxos`, `AccptorImpl`, `LearnerImpl`, `ProposerImpl`
- When ***acceptor fails***, a msg will be logged in the server log
- Once the server and a client instances are started, please check server log to see the Paxos commit in action

[//]: # (## System Design Diagram)

[//]: # (![System Diagram]&#40;distributed_kvStore_design-FlowChart.drawio.png&#41;)


## How to run the project

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

## Executive Summary
Part IV of the project is about consensus under fault tolerance and algorithm that handles fault tolerance. Distributed 
systems consist of multiple components that each could fail at random time. To ensure the system is still operational 
and data consistent is maintained, we need to implement a consensus algorithm. Paxos consensus algorithm will work as
long as the majority of the server are up and running. Also, to test that the algorithm is implemented correctly, we
simulate random failure of the acceptor.

## Technical Impression
The Paxos algorithm takes time to understand. The materials provided by the lecturer was helpful. I also watched some youtube
video by MIT which explains the idea of Paxos well. The implementation of the Paxos requires thoughts on the organization
of the code. First, I need to come up with the interface for the Acceptor, Learner, and Proposer. Also, I adapted the
client facing code that combines logics of Acceptor, Learn, and Proposer code. Thankfully, the structure of the code is 
similar to the one in phase III. So I could spend less time on figuring out the how to structure the code, and focus
more time on the consensus algorithm.

The biggest challenging I had is about implementation the failure of acceptor. As suggested  by the assignment instruction,
I implemented Acceptor, Learner, and Proposer as independent threads in a kvStore/Server process. However, instead of suggested
by the instruction, timing out the acceptor thread does not automatically fail the acceptor that registered in the registry.
This is rather hard to grasp as the all the threads (Acceptor, Learner, Proposer)'s life cycle is independent of the
object binding in the registry. Meaning, when the Acceptor, Learner, Proposer finishing running, the object in registry
pointing to those Acceptor, Learner, and Proposer could still respond to calls. 

Since I am running the server in a container, debugging using debugger is a big hard  to do. I have to talk to a few 
people to land on the idea to unbind the acceptor from the registry to simulate the failure
of the acceptor thread. Also, after unbind the acceptor, I need to refactor the code so that the proposer can access
the latest acceptor object instead of the stale entry that were already removed.

[//]: # (## Executive Summary)

[//]: # (Purpose and scope of the assignment helps us to get acquainted with how RPC is used in distributed system. What characterises RPC, and also how RPC could be implemented.)

[//]: # (RPC is a way to invoke a process remotely as though that process is local. It is a way of communication between processes running on different machine.)

[//]: # (To achieve this, RPC need a Interface Definition Language to define how client can interact with the server.)

[//]: # ()
[//]: # ()
[//]: # (The assignment also exposes us to multithreading, way to achieve concurrency in a process. It also encourages student to think about where the race condition)

[//]: # (could occur in our code &#40;in our case, the key-value store which is a shared memory in the same process&#41; and how we can explicitly address the race condition. For)

[//]: # (example with a mutex lock, or using the async keyword in Java. It also allow use to explore increase concurrency of our code using multithreading mechanisms such as)

[//]: # (a threadpool that allows reuse of threads.)

[//]: # ()
[//]: # (## Technical Impression)

[//]: # (There are a few challenges that I faced. I will elaborate on each with more details)

[//]: # (1. Understanding the concept of RPC and how it is implemented with Java RMI.)

[//]: # (2. Think about how to architect the code to enable RPC protocol while maintain its support for the TCP and UDP protocols.)

[//]: # (3. How to use threading to achieve concurrency and where the race condition could arise and in the code and how to address it.)

[//]: # ()
[//]: # (For point number 1. Figuring out Interface Definition Language in RMI is an extension of Remote inference took me a while. Although we do not need to explicitly)

[//]: # (specify any network protocol for communication in RPC/RMI, we still need to create a registry on a port number and bind a remote object to the registry. Then, our client)

[//]: # (code can figure out where to send the request to after looking up the object in the registry.)

[//]: # ()
[//]: # (For point number 2. I have to think about where do add the RPC code and where to modify the existing to support backward compatibility with TCP and UDP. I extended)

[//]: # (my KeyvalueStore as a Remote interface and implemented my RPCHandler as a extension of the HandlerAbstract. Luckily, the extension worked without me modifying much of the)

[//]: # (UPD and TCP code. I spent some error in project phase 1 to think about the architecture that support extension. I am glad that effort paid off. As a result, when we run)

[//]: # (a client, we can choose it to be either TCP, UDP, or RPC based, it all should work.)

[//]: # ()
[//]: # (For point number 3. The RMI is already multithreaded by default, however, we still need to add asynchronous keyword to the method in the IDF &#40;KeyValueStoreImpl class&#41; to prevent)

[//]: # (race condition on the hashmap. I also added a multiple thread to the ServerApp.java to have TCP, UDP, and RPC to run on separate threads. This way, we can have multiple clients types &#40;)

[//]: # (TCP, UDP, RPC&#41; to connect to the server concurrently and write to/read form it safely. Also RPC can handle multiple RPC client threads.)

[//]: # ()
[//]: # (In summary, through the project I gained much better understanding on RPC/RMI. Improved my ability to achitect code that is extensible. I also acquired pratical knowledge on multi-threading)

[//]: # (and multi-processing environment.)

