package imessages;

public interface IMessageQueue {
	interface Listener {
		void received(byte[] bytes);
		void closed();
		/**
		 * Use a the notify that this message has been sent.
		 * This mean that the ownership of the message has been returned.
		 */
		void sent(Message message);
	}
	
	void setListener(Listener listener);
	boolean send(Message message);
	void close();
	boolean closed();
}
