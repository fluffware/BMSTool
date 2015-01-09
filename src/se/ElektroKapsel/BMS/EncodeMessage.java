package se.ElektroKapsel.BMS;

import java.nio.ByteBuffer;
import static se.ElektroKapsel.BMS.Protocol.*;
public class EncodeMessage {
	
	/**
	 * Calculate checksum for a message. The checksum is encoded in such a way that
	 * the sum of all characters in the message is 0 (modulo 256).
	 *  
	 * @param msg message to calculate checksum for. 
	 * @return the checksum encoded as a string
	 */
	protected static byte [] checkSum(byte[] msg) {
		int sum = 0;
		for (int b = 0; b < msg.length;b++) {
			sum -= msg[b];
		}
		
		sum &= 0xff;
		if (sum < 56) {
			return new byte [] {'d','Z', (byte)(sum+66)};
		} else if (sum < 65) {
			return new byte [] {'z','z', (byte)(sum+12)};
		} else if (sum < 122) {
			return new byte [] {(byte)(sum)};
		} else if (sum < 179) {
			return new byte [] {'9', (byte)(sum-57)};
		} else if (sum < 237) {
			return new byte [] {'r', (byte)(sum-114)};
		} else {
			return new byte [] {'V','V', (byte)(sum-172)};
		}
	}
	/**
	 * Encode outgoing message
	 * 
	 * @param msg message to encode
	 * 
	 * @return Encoded byte array
	 */
	public static byte [] encode(Message msg) {
		String msg_str;
		if (msg.data >= 0) {
			msg_str = String.format(":%c%03d:%s %d&", msg.dir, msg.addr, msg.cmd, msg.data);
		} else {
			msg_str = String.format(":%c%03d:%s&", msg.dir, msg.addr, msg.cmd, msg.data);
		}
		byte [] bytes = msg_str.getBytes(ASCII_enc);
		byte [] cs = checkSum(bytes);
		ByteBuffer out = ByteBuffer.allocate(bytes.length + cs.length+2);
		out.put(bytes);
		out.put(cs);
		out.put(CRLF);
		return out.array();
	}
}
