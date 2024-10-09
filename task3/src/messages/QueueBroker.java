package messages;

import java.util.HashMap;

import channels.Broker;
import channels.BrokerManager;
import event.Task;
import ichannels.IChannel;
import imessages.IQueueBroker;

public class QueueBroker implements IQueueBroker {
	
	private HashMap<Integer, Thread> acceptThreads;
	private Broker broker;
	private String name;
	
	
	// List de AcceptTask
	public QueueBroker(String name) {
		this.broker = new Broker(name);
		this.name = name;
		this.acceptThreads = new HashMap<>();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		
		if (acceptThreads.containsKey(port)) {
            return false;
        }

        Thread acceptThread = new Thread(() -> {
            while (true) {
                try {
                    IChannel channel = broker.accept(port);
                    MessageQueue queue = new MessageQueue(channel);
                    Task task = new Task();
                    task.post(() -> listener.accepted(queue));
                } catch (Exception e) {
                    break;
                }
            }
        });
        acceptThreads.put(port, acceptThread);
        acceptThread.start();
        return true;
	}

	@Override
	public boolean unbind(int port) {
		if (!acceptThreads.containsKey(port)) {
			return false;
		}
		Thread acceptThread = acceptThreads.get(port);
		acceptThread.interrupt();
		acceptThreads.remove(port);
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		BrokerManager manager = BrokerManager.getInstance();
		Broker broker = manager.getBroker(name);
		if (broker == null) {
			return false;
		}
		new Thread(() -> {
			try {
				IChannel channel = broker.connect(name, port);
				MessageQueue queue = new MessageQueue(channel);
				Task task = new Task();
				task.post(() -> listener.connected(queue));
			} catch (Exception e) {
				listener.refused();
			}
		}).start();
		return true;
	}

}
