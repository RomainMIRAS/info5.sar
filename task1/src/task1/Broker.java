package task1;

public abstract class Broker {
	/**
	 * Create a broker with the given name
	 * @param name
	 */
	Broker(String name);
	
	/**
	 * Connect to a server with the given name and port.
	 * @param name
	 * @param port
	 * @return a Channel object representing the connection
	 */
	Channel connect(String name, int port);
	
	/**
	 * Accept a connection on the given port.
	 * This method blocks until a connection is made.
	 * 
	 * @param port
	 * @return a Channel object representing the connection
	 */
	Channel accept(int port);
}
