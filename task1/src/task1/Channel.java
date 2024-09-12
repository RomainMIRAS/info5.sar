package task1;

//TODO Bien tester les codes retour sur les sockets

public abstract class Channel {
	/**
	 * Read up to length bytes into the given array.
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes read and -1 if the connection is closed
	 */
	int read(byte[] bytes, int offset, int length);
	
	/**
	 * Write up to length bytes from the given array.
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes written
	 */
	int write(byte[] bytes, int offset, int length);
	
	/**
	 * Close the connection.
	 */
	void disconnect();
	
	/**
	 * Check if the connection is closed.
	 * @return
	 */
	boolean disconnected();
}
