package ichannels;

public interface IMessageQueue {
	void send(byte[] bytes, int offset, int length);
	byte[] receive();
	void close();
	boolean closed();
}
