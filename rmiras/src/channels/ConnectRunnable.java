package channels;

import ichannels.IBroker.ConnectListener;

public class ConnectRunnable implements Runnable {

	private ConnectListener listener;
	private Broker remoteBroker;
	private int port;

	public ConnectRunnable(int port, ConnectListener listener, Broker remoteBroker) {
		this.listener = listener;
		this.port = port;
		this.remoteBroker = remoteBroker;
	}

	@Override
	public void run() {
		remoteBroker._connect(port, listener);
	}

}
