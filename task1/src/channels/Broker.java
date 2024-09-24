package channels;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import ichannels.IBroker;
import ichannels.IChannel;

public class Broker implements IBroker {
	private final String name;
	private final ConcurrentHashMap<Integer, BlockingQueue<RendezVous>> rendezvousQueue;

	public Broker(String name) {
		this.name = name;
		this.rendezvousQueue = new ConcurrentHashMap<>();
		BrokerManager.getInstance().registerBroker(name, this);
	}

	@Override
	public synchronized IChannel connect(String remoteBrokerName, int port) {
		Broker remoteBroker = BrokerManager.getInstance().getBroker(remoteBrokerName);
		if (remoteBroker == null) {
			return null; // Remote broker doesn't exist
		}

		// Check if there's a matching rendezvous on the remote broker
		RendezVous matchingRendezVous = remoteBroker.findMatchingRendezVous(port, RendezVous.RendezVousState.CONNECT);
		if (matchingRendezVous != null) {
			return matchingRendezVous.connect(this);
		}

		// If no matching rendezvous, create a new one
		rendezvousQueue.putIfAbsent(port, new LinkedBlockingQueue<>());
		BlockingQueue<RendezVous> queue = rendezvousQueue.get(port);
		RendezVous rendezVous = new RendezVous(port, this, RendezVous.RendezVousState.CONNECT);
		queue.offer(rendezVous);
		IChannel channel = rendezVous.connect(remoteBroker);
		queue.remove(rendezVous);
		return channel;
	}

	@Override
	public synchronized IChannel accept(int port) throws IllegalStateException {
		// Check if there's a matching rendezvous on this broker
		RendezVous matchingRendezVous = findMatchingRendezVous(port, RendezVous.RendezVousState.ACCEPT);
		if (matchingRendezVous != null) {
			IChannel channel = matchingRendezVous.accept();
			rendezvousQueue.get(port).remove(matchingRendezVous);
			return channel;
		} else {
			// If no matching rendezvous, create a new one
			rendezvousQueue.putIfAbsent(port, new LinkedBlockingQueue<>());
			BlockingQueue<RendezVous> queue = rendezvousQueue.get(port);
			RendezVous rendezVous = new RendezVous(port, this, RendezVous.RendezVousState.ACCEPT);
			queue.offer(rendezVous);
			IChannel channel = rendezVous.accept();
			rendezvousQueue.get(port).remove(rendezVous);
			return channel;
		}
	}

	// New method to find a matching rendezvous
	private RendezVous findMatchingRendezVous(int port, RendezVous.RendezVousState state) {
		BlockingQueue<RendezVous> queue = rendezvousQueue.get(port);
		if (queue != null && !queue.isEmpty()) {
			for (RendezVous rendezVous : queue) {
				if (rendezVous.getState() == state) {
					return rendezVous;
				}
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}
}
