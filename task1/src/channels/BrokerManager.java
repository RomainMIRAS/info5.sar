package channels;

import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
    private static BrokerManager instance;
    private ConcurrentHashMap<String, Broker> brokers;

    private BrokerManager() {
        brokers = new ConcurrentHashMap<>();
    }

    public static synchronized BrokerManager getInstance() {
        if (instance == null) {
            instance = new BrokerManager();
        }
        return instance;
    }

    public synchronized void registerBroker(String name, Broker broker) {
        if (brokers.containsKey(name)) {
            throw new IllegalStateException("Broker with name " + name + " already exists");
        }
        brokers.put(name, broker);
    }

    public synchronized Broker getBroker(String name) {
        return brokers.get(name);
    }
}
