package channels;

import java.util.HashMap;
import java.util.Map;

import event.Task;
import ichannels.IBroker;

public class Broker implements IBroker {

	private String name;
	private BrokerManager manager;
	private Map<Integer, Binder> binders;

	public Broker(String string) {
		this.name = string;
		this.manager = BrokerManager.getInstance();
		this.binders = new HashMap<Integer, Binder>();
		manager.registerBroker(this);
	}

	@Override
	public boolean unbind(int port) {
		Binder binder = binders.get(port);
		if (binders == null) {
			return false;
		}
		binder.kill();
		binders.remove(port);
		return true;
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
		Binder binder = binders.get(port);
		if (binder != null) {
			return false;
		}

		binder = new Binder(port, listener);
		binders.put(port, binder);
		binder.bind();
		return true;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
		Broker remoteBroker = manager.getBroker(name);
		if (remoteBroker == null) {
			return false;
		}

		Task task = new Task("Connect Task " + this.name + " " + name + " " + port);
		task.post(new ConnectRunnable(port, listener, remoteBroker));
		return true;
	}

	void _connect(int port, ConnectListener listener) {
		Binder binder = binders.get(port);
		if (binder == null) {
			listener.refused();
			return;
		}
		binder._acceptConnection(listener);
	}

	@Override
	public String name() {
		return name;
	}

}
