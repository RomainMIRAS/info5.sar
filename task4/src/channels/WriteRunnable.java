package channels;

import event.Task;

public class WriteRunnable implements Runnable {
	
	private byte[] bytes;
	private int offset;
	private int length;
	private CircularBuffer outputBuffer;
	private Channel channel;

	public WriteRunnable(byte[] bytes, int offset, int length, CircularBuffer outputBuffer, Channel channel) {
		this.bytes = bytes;
        this.offset = offset;
        this.length = length;
        this.outputBuffer = outputBuffer;
        this.channel = channel;
	}

	@Override
	public void run() {
		if (channel.disconnected()) return;
		
		if (outputBuffer.full()) {
			Task.task().post(this);
			return;
		}
		
		int bytesWritten = 0;
		while (bytesWritten < length && !outputBuffer.full()) {
			byte value = bytes[offset + bytesWritten];
			outputBuffer.push(value);
			bytesWritten++;
		}
		
		channel.getListener().wrote(bytesWritten);
		channel.remoteChannel._read(bytesWritten);
	}

}
