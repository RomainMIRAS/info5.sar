package ichannels;

public interface IChannel {
	
	public boolean write(byte[] bytes, int offset, int length);
	
	interface Listener {
		void readed(byte[] bytes);
		
		void disconnected();
		
		void wrote(int bytesWrote);
	}
	

	public void setListener(Listener listener);

	public void disconnect();
	
	public boolean disconnected();
}
