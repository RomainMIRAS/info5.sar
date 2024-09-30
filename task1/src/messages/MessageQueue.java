package messages;

import java.util.concurrent.Semaphore;

import ichannels.DisconnectedException;
import ichannels.IChannel;
import imessages.IMessageQueue;

public class MessageQueue implements IMessageQueue {
	
	IChannel channel;
	Semaphore semaphoreSend = new Semaphore(1,true);
	Semaphore semaphoreReceive = new Semaphore(1,true);
	
	public MessageQueue(IChannel channel) {
		this.channel = channel;
	}

	@Override
	public void send(byte[] bytes, int offset, int length) throws DisconnectedException {
	    semaphoreSend.acquireUninterruptibly();
		try {
			// send the length of the message
			byte[] lengthBytes = new byte[4];
			lengthBytes[0] = (byte) (length >> 24);
			lengthBytes[1] = (byte) (length >> 16);
			lengthBytes[2] = (byte) (length >> 8);
			lengthBytes[3] = (byte) length;
			int byteWrite = 0;
			while (byteWrite < 4) {
				byteWrite += channel.write(lengthBytes, byteWrite, 4 - byteWrite);
			}
			
			// send the message
			byteWrite = 0;
			while (byteWrite < length) {
				byteWrite += channel.write(bytes, offset + byteWrite, length - byteWrite);
			}		
		} catch (DisconnectedException e) {
			throw e;
		} finally {
            semaphoreSend.release();
		}
	}

	@Override
	public byte[] receive() {
		semaphoreReceive.acquireUninterruptibly();
		try {
			// read the length of the message
			byte[] lengthBytes = new byte[4];
			int byteRead = 0;
			while (byteRead < 4) {
				byteRead += channel.read(lengthBytes, byteRead, 4 - byteRead);
			}
			int length = (lengthBytes[0] << 24) | (lengthBytes[1] << 16) | (lengthBytes[2] << 8) | lengthBytes[3];

			// read the message
			byte[] bytes = new byte[length];
			byteRead = 0;
			while (byteRead < length) {
				byteRead += channel.read(bytes, byteRead, length - byteRead);
			}
			return bytes;
		} catch (DisconnectedException e) {
			return null;
		}
		finally {
			semaphoreReceive.release();
		}
	}

	@Override
	public void close() {
        channel.disconnect();
	}

	@Override
	public boolean closed() {
		return channel.disconnected();
	}

}