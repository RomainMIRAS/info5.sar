# Task 4: specification (full events)

The aim of Task4 is to develop a full-duplex communication framework using **event-driven programming**. Unlike thread-based systems, this design is operating **non-blockingly**. Responses and operations are driven by **events**, ensuring that no method blocks execution, and responses are handled via **listeners**.

The core components include:

- **Broker**/**QueueBroker** for establishing and managing communication channels.
- **Channel**/**MessageQueue** for exchanging messages between tasks.
- **Listeners** to handle events triggered by message exchange or connection.

## Event system

### Task

A **Task** represents a set of event that will be executed asynchronously. Tasks use the event system to post work (runnable).

##### Methods

```void post(Runnable r)```: Posts a runnable event for execution. The task will be executed using the event system.

```static Task task()```: Returns the current executing task.

```void kill()```: Terminates the current task. Every posted event by the task will be canceled, and new runnables cannot be posted after the task is killed.


```boolean killed()```: Returns true if the task has been terminated, false otherwise.

## Connecting / Accepting

### Broker

The **Broker** is responsible for managing connections between tasks. It listens for incoming connection requests and facilitates the exchange of Channels that enable communication between tasks. The Broker operates asynchronously using events to handle connections.

##### Methods

```boolean bind(int port, Broker.AcceptListener listener)```:
Binds the Broker to a specific port, enabling it to listen for incoming connections. When a connection is accepted, the provided AcceptListener is notified, and a new event is posted to manage the connection. Returns true if the port is successfully bound, false otherwise.

```boolean unbind(int port)```:
Stops listening on the specified port and prevents any further connection attempts. Returns true if unbound successfully, false otherwise.

```connect(String name, int port, Broker.ConnectListener listener)```:
Attempts to connect to another task using broker's name and port. The ConnectListener handles connection success or refusal via events. Returns false if the connection attempt is already initiated, true otherwise.

##### AcceptListener

The AcceptListener is responsible for handling incoming connection requests.

`void accepted(Channel channel)`: Called when a new connection is successfully established. The shared channel is passed in parameters. 

##### ConnectListener

The ConnectListener handles outgoing connection attempts and their results.

`void connected(Channel channel)`: Called when a connection has been accepted. The shared channel is passed in parameters. 

`void refused()`: Called when a connection is refused.

### QueueBroker

The **QueueBroker** extends the functionality of the lower Broker layer, providing a higher level of abstraction for inter-task communication. In addition to managing connections, it automatically creates a MessageQueue instance upon successful connection establishment.

##### Methods

The QueueBroker maintains the same method signatures as the Broker class, but it utilizes listeners that return MessageQueue instances.

##### AcceptListener

The AcceptListener is responsible for handling incoming connection requests.

`void accepted(MessageQueue mq)`: Called when a new connection is successfully established. The shared message queue is passed in parameters. 

##### ConnectListener

The ConnectListener handles outgoing connection attempts and their results.

`void connected(MessageQueue mq)`: Called when a connection has been accepted. The shared message queue is passed in parameters. 

`void refused()`: Called when a connection is refused.

## Reading / Writing using Channel

### Channel

The **Channel** class represents a bidirectional communication channel. It is designed to ensure that data is transmitted in a FIFO order and it is lossless.

The writing process has multiple issues such has the definition of the ownership of the data sent. We assume that this implementation give the ownership of the byte array to the application when the user send it. No copy will be done and the user will be notify by the listener when the byte array is wrote to bring it back to him.

##### Methods

`boolean write(byte[] bytes, int offset, int length, WriteListener listener)`: Attempts to write a specified range of bytes from the provided byte array to the channel. It returns true if the write operation is initiated successfully, and false if it fails.

`int read(byte[] bytes, int offset, int length)`: Read a specified range of bytes from the channel. It returns the number of bytes read.

`void disconnect(DisconnectListener listener)`: Initiates the process of disconnecting the channel. This method triggers a background task that marks the channel as disconnected and notifies the associated listener about the disconnection event.

`boolean disconnected()`: Returns true if the channel is currently disconnected, and false otherwise.

##### ReadListener

`void available()`: Triggered when there is data available to read from the Channel.

##### WriteListener

`void written(int bytesWritten)`: Triggered when a specified number of bytes have been successfully written on the Channel.

##### DisconnectListener

`void disconnected()`: Triggered when the channel is disconnected.

## Sending / Receiving using MessageQueue

### Message

The **Message** class encapsulates the data that will be sent through the MessageQueue. It represents a set of bytes and can specify an optional offset and length for sending only part of the byte array.

##### Constructor

```Message(byte[] bytes)```: Initializes a Message using the entire byte array.

```Message(byte[] bytes, int offset, int length)```: Initializes a Message using only part of the byte array, starting at the specified offset and continuing for the specified length.


### MessageQueue

A **MessageQueue** provides a higher level of abstraction for communication. It is either open or closed. It handles the sending and receiving of messages, where each message is sent or received as a whole. A task can send a message by providing a range of bytes from a byte array, and a task can receive a message as a byte array. Once closed, no further send or receive operations are possible.

The sending process has multiple issues such has the definition of the ownership of the data sent. We assume that this implementation give the ownership of the Message to the application when the user send a message. No copy will be done and the user will be notify by the listener when the message is sent to bring it back to him.

##### Constructor
```MessageQueue(Channel channel)```: Creates a new MessageQueue by wrapping an existing Channel. The Channel provides the low-level communication link for message transmission. The MessageQueue will use the Channel to send and receive bytes.

##### Methods

`void setListener(Listener listener)`: Sets the Listener that will receive notifications when events occur in the MessageQueue.

`boolean send(Message message)`: Attempts to send a message to the MessageQueue. It returns true if the send operation is initiated successfully, and false if it fails.

`void close()`: Closes the MessageQueue, preventing any further messages from being sent or received. It also triggers a disconnect event.

`boolean closed()`: Returns true if the MessageQueue is closed and no more messages can be sent or received, false otherwise.

##### Listener

`void received(byte[] msg)`: Triggered when a message is received on the MessageQueue. The received message is passed as a byte array.

`void sent(Message msg)`: Triggered when a message has been successfully sent through the MessageQueue.

`void closed()`: Triggered when the MessageQueue is closed, signaling that no further messages can be sent or received.
