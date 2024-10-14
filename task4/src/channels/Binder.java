package channels;

import event.Task;
import ichannels.IBroker.AcceptListener;
import ichannels.IBroker.ConnectListener;

public class Binder {
	private Task task;
	private AcceptRunnable acceptRunnable;
	Channel acceptChannel;
	Channel connectChannel;	
	boolean alreadyAccepted;
	AcceptListener listener;
	
	public Binder(int port, AcceptListener listener) {
		task = new Task("Accept Task on " + port);
		alreadyAccepted = false;
		this.listener = listener;
		
		acceptRunnable = new AcceptRunnable(this);
	}
	
	public void bind() {
		task.post(acceptRunnable);
	}
	
	public void kill() {
		acceptChannel.disconnect();
		connectChannel.disconnect();
		task.kill();
	}
	
	private void createChannel() {
        acceptChannel = new Channel();
        connectChannel = new Channel();
        acceptChannel.connect(connectChannel);
        connectChannel.connect(acceptChannel);
    }
	
	void _acceptConnection(ConnectListener listener) {
		if (alreadyAccepted) {
			listener.refused();
		} else {
			alreadyAccepted = true;
			createChannel();
			listener.connected(connectChannel);
		}
	} 
	
	void acceptConnection() {
		this.listener.accepted(acceptChannel);
	}
	
	
}
