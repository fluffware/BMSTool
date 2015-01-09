/**
 * 
 */
package se.ElektroKapsel.BMS;

import java.nio.charset.Charset;

/**
 * Protocol specific constants, etc.
 * 
 * @author sibe
 *
 */
public class Protocol {
	/**
	 * Represents an unencoded BMS message
	 * 
	 * @author sibe
	 *
	 */
	static public class Message {
		public Message(char dir, int addr, String cmd, int data) {
			this.dir = dir;
			this.addr = addr;
			this.cmd = cmd;
			this.data = data;
		}
		/**
		 * Direction of transfer, either {@link se.ElektroKapsel.BMS.Protocol#MasterToSlave} or {@link se.ElektroKapsel.BMS.Protocol#SlaveToMaster}
		 * 
		 */
		public char dir;
		/**
		 * Slave address
		 * 
		 */
		public int addr;
		/**
		 * Command or indication.
		 */
		public String cmd;
		/**
		 * Optional data. Set to -1 if unavailable.
		 */
		public int data;
	}
	
	/**
	 * Message is sent from master to slave.
	 */
	static public final char MasterToSlave = ';';
	/**
	 * Message is sent from slave to master.
	 */
	static public final char SlaveToMaster = ':';
	/**
	 * CR LF terminates a message.
	 */
	static public final byte [] CRLF = new byte [] {0x0d, 0x0a};
	/**
	 * Character encoding used by the protocol.
	 */
	static public final Charset ASCII_enc = Charset.forName("ASCII");
}
