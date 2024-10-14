package channels;

import event.Task;

public class ReadRunnable implements Runnable {
	
	private CircularBuffer inputBuffer;
	private Channel channel;
	int availableBytes;
	
	public ReadRunnable(CircularBuffer inputBuffer, Channel channel, int availableBytes) {
		this.inputBuffer = inputBuffer;
		this.channel = channel;
		this.availableBytes = availableBytes;
	}

	@Override
	public void run() {

		if (inputBuffer.empty()) {
			Task.task().post(this);
			return;
		}

	    byte[] bytes = new byte[availableBytes];
	    int bytesRead = 0;
	    while (bytesRead < availableBytes && !inputBuffer.empty()) {
	        byte value = inputBuffer.pull();
	        bytes[bytesRead] = value;
	        bytesRead++;
	    }
	    
		channel.getListener().readed(bytes);
	}

}
