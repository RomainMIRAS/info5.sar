package channels;

import java.util.concurrent.ConcurrentHashMap;

import ichannels.IBroker;
import ichannels.IChannel;

public class Broker implements IBroker {
	private final String name;
	private final ConcurrentHashMap<Integer, RendezVous> acceptRendezVous;
	private final ConcurrentHashMap<Integer, RendezVous> pendingConnectRendezVous;
	
	public Broker(String name) {
		this.name = name;
		this.acceptRendezVous = new ConcurrentHashMap<>();
		this.pendingConnectRendezVous = new ConcurrentHashMap<>();
		BrokerManager.getInstance().registerBroker(name, this);
	}

	@Override
	public synchronized IChannel connect(String remoteBrokerName, int port) {
		Broker remoteBroker = BrokerManager.getInstance().getBroker(remoteBrokerName);
		if (remoteBroker == null) {
			return null; // Remote broker doesn't exist
		}
		RendezVous rendezVous = remoteBroker.getAcceptRendezVous(port);
		if (rendezVous == null) {
			rendezVous = new RendezVous(port);
			remoteBroker.addWaitingConnection(rendezVous);
		} 
		return rendezVous.connecting();
	}

	@Override
	public synchronized IChannel accept(int port) throws IllegalStateException {
		//check if the acceptRendezVous is already set
		if (acceptRendezVous.containsKey(port)) {
			throw new IllegalStateException("Already an listing on this port");
		}
		RendezVous rendezVous;

		if (pendingConnectRendezVous.containsKey(port)) {
			rendezVous = pendingConnectRendezVous.get(port);
		} else {
			rendezVous = new RendezVous(port);
			acceptRendezVous.put(port, rendezVous);
		}
		
		IChannel channel = rendezVous.accepting();
		acceptRendezVous.remove(port);
		return channel;
	}

	private void addWaitingConnection(RendezVous rendezVous) {
		pendingConnectRendezVous.put(rendezVous.getPort(), rendezVous);
	}

	private RendezVous getAcceptRendezVous(int port) {
		return acceptRendezVous.get(port);
	}

	public String getName() {
		return name;
	}
}
