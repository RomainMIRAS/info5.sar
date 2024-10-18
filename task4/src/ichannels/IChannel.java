package ichannels;

public interface IChannel {
	
	public boolean write(byte[] bytes, int offset, int length, WriteListener listener);
	
	public int read(byte[] bytes, int offset, int length);
	
	interface ReadListener {
		void available();
	}
	
	interface WriteListener {
		void written(int bytesWritten);
	}
	
	interface DisconnectListener {
		void disconnected();
	}

	public void setReadListener(ReadListener listener);
	
	public boolean disconnected();

	public void disconnect(DisconnectListener listener);
}
