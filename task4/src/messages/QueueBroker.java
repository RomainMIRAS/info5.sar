package messages;

import imessages.IQueueBroker;

public class QueueBroker implements IQueueBroker {
	
	private String name;
	
	public QueueBroker(String string) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean unbind(int port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
