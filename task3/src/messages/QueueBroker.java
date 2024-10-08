package messages;

import java.util.HashMap;

import event.AcceptTaskEvent;
import event.ConnectTaskEvent;
import imessages.IQueueBroker;

public class QueueBroker implements IQueueBroker {
	
	private String name;
	private HashMap<Integer, AcceptTaskEvent> acceptTask;
	
	
	// List de AcceptTask
	public QueueBroker(String name) {
		this.name = name;
		this.acceptTask = new HashMap<>();
		QueueBrokerManager.getInstance().registerBroker(this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public boolean unbind(int port) {
		AcceptTaskEvent a = acceptTask.get(port);
		a.kill();
		if (a.killed()) {
			acceptTask.remove(port);
			return true;
		}
		return a.killed();
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		AcceptTaskEvent a = acceptTask.get(port);
		if (a != null) {
			return false;
		}
		a = new AcceptTaskEvent(port,listener);
		acceptTask.put(port, a);
		a.postTask();
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		QueueBroker broker = (QueueBroker) QueueBrokerManager.getInstance().getBroker(name);
        if (broker == null) {
            return false;
        }
        
        ConnectTaskEvent c = new ConnectTaskEvent(broker, port, listener);
        c.postTask();
        return true;
	}

	public void _connect(int port, ConnectListener listener) {
        AcceptTaskEvent a = acceptTask.get(port);
        if (a == null || a.isConnectionAlreadyAccepted()) {
            listener.refused();
        }
        boolean connectionAlreadAccepted = a.getConnection();
		if (!connectionAlreadAccepted) {
			listener.refused();
		}
        listener.connected(a.getConnectQueue());
	}

}
