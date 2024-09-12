# Specification for the Communication Layer

## Task
A task is an independent unit of work within the system. Each task has a unique identifier.

## Communication Layer
This layer is responsible for establishing and managing communication channels between tasks. It does not assume whether tasks run within the same process or the same machine.

## Communication Channel
A communication channel is a conduit through which tasks can send and receive bytes of data. Each channel is associated with two tasks - a sender and a receiver.

## Methods
- **`createChannel()`**: This method creates a new communication channel between the specified sender and receiver tasks.
- **`connectToChannel()`**: This method connect the task to the channel to initialyze her into this.
- **`disconnectToChannel()`**: This method disconnect the task to the channel to initialyze her into this.
- **`send()`**: This method sends the specified data over the specified channel. It returns a boolean indicating whether the send operation was successful.
- **`receive()`**: This method receives data from the specified channel. It returns the received data as a byte array.

## Byte-Oriented Circular Buffers
These buffers are used to implement the communication channels. They allow for efficient, non-blocking communication between tasks. The buffer is "circular" in that when it is full, new data overwrites the oldest data in the buffer.

## Assumptions
- **Tasks run within the same process**: The design of the communication layer assumes that all tasks are running within the same process to simplifies the implementation and allows for efficient communication.
- **Use of byte-oriented circular buffers**: The design assumes the use of byte-oriented circular buffers to implement the communication channels. 

## Testing
The communication layer should be thoroughly tested to ensure its correct operation. Tests should cover all methods and consider edge cases, such as attempting to send data over a non-existent channel or receiving data from an empty buffer.
