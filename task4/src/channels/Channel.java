package channels;

import event.Task;
import ichannels.IChannel;

public class Channel implements IChannel {
	
	private CircularBuffer inputBuffer;
	private CircularBuffer outputBuffer;
	Channel remoteChannel;
	private Listener listener;
	private boolean disconnected;
	
	private Task writeTask;
	private Task readTask;
	
	public Channel() {
		inputBuffer = new CircularBuffer(64);
		writeTask = new Task("Write Task");
		readTask = new Task("Read Task");
	}

	@Override
	public boolean write(byte[] bytes, int offset, int length) {
		// Event base
		if (outputBuffer == null) {
			return false;
		}
		writeTask.post(new WriteRunnable(bytes, offset, length, outputBuffer, this));
		return true;
	}

	@Override
	public void disconnect() {
		new Task("Disconnect Task").post(new Runnable() {
			@Override
			public void run() {
				disconnected = true;
				if (remoteChannel != null && !remoteChannel.disconnected()) {
					remoteChannel.disconnect();
				}
				if (listener != null) {
					listener.disconnected();
				}
			}
		});
	}
	
	void _read(int availableBytes) {
		readTask.post(new ReadRunnable(inputBuffer, this, availableBytes));
	}

	@Override
	public boolean disconnected() {
		return disconnected;
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	Listener getListener() {
		return listener;
	}
	
	void connect(Channel connectChannel) {
		this.remoteChannel = connectChannel;
		connectChannel.remoteChannel = this;
		this.outputBuffer = connectChannel.inputBuffer;
		connectChannel.outputBuffer = this.inputBuffer;
	}
	
}
