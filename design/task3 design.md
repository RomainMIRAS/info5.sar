# Design Template for Message Layer based on Mixing Event/Thread

## Event Gestion

### EventPump

EventPump is a singleThread class that will execute a set of Event when needed.
This method will be design as a Singleton class to have only one instance of this class. 
As a basic EventPump implementation, this pump will have a set of TaskEvent to execute by a loop.

The class assure that the execution of the Event will happens in FIFO Order and by the fact that we implement a mixed 
Thread/Event application, this application prevent TaskEvent to be blocking the Pump by adding a TimeOut in case of any 
block of the pump.

A task will be automatically removed from the pump if the run at end. In other case, the task will be put at the end of the list and will be resume latter. ( **Can be changed** ) 

As the Class extends Thread, This class can be start at anytime.

### TaskEvent

TaskEvent are the link between the code and the EventPump. It represent and set a method to execute by an Event.
The "TaskEvent" is just the generalization of the Event **but** many specialization of this class will be created
to respond to a specific Event such as Accepting or Connecting Event for example.

Each specialization will have there own unique Runnable in there class and there own set of data needed to execute the Runnable.
Morover, there post method will be change to be private and had an other one to post there own Runnable like : 

**`boolean postSpecialization();`** : This method will use the post Method give by there mother class.


## QueueBrokerManager

As we already saw with Broker, we will used a Singleton class that will be used to store the map of all Name/QueueBroker created.

## QueueBroker Accept/Connect Process Design

Both Accepting/Connecting process will have their own specialization of the process and will be fully executed as an EventProcess.

### AcceptingTaskEvent

A QueueBroker will have a map of Port/AcceptingTaskEvent that correspond to the list of AcceptingTask that is put in the pump.

At the binding method start, this method will check is there is already an AcceptingTaskEvent on this port and if not, it will post an AcceptingTaskEvent an so the acceptingProcess as fully start. In other case, return false. 

Each time the pump execute this task, we will check if anypeople is tryign to connect to us, if yes then connect to each other by the Listener. If no one is trying to connect, this task will be repost in the pump to keep accepting.

### ConnectingTaskEvent

**`boolean connect(String name, int port, ConnectListener listener);`** : Will check if the name of the QueueBroker exist and return false if not. Other else, created/post a ConnectingTaskEvent.

This task will try to connect to the Broker and will refused() by the Listener if there is a problem. Otherwise will return the Channel by the listener

## MessageQueue Receive/Send Process Design

To create a **full-duplex** communication, we utilized two **MessageQueue** base on a Channel for each of them. This allows **simultaneous** transmission and reception of **messages**, ensuring **effimessagescient** communication between the connected **Brokers**.

As a message is send and receive as a one, both method as to ensure this process.

## SendingTaskEvent

A task will be created by the method send if the precondition of good.

This Task will try to send the method by using blocking channel method.
To ensure that the pump will not block by using those method will running, we will use the timeout to prevent this. If this class will aliase a Message by taking the ownership and will send when he can though the channel.

## Receiving

Will be trigger on the Listener.
Maybe using a New task.
No found yet.

## Closing System

The closing system will be based on the Channel Linked to the MessageQueue, the closed method while return the disconnected value of the value.