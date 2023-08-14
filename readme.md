# Real-Time Chatroom

----
### Description:  
A real-time chatroom  where users can send messages, 
and receive messages from other users in real-time. 
The communication between the client and server is 
facilitated using SSE and Kafka messaging.  

***For remote access, replace the [localhost]:80/chat with the [IPv4] address of machine run the application***


### Environment Setup (for Windows):
- *Kafka*  
Installation and Quickstart link to [Kafka](https://kafka.apache.org/quickstart)    
Open terminals to start the ZooKeeper service & Kafka broker service  
a)  bin\windows\zookeeper-server-start.bat config\zookeeper.properties  
b)  bin\windows\kafka-server-start.bat config\server.properties  

Topic would be created after starting the ExampleApplication

***To connect to Kafka from a different machine, replace the [localhost]:9092 with the [IPv4] address of that machine in application.properties***


- Redis  


### Technologies Used:
1. Java
2. Spring Kafka
3. Server sent events
4. HTML
5. JavaScript 
6. Thymeleaf 
7. Redis
