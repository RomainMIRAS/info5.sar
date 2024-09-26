# Design Template for Communication Layer

## Recuperation of a broker

To get a Broker link to a given Task in a Thread, the class Task will extend from Thread to link 

## Broker Accept/Connect Process Design

Due to the specification that share that the accept/connect are thread safe, those method will be syncronized with proper utilization.

In order to connect to an other Broker, a Broker have to know the other that he want to connect to and find if he exist.
Then a singleton class "BrokerManager" will be used to store the map of all Name/Broker created.
This also mean that we allow only on universe of Broker. The BrokerManager will be Thread Safe as well as the Broker.

After finding a Broker with the Manager, a Broker have to invock a method in the wanted Broker :

`public void connectToBroker(Broker broker, int port);`

We will create a class RendezVous that will manage the connection process between Brokers. This class will handle the synchronization of the accept and connect methods to ensure thread safety. It will also maintain a queue of pending connections and facilitate the rendezvous between Brokers. The class will include methods such as:

We can have multiple RendezVous created by the connected but we can't have multiple RendezVous accepting in the same port.

This design will ensure that the connection process is managed efficiently and safely across multiple threads.

RendezVous will need those fields : 
- Two broker, one that accept and one connecting
- the working port

### Method Descriptions for RendezVous Class

- `public Channel connect(Broker broker, int port)` : Initiates a connection to the specified Broker at the given port, ensuring thread safety during the process and updating the internal state upon success. Returns a Channel object representing the connection.

- `public syncronized Channel accept(int port)` : Listens for incoming connections on the specified port, blocking until a request is received. It synchronizes the connection handling to prevent thread interference and manages pending connections, returning a Channel object for the accepted connection.

## Channel Read/Writing Process Design

To create a **full-duplex** communication, we utilized two **Channels**: one for **sending** data and another for **receiving** data. This allows **simultaneous** transmission and reception of **messages**, ensuring **efficient** communication between the connected **Brokers**.

### Circular Buffer

**Circular Buffer** is the class used to **push/pull** bytes in the **channel**. Each **Channel** has the same two **CircularBuffer** objects used in **in/out** mode. Those two **buffers** are then **aliased** in our two **Channel**.

## Channel Disconnected Process Design

- Either end can initiate disconnection at any time
- Implement a `disconnect()` method in the Channel class

- After `disconnect()` is called locally:
  - Set a local flag indicating disconnection
  - Throw DisconnectedException for new read/write attempts
  - `disconnected()` method calls to check status

- When remote side disconnects:
  - Allow reading of in-transit bytes
  - Silently drop new local writes
  - Set disconnected state only after all in-transit bytes are read

- Interrupt blocked read/write operations on disconnection
- Throw DisconnectedException for interrupted operations
- Implement thread-safe disconnect mechanism
- Release resources (e.g., buffers) when fully disconnected

