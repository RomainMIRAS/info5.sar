package imessages;

import ichannels.DisconnectedException;

public interface IMessageQueue {
	void send(byte[] bytes, int offset, int length) throws DisconnectedException;
	byte[] receive();
	void close();
	boolean closed();
}
