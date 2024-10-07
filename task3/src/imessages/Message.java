package imessages;

public class Message {

	private byte[] bytes;
	private int offset;
	private int length;
	
	/**
	 * Create a message with the given bytes
	 * 
	 * @param bytes Bytes
	 */
	public Message(byte[] bytes) {
		this(bytes, 0, bytes.length);
	}
	
	/**
	 * Create a message with the given bytes, offset and length
	 * 
	 * @param bytes  Bytes
	 * @param offset Offset
	 * @param length Length
	 */
	public Message(byte[] bytes, int offset, int length) {
		this.bytes = bytes;
        this.offset = offset;
        this.length = length;
    }
	
	/**
	 * Get the bytes of this messages
	 */
	public byte[] getBytes() {
		return bytes;
	}
}
