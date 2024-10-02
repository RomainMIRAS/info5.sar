# Overview: QueueBroker / MessageQueue Event System
The QueueBroker / MessageQueue system is a framework to send and receive messages on **event** system.
A MessageQueue is a communication class use to send messages.
Full-duplex, each end point can be used to read or write message (CF message section).
A connected MessageQueue is FIFO and lossless, see Section "Closing"
for details about disconnection.

The typical use of messageQueues is by two tasks to establish a full-duplex communication. However, there is no ownership between messageQueue and tasks, any task may read or write in any messageQueue it has the reference to. The following rules apply:

A messageQueue is either open or closed. It is created open and it becomes closed when either side requests a disconnect. There is no notion of the end of stream for a open stream. To mark the end of a stream, the corresponding messageQueue is simply closed.

# Event System

Due to the utilization of a event system instead of thread base system, **each of the method use can't block** and will be responding at some point by an event. 
Both of the class have public method have to set listener that will trigger the method at some point. Each listener are linked to the class.

# Connecting / Accepting

QueueBroker use asynchronous method to make the connection.
Both connection method use different listener created by the user to apply corresponding process to do when the event system will responde.

## Connecting

- Signature : **`boolean bind(int port, AcceptListener listener);`**
  - Use : Start listening to the current port listening on the port. The current listener pass will be call by an event to responde to him. This method return true if the method has succesfully start listening, false otherwise.

- Signature : **`boolean unbind(int port);`**
  - Use : Cancel the current listening on the port .This method return true if the method has succesfully stop listening, false otherwise.

## Accepting

- Signature : **`boolean connect(String name, int port, ConnectListener listener);`**
  - Use : This method starat trying to connect to the current port/name QueueBroker. The current listener pass will be call by an event to responde to him. This method return true if the method can succesfully start connecting, false otherwise.

# Sending / Reading

Each MessageQueue have one Channel that is used to read/write byte and so this system is only an upper layer providing the guaranty to send message.

## Message

A Message class that represent a set of byte with any longer to be send and receive on a MessageQueue. This class contains a length and the offset to apply to the given array.
When a message is send, he will be receive directly as a whole.

## Listener

A listener is used inside of the MessageQueue to notify the user when event has reposonde to him such as 

- Signature : **`void sent(Message message);`**
  - Use : notify to the user when his message has been successfuly sent. This mean that the **ownership** of the message has been returned.
- Signature : **`void close();`**
  - Use : notify to the user when the current MessageQueue is closed
- Signature : **`void received(Message message);`**
  - Use : notify to the user when he received a message. This method guarantee that the message is the oldest messages.
  
## Sending

The sending process has multiple issues such has the definition of the ownership of the data sent. We assume that this implementation give the ownership of the Message to the application when the user send a message. No copy will be done and the user will be notify by the listener when the message is sent to bring it back to him.

- Signature: **`boolean send(Message message);`**
  - Use : Start trying to send to the given Message. This method return true if the method has succesfully start trying to send, false otherwise.

So, the send method while not block and assume that the message fully be receive has a whole.
Reminder : The message which is a set of byte can be of any size.

# Closing

Closing this is using the same system as Channel disconnection.