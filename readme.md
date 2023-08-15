# Real-Time Chatroom

----
### Description:  
A real-time chatroom where users can send messages, and receive messages from other users in real-time. The communication between the client and server is facilitated using SSE and Kafka messaging.

~~**Message Accept Type: Integer Only**  
Users can send integers through text box and receive the result of calculation in real-time.~~

****For remote access, replace the [localhost]:80/chat with the [IPv4] address of machine run the application***


### Environment Setup (for Windows):
Exit the environment: ***ctrl+c***
1. *Kafka*  
Installation and Quickstart link to [Kafka](https://kafka.apache.org/quickstart)  
Open **TWO** terminals to start the ZooKeeper service & Kafka broker service respectively  
i.   bin\windows\zookeeper-server-start.bat config\zookeeper.properties  
ii.  bin\windows\kafka-server-start.bat config\server.properties   
Topic would be created after starting the ExampleApplication<br />     
****To connect to Kafka from a different machine, replace the [localhost]:9092 with the [IPv4] address of that machine in application.properties*** <br /><br />

2. *Redis*  
Installation and Quickstart link to [Redis](https://redis.io/docs/getting-started/installation/install-redis-on-windows/)  
Open **Ubuntu** and running 'sudo service redis-server start' & 'redis-cli' commands.  
****Need to input password after starting the server***<br />  

*For first installation, need to set up username and password (password unseen)*

### Technologies Used:
- Java (Spring Boot)
- Spring Kafka
- Server-Sent Events (SSE)
- **Frontend:** HTML & JavaScript & Thymeleaf
- Redis
