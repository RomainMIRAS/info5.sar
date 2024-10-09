package messages;

import java.util.LinkedList;
import java.util.Queue;

import event.Task;
import ichannels.DisconnectedException;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {
	
	private IChannel channel;
	private Listener listener;
	private Queue<Message> messagesToSend;
	
	private final Thread threadReceive = new Thread(() -> {
		while (!channel.disconnected()) {
			try {
				byte[] lengthBytes = new byte[4];
				int byteRead = 0;
				while (byteRead < 4) {
					byteRead += channel.read(lengthBytes, byteRead, 4 - byteRead);
				}
				int length = 0;
				for (int i = 0; i < 4; i++) {
	                length += (lengthBytes[i] & 0xFF) << (i * 8);
	             }
				
				// read the message
				byte[] bytes = new byte[length];
				byteRead = 0;
				while (byteRead < length) {
					byteRead += channel.read(bytes, byteRead, length - byteRead);
				}
				
				new Task().post(() -> listener.received(bytes));
			} catch (DisconnectedException e) {
				break;
			}
		}	
	});
	
	private final Thread threadSend = new Thread(() -> {
		while (!channel.disconnected()) {
			try {
				Message message = null;
				synchronized (messagesToSend) {
					if (!messagesToSend.isEmpty()) {
						message = messagesToSend.poll();
					}
				}

				if (message != null) {
					byte[] bytes = message.getBytes();
					int offset = 0;
					int length = bytes.length;

					// send the length of the message
					byte[] lengthBytes = new byte[4];
					for (int i = 0; i < 4; i++) {
						lengthBytes[i] = (byte) (length >> (i * 8));
					}

					int byteWrite = 0;
					while (byteWrite < 4) {
						byteWrite += channel.write(lengthBytes, byteWrite, 4 - byteWrite);
					}

					// send the message
					byteWrite = 0;
					while (byteWrite < length) {
						byteWrite += channel.write(bytes, offset + byteWrite, length - byteWrite);
					}
					
					final Message finalMessage = message;
					new Task().post(() -> listener.sent(finalMessage));
				}
			} catch (DisconnectedException e) {
				break;
			}
		}
	});
	
	public MessageQueue(IChannel channel) {
		this.channel = channel;
		this.listener = null;
		this.messagesToSend = new LinkedList<Message>();
		threadReceive.start();
		threadSend.start();
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized boolean send(Message message) {
		if (this.listener == null) {
			return false;
		} else if (channel.disconnected()) {
			return false;
		}
		synchronized (messagesToSend) {
			messagesToSend.add(message);
		}
		return true;
	}

	@Override
	public void close() {
		threadReceive.interrupt();
		channel.disconnect();
	}

	@Override
	public boolean closed() {
		return channel.disconnected();
	}

}