## Main code for project phase 2
```
RPCHandler.java
ServerApp.java # where multiple server threads are created
RPCClient.java
keyValueStore.java # where interface is deinfed
KeyValueStoreImpl.java
```

## How to run the project

System Design Diagram
![System Diagram](distributed_kvStore_design-FlowChart.drawio.png)
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

## Technical Impression

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

