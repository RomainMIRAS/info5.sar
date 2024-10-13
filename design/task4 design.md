# Design Template for Full Communication Layer based on Event

## Event Gestion

### EventPump

EventPump is a singleThread class that will execute a set of Event when needed.
This method will be design as a Singleton class to have only one instance of this class. 
As a basic EventPump implementation, this pump will have a set of Event to execute by a loop.

The class assure that the execution of the Event will happens in FIFO Order for the Event application.

An executed event will be automatically removed from the pump at end.

As the Class extends Thread, This class can be start at anytime.

### Task

Task are the link between the code and the EventPump. It represent and set a method to execute.

On post, a task will created an "Event" to keep the context of it and make it simplier to debug.

Runnable post can have many specialization of this class to respond to a specific Event such as Accepting or Connecting Event for example.
Each specialization will have there own set of data needed to execute the Runnable.

### Event 

Event are just Runnable that have the current task that have posted them and also the task that post there task.
It just a Runnable with context.

## BrokerManager

We will reused the system of BrokerManager.

## QueueBroker/Broker Accept/Connect Event Process Design

Both Accepting/Connecting process will have their own specialization of the Runnable and will be fully executed as an Event.
QueueBroker and Broker will have the same 

### AcceptingTask

A QueueBroker will have a map of Port/Task that correspond to the list of AcceptingTask that is linked to the pump.

At the binding method start, this method will check is there is already an AcceptingTask on this port and if not, it will post an AcceptingTask an so the acceptingProcess as fully start. In other case, return false. 

Each time the pump execute this event, we will check if anypeople is trying to connect to us, if yes then connect to each other by the Listener. If no one is trying to connect, this task will be repost in the pump to keep accepting.

### ConnectingTask

**`boolean connect(String name, int port, ConnectListener listener);`** : Will check if the name of the QueueBroker/Broker exist and return false if not. Other else, created/post a ConnectingTask.

This task will try to connect to the Broker and will refused() by the Listener if there is a problem. Otherwise will return the Channel by the listener

## Channel Write/Read Process Design

Each Channel will have there own Task for Reading or Writing.

## Writing

On method write, we will post a Wrinting Event on the Writing Task. This Event will check if the buffer of the channel is full, if not we will put as much as byte he can on the buffer and then notify the number of bytes sent by the listener.
If the buffer is full, we will repost this event on the same task as we his post. This will ensure the FIFO method.

## Reading 

**First option** : Each Channel will have a read event in the pump to read in case of byte in the channel 

**Second option** : After writing, post on a read event for the remote channel.

## MessageQueue Receive/Send Process Design

To create a **full-duplex** communication, we utilized two **MessageQueue** base on a Channel for each of them. This allows **simultaneous** transmission and reception of **messages**, ensuring **effimessagescient** communication between the connected **Brokers**.

As a message is send and receive as a one, both method as to ensure this process.

MessageQueue will have his own Listener used to used channel 

## SendingTask

Set of Message to send in the class.

On sending, post an event that will send by writing on the channel, with the listener if not all is send, repost then.
When message send, if there is pennding message left -> post again.

## Receiving

Idem choice as reading  

## Closing System

The closing system will be based on the Channel Linked to the MessageQueue, the closed method while return the disconnected value of the value.