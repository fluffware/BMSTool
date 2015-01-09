package se.ElektroKapsel.BMS;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import se.ElektroKapsel.BMS.DecodeMessage.Handler;

/**
 * Abstract class for sending and receiving BMS messages
 * 
 * @author sibe
 *
 */
public abstract class Transceiver {
	protected DecodeMessage decoder;
	protected LinkedBlockingQueue<Protocol.Message> queue;

	public class SynchHandler implements DecodeMessage.Handler {
		public void handleMsg(Protocol.Message msg) {
			try {
			queue.add(msg);
			} catch(IllegalStateException e) {
				Logger.log(Level.SEVERE, "Failed to add received message to queue. Queue full.");
			}
		}
	}

	public Transceiver() {
		decoder = new DecodeMessage();
		decoder.setMessageHandler(new SynchHandler());
		queue = new LinkedBlockingQueue<Protocol.Message>(2);
	}

	public abstract void sendMsg(Protocol.Message msg);

	public void setMessageHandler(Handler handler) {
		decoder.setMessageHandler(handler);
	}

	/**
	 * Receive the next message matching the pattern.
	 * 
	 * @param pattern
	 *            to match. Only return messages whose fields match the pattern.
	 *            Set to null to always match. Set fields in the pattern to -1
	 *            or null to match any value.
	 *            
	 * @param timeout milliseconds to wait before returning null
	 * @return a received message or null if timeout
	 * @throws InterruptedException if interrupted
	 */

	public Protocol.Message receiveMsg(Protocol.Message pattern, int timeout)
			throws InterruptedException {
		Protocol.Message msg = null;
		while (true) {
			if (timeout > 0) {
				msg = queue.poll(timeout, TimeUnit.MILLISECONDS);
			} else {
				msg = queue.poll();
			}
			if (pattern == null)
				break;
			if ((pattern.dir == -1 || pattern.dir == msg.dir)
					&& (pattern.addr == -1 || pattern.addr == msg.addr)
					&& (pattern.cmd == null || pattern.cmd.equals(msg.cmd))
					&& (pattern.data == -1 || pattern.addr == msg.data)) {
				break;
			}
		}
		return msg;
	}

	/**
	 * Receive the next message matching the pattern.
	 * 
	 * @param pattern
	 *            to match. Only return messages whose fields match the pattern.
	 *            Set to null to always match. Set fields in the pattern to -1
	 *            or null to match any value.
	 * @return a received message or null if timeout
	 * @throws InterruptedException if interrupted
	 */

	public Protocol.Message receiveMsg(Protocol.Message pattern)
			throws InterruptedException {
		return receiveMsg(pattern, 0);
	}

}
