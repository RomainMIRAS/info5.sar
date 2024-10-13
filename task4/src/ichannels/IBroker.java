package ichannels;

public interface IBroker {
	
	boolean unbind(int port);
	boolean bind(int port, AcceptListener listener);
	boolean connect(String name, int port, ConnectListener listener);
	
	interface AcceptListener {
		void accepted(IChannel queue);
	}
	
	interface ConnectListener {
		void connected(IChannel queue);
		void refused();
	}

	/**
	 * Get the name of the broker.
	 * 
	 * @return the name of the broker
	 */
	String name();
}
