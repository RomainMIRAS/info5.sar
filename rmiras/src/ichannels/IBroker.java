package ichannels;

public interface IBroker {
	interface AcceptListener {
		void accepted(IChannel queue);
	}

	interface ConnectListener {
		void connected(IChannel queue);

		void refused();
	}

	boolean unbind(int port);
	boolean bind(int port, AcceptListener listener);
	boolean connect(String name, int port, ConnectListener listener);
	String name();
}
