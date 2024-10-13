package channels;

import ichannels.IChannel;

public class Channel implements IChannel {

	@Override
	public boolean write(byte[] bytes, int offset, int length) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disconnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}
	
}
