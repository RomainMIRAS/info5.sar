package channels;

import event.Task;
import ichannels.IChannel;

public class Channel implements IChannel {

	private CircularBuffer inputBuffer;
	private CircularBuffer outputBuffer;
	Channel remoteChannel;
	ReadListener listener;
	private boolean disconnected;

	private Task writeTask;
	private boolean dangling;

	public Channel() {
		inputBuffer = new CircularBuffer(64);
		writeTask = new Task("Write Task");
		disconnected = false;
		dangling = false;
	}

	@Override
	public boolean write(byte[] bytes, int offset, int length, WriteListener listener) {
		// Event base
		if (outputBuffer == null || disconnected || listener == null || dangling) {
			return false;
		}
		writeTask.post(new WriteRunnable(bytes, offset, length, outputBuffer, this, listener));
		return true;
	}

	@Override
	public void disconnect(DisconnectListener listener) {
		new Task("Disconnect Task").post(new Runnable() {
			@Override
			public void run() {
				disconnected = true;
				remoteChannel.dangling = true;

				if (listener != null) {
					listener.disconnected();
				}
			}
		});
	}

	@Override
	public boolean disconnected() {
		return disconnected;
	}

	void connect(Channel connectChannel) {
		this.remoteChannel = connectChannel;
		connectChannel.remoteChannel = this;
		this.outputBuffer = connectChannel.inputBuffer;
		connectChannel.outputBuffer = this.inputBuffer;
	}

	@Override
	public int read(byte[] bytes, int offset, int length) {
		int bytesRead = 0;
		while (bytesRead < length && !inputBuffer.empty()) {
			byte value = inputBuffer.pull();
			bytes[offset + bytesRead] = value;
			bytesRead++;
		}
		return bytesRead;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		this.listener = listener;
	}

}
