package se.ElektroKapsel.BMS;
import static se.ElektroKapsel.BMS.Protocol.*;

import java.util.logging.Level;

public class DecodeMessage {
	
	// Line buffer
	final int MaxBuffer = 25;
	byte msgBuffer[] = new byte[MaxBuffer];
	int msgLen = 0;
	
	Handler handler;
	
	/**
	 * Callback called when ever a message is received
	 * @author sibe
	 *
	 */
	public interface Handler {
		/**
		 * Handle an incoming message
		 * 
		 * @param msg to handle
		 */
		void handleMsg(Message msg);
	}

	
	public void decode(byte [] in, int length) {
		
		for (int i = 0; i < length; i++) {
			if (in[i] == 0x0a) {
				// Ignore
			} else if (in[i] == 0x0d) {
				if (msgLen > 0) {
					decodeMsg();
					msgLen = 0;
				}
			} else if (msgLen < MaxBuffer){
				msgBuffer[msgLen++] = in[i];
			} else {
				// Buffer overflow, start over
				msgLen = 0;
			}
		}
	}
	
	protected void decodeMsg() {
		int sum = 0;
		for (int i = 0; i < msgLen;i++) {
			sum += msgBuffer[i];
		}
		if ((sum & 0xff) != 0) return;
		String msg = new String(msgBuffer, ASCII_enc);
		if (msg.charAt(0) != ':' && msg.charAt(5) != ':') return;
		char dir= msg.charAt(1);
		if (dir != MasterToSlave && dir != SlaveToMaster) return;
		int end = msg.indexOf('&',6);
		if (end == -1) return;
		int startData = msg.indexOf(' ',6);
		if (startData == -1 || startData >= end) {
			startData = end;
		}
		int addr = Integer.parseInt(msg.substring(2, 5), 10);
		
		String cmd =  msg.substring(6,startData);
		startData++; // Skip space
		
		int data = -1;
		if (startData < end) {
			data = Integer.parseInt(msg.substring(startData, end), 10);
		}
		if (handler != null) {
			handler.handleMsg(new Message(dir, addr, cmd, data));
		} else {
			Logger.log(Level.FINER, (dir == MasterToSlave ? "->": "<-")+" "+addr+" "+cmd+" "+data);
		}
	}
	
	public void setMessageHandler(Handler handler) {
		this.handler =handler;
	}
}
