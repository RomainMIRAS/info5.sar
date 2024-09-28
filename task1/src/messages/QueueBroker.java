package messages;

import ichannels.IBroker;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.IQueueBroker;

public class QueueBroker implements IQueueBroker {
	
	IBroker broker;
	
	public QueueBroker(IBroker broker) {
		this.broker = broker;
	}

	@Override
	public String name() {
		return broker.getName();
	}

	@Override
	public synchronized IMessageQueue accept(int port) {
		IChannel channel = broker.accept(port);
		return new MessageQueue(channel);
	}

	@Override
	public synchronized IMessageQueue connect(String name, int port) {
		IChannel channel = broker.connect(name, port);
        return new MessageQueue(channel);
	}

}
