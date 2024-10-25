package messages;

import java.util.LinkedList;

import event.Task;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {

	private IChannel channel;

	private MyReadListener myReadListener;

	private Listener listener;

	// For sending
	private byte[] length;
	private Task sendTask;
	private MyWriteListener myWriteListener;
	private Message currentSendingMessage;
	private LinkedList<Message> messages;
	private Runnable startSending;

	public MessageQueue(IChannel channel) {
		this.channel = channel;
		myReadListener = new MyReadListener();
		sendTask = new Task("SendTask");
		currentSendingMessage = null;
		this.length = new byte[4];
		myWriteListener = new MyWriteListener();
		messages = new LinkedList<Message>();

		startSending = new Runnable() {
			@Override
			public void run() {
				if (currentSendingMessage != null) {
					for (int i = 0; i < 4; i++) {
						length[i] = (byte) ((currentSendingMessage.getLength() >> (i * 8)) & 0xFF);
					}
					channel.write(length, 0, 4, myWriteListener);
				}
			}
		};

	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
		myReadListener.setListener(listener);
		channel.setReadListener(myReadListener);
		myWriteListener.setListener(listener);
	}

	@Override
	public boolean send(Message message) {
		if (channel.disconnected()) {
			return false;
		}
		if (currentSendingMessage != null) {
			messages.add(message);
		} else {
			currentSendingMessage = message;
			sendTask.post(startSending);
		}
		return true;
	}

	@Override
	public void close() {
		IChannel.DisconnectListener disconnectListener = new IChannel.DisconnectListener() {
			@Override
			public void disconnected() {
				listener.closed();
			}
		};

		channel.disconnect(disconnectListener);
	}

	@Override
	public boolean closed() {
		return channel.disconnected();
	}

	private class MyReadListener implements IChannel.ReadListener {

		private Listener listener;
		byte[] data;
		int offset;

		State state = State.READING_LENGTH; // initial state

		enum State {
			READING_LENGTH, READING_MSG
		};

		public MyReadListener() {
			this.listener = null;
			this.data = null;
			this.offset = 0;
		}

		void setListener(Listener listener) {
			this.listener = listener;
		}

		@Override
		public void available() {
			switch (state) {
			case READING_LENGTH:
				int byteRead = channel.read(length, offset, 4 - offset);
				offset += byteRead;
				if (offset == 4) {
					int len = 0;
					for (int i = 0; i < 4; i++) {
						len += (length[i] & 0xFF) << (i * 8);
					}
					data = new byte[len];
					offset = 0;
					state = State.READING_MSG;
				}
				break;
			case READING_MSG:
				int byteRead2 = channel.read(data, offset, data.length - offset);
				offset += byteRead2;
				if (offset == data.length) {
					offset = 0;
					state = State.READING_LENGTH;
					listener.received(data);
				}
				break;
			}
		}
	}

	private class MyWriteListener implements IChannel.WriteListener {

		enum State {
			WRITING_LENGTH, WRITING_MSG
		};

		State state = State.WRITING_LENGTH;
		byte[] length;
		int offset;

		Listener listener;

		public MyWriteListener() {
			this.length = new byte[4];
			this.offset = 0;
		}

		public void setListener(Listener listener) {
			this.listener = listener;
		}

		@Override
		public void written(int bytesWritten) {
			if (channel.disconnected())
				return;

			offset += bytesWritten;
			switch (state) {
			case WRITING_LENGTH:
				if (offset == 4) {
					offset = 0;
					state = State.WRITING_MSG;
					channel.write(currentSendingMessage.getBytes(), 0, currentSendingMessage.getLength(), this);
				} else {
					channel.write(length, offset, 4 - offset, this);
				}
				break;
			case WRITING_MSG:
				if (offset == currentSendingMessage.getLength()) {
					offset = 0;
					state = State.WRITING_LENGTH;
					listener.sent(currentSendingMessage);
					currentSendingMessage = null;
					if (!messages.isEmpty()) {
						Message message = messages.poll();
						sendTask.post(startSending);
					}
				} else {
					channel.write(currentSendingMessage.getBytes(), offset, currentSendingMessage.getLength() - offset,
							this);
				}
				break;
			}
		}

	}

}