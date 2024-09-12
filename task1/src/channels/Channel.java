package channels;

//TODO Bien tester les codes retour sur les sockets

public abstract class Channel {
	/**
	 * Read up to length bytes into the given array.
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes read and -1 if the connection is closed
	 */
	public int read(byte[] bytes, int offset, int length) {
		//TODO	
		return 0;
	}
	
	/**
	 * Write up to length bytes from the given array.
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes written
	 */
	public int write(byte[] bytes, int offset, int length) {
		//TODO
		return 0;
	}
	
	/**
	 * Close the connection.
	 */
	public void disconnect() {
		//TODO
	}
	
	/**
	 * Check if the connection is closed.
	 * @return
	 */
	boolean disconnected() {
		//TODO
        return false;	
     }
}
