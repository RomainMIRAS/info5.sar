package channels;

import java.util.HashMap;

import ichannels.IBroker;
import ichannels.IChannel;

public class Broker implements IBroker {
	private final String name;
	private final HashMap<Integer, RendezVous> acceptRendezVous;
	
	public Broker(String name) {
		this.name = name;
		this.acceptRendezVous = new HashMap<>();
		BrokerManager.getInstance().registerBroker(this);
	}
	
	@Override
	public synchronized IChannel connect(String remoteBrokerName, int port) {
		Broker remoteBroker = BrokerManager.getInstance().getBroker(remoteBrokerName);
		if (remoteBroker == null) {
			return null; // Remote broker doesn't exist
		}
		return remoteBroker._connect(this, port);
	}

	private IChannel _connect(Broker broker, int port) {
		RendezVous rendezVous = null;
		synchronized (acceptRendezVous) {
            rendezVous = acceptRendezVous.get(port);
			while (rendezVous == null) {
				try {
					acceptRendezVous.wait();
					rendezVous = acceptRendezVous.get(port);
				} catch (InterruptedException e) {
					// Do nothing
				}
				rendezVous = acceptRendezVous.get(port);
			}
			acceptRendezVous.remove(port);
        }
		return rendezVous.connect(broker,port);
	}

	@Override
	public synchronized IChannel accept(int port) throws IllegalStateException {
		RendezVous rendezVous = null;
		synchronized (acceptRendezVous) {
			rendezVous = acceptRendezVous.get(port);
			if (rendezVous != null) {
				throw new IllegalStateException("Already accepting on port " + port);
			}
			rendezVous = new RendezVous();
			acceptRendezVous.put(port, rendezVous);
			acceptRendezVous.notifyAll();
		}
		IChannel ch;
		ch = rendezVous.accept(this, port);
		return ch;
	}

	public String getName() {
		return name;
	}
}
