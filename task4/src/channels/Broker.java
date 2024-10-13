package channels;

import java.util.HashMap;
import java.util.Map;

import event.Task;
import ichannels.IBroker;

public class Broker implements IBroker {

	private String name;
	private BrokerManager manager;
	private Map<Integer, Task> acceptTasks;
	
	public Broker(String string) {
		this.name = string;
		this.manager = BrokerManager.getInstance();
		this.acceptTasks = new HashMap<Integer, Task>();
		manager.registerBroker(this);
	}

	@Override
	public boolean unbind(int port) {
		Task task = acceptTasks.get(port);
		if (task == null) {
			return false;
		}
		task.kill();
		acceptTasks.remove(port);
		return true;
	}

	@Override
	public boolean bind(int port, AcceptListener listener) {
//		Task task = new Task();
//		acceptTasks.put(port, task);
//		task.post(new AcceptRunnable(port, listener, this));
//		return true;
		return false;
	}

	@Override
	public boolean connect(String name, int port, ConnectListener listener) {
//		Task task = new Task();
//		task.post(new ConnectRunnable(name, port, listener, this));
//		return true;
		return false;
	}

	@Override
	public String name() {
		return name;
	}

}
