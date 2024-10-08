# Specification for the TP Communication layer

The communication layer is the program that allow user to create channel where you can communicate with other people through channel by sending/receiving bytes.

## Broker

Broker is the entity that will **manage the connection** between the task.
Broker is doing different roles such as accepting and sending connection request.
A Broker can perform **multiple task** at once without limit. A Broken is **identify** by his name.

### Methods

- **`Broker(String name)`**: Constructor of the Broker by specifying his **unique** name.
- **`connect(String name, int port)`**: Block until the connection to an other Broker by using his unique name and a specified port. Return the Channel to communicate on success, null otherwise.

- **`accept(int port)`**: Accepts incoming connections on the specified port.  
This method **blocks** until a connection is made and then will create a Channel on accepting. Trying to accept on the **same port** of an other Broker that have the **same name** will return null.
Return the Channel created.

## Channel

Channel is the entity where task will send and receive data through.  
Channel can be closed by any Task.    
Channel is **FIFO** and **Lossless**.  
Channel is **full-duplex**.  
Channel is **multi-threaded** but reading/writing at the same time with multiple tasks can't guarantee data consistency.

### Methods

- **`read(byte[] bytes, int offset, int length)`**: Reads data from the channel into the specified byte array starting at the given offset and up to the specified length.  
This methods **blocks** until the number of bytes (length) is read. 
Returns the number of bytes read, -1 if the connection is closed.
- **`write(byte[] bytes, int offset, int length)`**: Writes data to the channel from the specified byte array starting at the given offset and up to the specified length.  
This methods **blocks** if there is no space left to the buffer channel.
Returns the number of bytes written, -1 if the connection is closed.
- **`disconnect()`**: Closed the Channel.
- **`disconnected()`**: Checks if the channel is closed. Returns a boolean indicating the disconnection status.

## Task

A task represents a unit of work that interacts with a broker and can be executed.
Task can talk to multiple Broker but **at least one**.

### Methods

- **`Task(Broker b, Runnable r)`**: Constructor of the Task with the first Broker linked to and a Runnable executable.

- **`static Broker getBroker()`**: Get the Broker of the current Thread.

## Testing
The test is implemented through a **simple echo server**. This server accepts connections from any number of clients and echoes back anything a client sends.

A test client will loop over and over the following steps:
- Connect to the server.
- Send a sequence of bytes, representing the numbers from 1 to 255.
- Test that these bytes are echoed properly by the server.
- Disconnect.

Moreover, the test will involve **one server** and several clients.
