package ichannels;

public interface IBroker {
	
	/**
	 * Connect to a server with the given name and port.
	 * @param name
	 * @param port
	 * @return a Channel object representing the connection
	 */
	public IChannel connect(String name, int port);
	
	/**
	 * Accept a connection on the given port.
	 * This method blocks until a connection is made.
	 * 
	 * @param port
	 * @return a Channel object representing the connection
	 */
	public IChannel accept(int port);
}
