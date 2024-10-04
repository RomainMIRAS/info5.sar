package channels;

import ichannels.DisconnectedException;
import ichannels.IChannel;

public class Channel implements IChannel {
	private CircularBuffer inputBuffer;
	private CircularBuffer outputBuffer;
	private Channel remoteChannel; // The remote channel to which this channel is connected

	private boolean disconnected;
	private boolean dangling;
	
	public Channel() {
		inputBuffer = new CircularBuffer(64);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
		if (disconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}
		int bytesRead = 0;
		try {
			while (bytesRead == 0) {
				if (inputBuffer.empty()) {
					synchronized (inputBuffer) {
						while (inputBuffer.empty()) {
							if (disconnected)
								throw new DisconnectedException("Channel is remotely disconnected");
							try {
								inputBuffer.wait();
							} catch (InterruptedException e) {
								// Do nothing
							}
						}
					}
				}
				
				while (bytesRead < length && !inputBuffer.empty()) {				
					byte value = inputBuffer.pull();
					bytes[offset + bytesRead] = value;
					bytesRead++;
				}
				
				if (bytesRead != 0) {
					synchronized (inputBuffer) {
						inputBuffer.notify();
					}
				}
			}
			
		} catch (DisconnectedException e) {
			if (!disconnected) {
				disconnected = true;
				synchronized (outputBuffer) {
					outputBuffer.notifyAll();
				}
			}
			throw e;
		}
		return bytesRead;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws DisconnectedException {	
		if (disconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}
		int bytesWritten = 0;
		
		while (bytesWritten == 0) {
			if (outputBuffer.full()) {
				synchronized (outputBuffer) {
					while (outputBuffer.full()) {
						if (disconnected || dangling) throw new DisconnectedException("Channel is remotely disconnected");
						try {
							outputBuffer.wait();
						} catch (InterruptedException e) {
							// Do nothing
						}
					}
				}
			}

			while (bytesWritten < length && !outputBuffer.full()) {
				byte value = bytes[offset + bytesWritten];
				outputBuffer.push(value);
				bytesWritten++;
			}

			if (bytesWritten != 0) {
				synchronized (outputBuffer) {
					outputBuffer.notify();
				}
			}
		}
		
		return bytesWritten;
	}

	@Override
	public void disconnect() {
		synchronized (this) {
			if (disconnected) {
				return;
			}
			disconnected = true;
			this.remoteChannel.dangling = true;
		}
		synchronized (outputBuffer) {
		    outputBuffer.notifyAll();
		}
		synchronized (inputBuffer) {
			inputBuffer.notifyAll();
		}
	}

	@Override
	public boolean disconnected() {
		return disconnected;
	}

	// Method to be called when the remote side disconnects
	public synchronized void remoteDisconnect() {
		dangling = true;
		notifyAll(); // Wake up any blocked read/write operations
	}

	public void connect(Channel connectChannel, String name) {
		this.remoteChannel = connectChannel;
		connectChannel.remoteChannel = this;
		this.outputBuffer = connectChannel.inputBuffer;
		connectChannel.outputBuffer = this.inputBuffer;
	}
}
