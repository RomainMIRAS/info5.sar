package channels;

import ichannels.IChannel;


/*
 * Symmetrical rendez-vous: the first operation waits for the second one. Both accept and connect operations are therefore blocking calls, blocking until the rendez-vous happens, both returning a fully connected and usable full-duplex channel.
 */
public class RendezVous {
    private Channel connectChannel;
    private Channel acceptChannel;
    private Broker acceptBroker;
    private Broker connectBroker;
    
    private void _wait() {
		while (connectChannel == null || acceptChannel == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// Do nothing
			}
		}
    }

	public synchronized IChannel accept(Broker broker, int port) {
		this.acceptBroker = broker;
		acceptChannel = new Channel();
		if (connectChannel != null) {
			acceptChannel.connect(connectChannel,acceptBroker.getName());
			notify();
		} else {
			_wait();
		}
		return acceptChannel;
	}

	public synchronized IChannel connect(Broker broker, int port) {
		this.connectBroker = broker;
		connectChannel = new Channel();
		if (acceptChannel != null) {
			connectChannel.connect(acceptChannel, connectBroker.getName());
			notify();
		} else {
			_wait();
		}
		return connectChannel;
	}
}
