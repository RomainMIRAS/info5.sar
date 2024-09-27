# Overview: QueueBroker / MessageQueue
The QueueBroker / MessageQueue system is a framework to send and receive messages using basic messageQueue and queueBroker.
A MessageQueue is a communication messageQueue, a message.
Full-duplex, each end point can be used to read or write message (CF message section).
A connected MessageQueue is FIFO and lossless, see Section "Closing"
for details about disconnection.

The typical use of messageQueues is by two tasks to establish a full-duplex communication. However, there is no ownership between messageQueue and tasks, any task may read or write in any messageQueue it has the reference to. The following rules apply:

A messageQueue is either open or closed. It is created open and it becomes closed when either side requests a disconnect. There is no notion of the end of stream for a open stream. To mark the end of a stream, the corresponding messageQueue is simply closed.

# Message

A Message is a set of byte with any longer to be send and receive on a channel.
When a message is send, he will be receive directly in one part.

# Connecting

QueueBroker is the same class as Broker on Broker instance to make the connection.
Connecting system is using the same as Broker do.
The MessageQueue return while accepting/connecting is created using the Channel return by the accept/connect by the Broker.

A QueueBroker can't connect with a Broker.

# Sending / Reading

Each MessageQueue have one Channel that is used to read/write byte and so this system is only an upper layer providing the guaranty to send message.

## Sending

Signature: send(byte[] bytes, int offset, int length);

When sending, the given byte array contains the bytes of a message send in the channel.

The method "send" returns the void and a message is always either fully receive/sending or not sending at all making a Disconnect Exception in this case.
So, the send method while block until the message is fully send through.
Reminder : The message which is a set of byte can be of any size.

## Receiving

Signature: byte[] receive();

The method "receive" will return the oldest message send as a set of bytes.
As indeed the message received is not split and is receive as one full.

The receive will block while no message is send though the MessageQueue, it will until a the moment.

The end of stream is the same as being as the messageQueue being disconnected, so the method will throw an exception (DisconnectedException). 

In case of MessageQueue closed, this class allow to receive message if the was some left in the MessageQueue

Note: notice that the disconnected exception does not always indicate an error, rarely in fact. The end of stream is an exceptional situation, but it is not an error. Remember that exceptions are not only for errors, but for exceptional situations, hence their name.
The disconnected exception may give some extra information regarding an error if an internal error caused the messageQueue to disconnect.   

# Closing

Closing this is using the same system as Channel disconnection.





