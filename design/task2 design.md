# Design Template for Message Layer based on Thread

## Task Updated

As a Task is a Thread, task will **updated** to be catch at every moment as a Thread. This will allow user to get the current Broker used on a Task or the current QueueBroker.

## QueueBroker Accept/Connect Process Design

A queue Broker is using one Broker to do all the Accept/Connect Process as it already been created in the Broker.
The QueueBroker will only use the Channel created by the Broker method to create the MessageQueue as constructor will be at least :

`QueueBroker(Channel c)`

## MessageQueue Receive/Send Process Design

To create a **full-duplex** communication, we utilized two **MessageQueue** base on a Channel for each of them. This allows **simultaneous** transmission and reception of **messages**, ensuring **effimessagescient** communication between the connected **Brokers**.

As a message is send and receive as a one, both method as to ensure this process.

## Sending

- At the beginning of the sending message process, we will send the size of the message to make sure that the receiver know how many bytes he has to read
- We will then loop while the message is not fully send through the Channel, this process while block until the receiver decide to receive the message to clear the channel.
- We can maybe think to send a special ending character to inform the receiver that the message has been fully send

## Receiving

- At the beginning of the sending message process, we will read the size of the oldest message to read to make know how many bytes we have to read, this message can block if no message is send in this MessageQueue.
- We will then loop until the message has been fully receive and construct.

## Closing System

The closing system will be based on the Channel Linked to the MessageQueue, the closed method while return the disconnected value of the value.



