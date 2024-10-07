package event;

import imessages.IQueueBroker.ConnectListener;
import messages.QueueBroker;

public class ConnectTaskEvent extends EventTask {

	public ConnectTaskEvent(QueueBroker broker, int port, ConnectListener listener) {
		super();
		this.myRunnable = new Runnable() {
			@Override
			public void run() {
				broker._connect(port, listener);
			}
		};
	}

	@Override
	public void post(Runnable r) {
		throw new RuntimeException("Should not be call");
	}
	
	public void postTask() {
		super.post(myRunnable);
	}
}
