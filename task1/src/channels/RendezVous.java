package channels;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
   

/*
 * Symmetrical rendez-vous: the first operation waits for the second one. Both accept and connect operations are therefore blocking calls, blocking until the rendez-vous happens, both returning a fully connected and usable full-duplex channel.
 */
public class RendezVous {
    private Broker connectBroker;
    private Broker acceptBroker;
    private int port;
    private Channel connectChannel;
    private Channel acceptChannel;
    private CircularBuffer bufferIn;
    private CircularBuffer bufferOut;
    public final static enum RendezVousState {CONNECT, ACCEPT};
    private RendezVousState state;
    
    public RendezVous(int port, Broker broker, RendezVousState state) {
        this.port = port;
        this.connectBroker = null;
        this.acceptBroker = null;
        if(state == RendezVousState.CONNECT){
            this.connectBroker = broker;
        }
        else{
            this.acceptBroker = broker;
        }
        this.connectChannel = null;
        this.acceptChannel = null;
        this.state = state;
    }

    /*
     * Initiates a connection to the specified Broker at the given port, ensuring thread safety during the process and updating the internal state upon success. Returns a Channel object representing the connection.
     */
    public synchronized Channel connect(Broker brokerToConnect) {
        this.connectBroker = brokerToConnect;

        notifyAll();

        while(acceptChannel == null){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        return connectChannel;
    }

    /*
     * Listens for incoming connections on the specified port, blocking until a request is received. It synchronizes the connection handling to prevent thread interference and manages pending connections, returning a Channel object for the accepted connection.
     */
    public synchronized Channel accept(){
        while(connectBroker == null){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        this.bufferIn = new CircularBuffer(1024);
        this.bufferOut = new CircularBuffer(1024);
        this.connectChannel = new Channel(bufferIn, bufferOut);
        this.acceptChannel = new Channel(bufferOut, bufferIn);

        notifyAll();

        return acceptChannel;
    }

    public Broker getAcceptBroker(){
        return this.acceptBroker;
    }

    public Broker getConnectBroker(){
        return this.connectBroker;
    }
}
