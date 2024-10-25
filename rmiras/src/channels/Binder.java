package channels;

import event.Task;
import ichannels.IBroker.AcceptListener;
import ichannels.IBroker.ConnectListener;
import ichannels.IChannel;

public class Binder {
	Task task;
	AcceptRunnable acceptRunnable;
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
		IChannel.DisconnectListener disconnectListener = new IChannel.DisconnectListener() {
			@Override
			public void disconnected() {
				// Do nothing
			}
		};

		acceptChannel.disconnect(disconnectListener);
		connectChannel.disconnect(disconnectListener);
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
