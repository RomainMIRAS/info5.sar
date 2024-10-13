package imessages;

public interface IQueueBroker {
	String name();
	
	interface AcceptListener {
		void accepted(IMessageQueue queue);
	}
	
	interface ConnectListener {
		void connected(IMessageQueue queue);
		void refused();
	}
	
	boolean unbind(int port);
	boolean bind(int port, AcceptListener listener);
	boolean connect(String name, int port, ConnectListener listener);
}
