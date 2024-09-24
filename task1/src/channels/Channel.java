package channels;

import ichannels.DisconnectedException;
import ichannels.IChannel;

public class Channel implements IChannel {
	private CircularBuffer inputBuffer;
	private CircularBuffer outputBuffer;
	private volatile boolean localDisconnected = false;
	private volatile boolean remoteDisconnected = false;

	public Channel(CircularBuffer input, CircularBuffer output) {
		this.inputBuffer = input;
		this.outputBuffer = output;
	}

	@Override
	public synchronized int read(byte[] bytes, int offset, int length) throws DisconnectedException {
		if (localDisconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}

		int bytesRead = 0;
		while (bytesRead < length && !(inputBuffer.empty() && remoteDisconnected)) {
			try {
				bytes[offset + bytesRead] = inputBuffer.pull();
				bytesRead++;
			} catch (IllegalStateException e) {
				if (bytesRead == 0) {
					try {
						wait();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new DisconnectedException("Read operation interrupted");
					}
				} else {
					break;
				}
			}
		}

		if (bytesRead == 0 && remoteDisconnected) {
			throw new DisconnectedException("Channel is remotely disconnected");
		}

		return bytesRead;
	}

	@Override
	public synchronized int write(byte[] bytes, int offset, int length) throws DisconnectedException {
		if (localDisconnected) {
			throw new DisconnectedException("Channel is locally disconnected");
		}

		int bytesWritten = 0;
		while (bytesWritten < length && !remoteDisconnected) {
			try {
				outputBuffer.push(bytes[offset + bytesWritten]);
				bytesWritten++;
				notifyAll(); // Notify readers that data is available
			} catch (IllegalStateException e) {
				if (bytesWritten == 0) {
					try {
						wait();
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new DisconnectedException("Write operation interrupted");
					}
				} else {
					break;
				}
			}
		}

		if (remoteDisconnected) {
			// Silently drop bytes if remote side is disconnected
			return length;
		}

		return bytesWritten;
	}

	@Override
	public synchronized void disconnect() {
		localDisconnected = true;
		notifyAll(); // Wake up any blocked read/write operations
	}

	@Override
	public boolean disconnected() {
		return localDisconnected || (remoteDisconnected && inputBuffer.empty());
	}

	// Method to be called when the remote side disconnects
	public synchronized void remoteDisconnect() {
		remoteDisconnected = true;
		notifyAll(); // Wake up any blocked read/write operations
	}
}
