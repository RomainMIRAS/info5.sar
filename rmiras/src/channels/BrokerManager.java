package channels;

import java.util.HashMap;

public class BrokerManager {
	private static BrokerManager instance;
	private HashMap<String, Broker> brokers;

	private BrokerManager() {
		brokers = new HashMap<>();
	}

	public static synchronized BrokerManager getInstance() {
		if (instance == null) {
			instance = new BrokerManager();
		}
		return instance;
	}

	/*
	 * Variant without lazy initialization Block is call at the beginning of the
	 * class definition by the VM static { instance = new BrokerManager(); }
	 */

	public synchronized void registerBroker(Broker broker) {
		String name = broker.name();
		if (brokers.containsKey(name)) {
			throw new IllegalStateException("Broker with name " + name + " already exists");
		}
		brokers.put(name, broker);
	}

	public synchronized Broker getBroker(String name) {
		return brokers.get(name);
	}

	public synchronized void removeBroker(String name) {
		brokers.remove(name);
	}

	public synchronized void remove(Broker broker) {
		brokers.remove(broker.name());
	}
}
