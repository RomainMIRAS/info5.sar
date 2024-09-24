package channels;

import java.util.concurrent.Semaphore;


/*
 * Symmetrical rendez-vous: the first operation waits for the second one. Both accept and connect operations are therefore blocking calls, blocking until the rendez-vous happens, both returning a fully connected and usable full-duplex channel.
 */
public class RendezVous {
    private Channel connectChannel;
    private Channel acceptChannel;
    private final Semaphore connectSemaphore;
    private final Semaphore acceptSemaphore;
    private final int port;

    public RendezVous(int port) {
        this.port = port;
        CircularBuffer bufferIn = new CircularBuffer(1024);
        CircularBuffer bufferOut = new CircularBuffer(1024);
        this.connectChannel = new Channel(bufferIn, bufferOut);
        this.acceptChannel = new Channel(bufferOut, bufferIn);

        this.connectSemaphore = new Semaphore(0);
        this.acceptSemaphore = new Semaphore(0);
    }

    /*
     * Initiates a connection to the specified Broker at the given port, ensuring thread safety during the process and updating the internal state upon success. Returns a Channel object representing the connection.
     */
    public Channel connecting() {
        acceptSemaphore.release(); // Signal that a connection is ready

        try {
            connectSemaphore.acquire(); // Wait for accept to be ready
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        return connectChannel;
    }

    /*
     * Listens for incoming connections on the specified port, blocking until a request is received. It synchronizes the connection handling to prevent thread interference and manages pending connections, returning a Channel object for the accepted connection.
     */
    public Channel accepting() {
        connectSemaphore.release(); // Signal that accept is ready

        try {
            acceptSemaphore.acquire(); // Wait for connection to be ready
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        return acceptChannel;
    }

	public int getPort() {
		return this.port;
	}
}
