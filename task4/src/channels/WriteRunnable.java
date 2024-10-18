package channels;

import event.Task;
import ichannels.IChannel.WriteListener;

public class WriteRunnable implements Runnable {
	
	private byte[] bytes;
	private int offset;
	private int length;
	private CircularBuffer outputBuffer;
	private Channel channel;
	private WriteListener listener;

	public WriteRunnable(byte[] bytes, int offset, int length, CircularBuffer outputBuffer, Channel channel, WriteListener listener) {
		this.bytes = bytes;
        this.offset = offset;
        this.length = length;
        this.outputBuffer = outputBuffer;
        this.channel = channel;
        this.listener = listener;
	}

	@Override
	public void run() {
		if (channel.disconnected()) return;
		
		int bytesWritten = 0;
		while (bytesWritten < length && !outputBuffer.full()) {
			byte value = bytes[offset + bytesWritten];
			outputBuffer.push(value);
			bytesWritten++;
		}
		
		listener.written(bytesWritten);
		if (channel.remoteChannel.listener != null && bytesWritten > 0) {
			channel.remoteChannel.listener.available();
		}
	}

}
