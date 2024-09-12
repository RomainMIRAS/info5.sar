package ichannels;

//TODO Bien tester les codes retour sur les sockets

public interface IChannel {
	/**
	 * Read up to length bytes into the given array.
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes read and -1 if the connection is closed
	 */
	public int read(byte[] bytes, int offset, int length);
	
	/**
	 * Write up to length bytes from the given array.
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return the number of bytes written
	 */
	public int write(byte[] bytes, int offset, int length);
	
	/**
	 * Close the connection.
	 */
	public void disconnect();
	
	/**
	 * Check if the connection is closed.
	 * @return
	 */
	public boolean disconnected();
}
