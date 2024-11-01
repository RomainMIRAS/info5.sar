# Task 4: Design (Full Events)

## Event System

### Structure of the EventPump
The EventPump is an event manager designed to execute tasks in a FIFO model, ensuring that events are processed in the order they are added. As a Singleton, EventPump ensures that only one instance of this class is active throughout the system, centralizing task management. This design allows EventPump to start execution at any time since it extends Thread.

**Key features:**
- EventPump operates in a continuous while loop to process each event in the queue.
- Events are automatically removed from the queue once executed.
- An event can be added at any time via the post() method.
- If the queue is empty, the thread enters sleep mode, enhancing performance.
- Once the loop resumes, EventPump continues to execute events in FIFO order.

### Task and Event Classes
Task and Event classes provide a management layer for Runnable objects, allowing events to retain context for easier tracking and debugging.

- **Task**: Represents a unit of work with its own lifecycle. Each task can post events to the EventPump, creating an Event to keep its execution context, making debugging simpler. Each specialization of Task (for example, AcceptingTask or ConnectingTask) has its own data to respond to a specific event.
- **Event**: A Runnable executed by the EventPump, containing references to the Task that created it and the current Task (to retain context). It acts as a link allowing the execution of specific, context-aware actions.

## Connecting / Accepting

Both Connecting / Accepting process will have their own specialization of the Runnable and will be fully executed as an Event. QueueBroker and Broker will have the same.

### Broker manager

The BrokerManager is implemented as a singleton to ensure centralized management of brokers, allowing brokers to communicate with each others and access brokers by name without risk of duplication or conflict. It uses a HashMap to store broker instances and provides methods to safely add or retrieve brokers.

### Binder Class
The Binder class manages server-side connections by binding an accepting task to a specific port. Its main role is to accept incoming connections and assign channels for communication.

**Main Components:**
- Binding and Listening: The Binder accepts connections on a specified port. When a client requests a connection, the Binder creates a Channel to manage communication.
- Channel Management: The Binder holds two Channel instances: one for the accepting side (acceptChannel) and one for the connecting side (connectChannel). These channels are linked to ensure seamless data exchange.
- Connection Logic: If a connection request is accepted, the Binder establishes communication by connecting the acceptChannel and connectChannel. It posts an AcceptRunnable task to the EventPump to handle the acceptance process.

### AcceptingTask Design
The AcceptingTask handles incoming connections for the Broker. Each port can have only one active AcceptingTask to avoid conflicts.
- When calling bind() method, if an acceptance Task already exists on the given port, the method returns false.
- Otherwise, a new AcceptingTask is added to monitor incoming connections on this port, with an attached AcceptListener.
- If an incoming connection is detected, it is accepted, and the communication channel (Channel) is set up.

### ConnectingTask Design
The ConnectingTask is responsible for connecting to a remote Broker instance on a specified port.
- The connect() method checks if the remote Broker is available. If not, it returns false.
- If a connection is possible, the ConnectingTask is posted in the EventPump to establish the connection with the remote Broker.
- The connection task notifies the ConnectListener upon success; otherwise, it calls refused() if the connection fails.

## Reading / Writing using Channel

### Writing Process
Each Channel has a WritingTask type task that handles write events on the communication channel. Write events are managed by adding WriteRunnable objects to the EventPump queue. If the output buffer is not full, the maximum possible bytes are written, and the number of bytes written is reported to the WriteListener. It also inform the remote channel that some bytes are available to read.

### Reading Process
The reading process in the Channel class is designed to efficiently manage incoming data from the communication medium. It employs a read listener that is triggered whenever there are available bytes to read. When data becomes available, the listener reads the data in chunks, processing the incoming information incrementally. This approach allows the system to handle variable-sized messages without blocking, as it can continuously check for new data and notify the relevant listeners when complete messages are received. By maintaining this responsiveness, the Channel ensures effective communication between connected components while minimizing latency.

## Sending / Receiving using MessageQueue

### MessageQueue Design for Full-Duplex Communication
The MessageQueue class is designed to facilitate the sending and receiving of messages through a communication channel. It uses two internal listeners to manage reading and writing operations: one for handling incoming messages by reading the length and content, and another for sending messages by first transmitting the length followed by the actual message. The class manages a queue of messages to ensure they are sent in order, allowing new messages to be added while one is already being sent.

### Closing and Channel Management
The closing mechanism in the MessageQueue class ensures that the connection is properly terminated when no longer needed. It utilizes a disconnect listener that is invoked when the channel is disconnected, notifying the registered listener about the closure of the connection. This approach allows the system to handle resource cleanup effectively and maintain a reliable communication flow by informing all relevant components about the connection's status, ensuring that they can respond appropriately when the channel is closed.
