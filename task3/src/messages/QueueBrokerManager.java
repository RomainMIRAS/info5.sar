package messages;

import java.util.HashMap;

import imessages.IQueueBroker;

public class QueueBrokerManager {
    private static QueueBrokerManager instance;
    private HashMap<String, IQueueBroker> brokers;

    private QueueBrokerManager() {
        brokers = new HashMap<>();
    }

    public static synchronized QueueBrokerManager getInstance() {
        if (instance == null) {
            instance = new QueueBrokerManager();
        }
        return instance;
    }

    /* Variant without lazy initialization
    Block is call at the beginning of the class definition by the VM
     static {
        instance = new BrokerManager();
    }
    */

    public synchronized void registerBroker(IQueueBroker broker) {
    	String name = broker.name();
        if (brokers.containsKey(name)) {
            throw new IllegalStateException("Broker with name " + name + " already exists");
        }
        brokers.put(name, broker);
    }

    public synchronized IQueueBroker getBroker(String name) {
        return brokers.get(name);
    }

    public synchronized void removeBroker(String name) {
        brokers.remove(name);
    }
    
    public synchronized void remove(IQueueBroker broker) {
        brokers.remove(broker.name());	
    }
}
