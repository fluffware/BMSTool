package se.ElektroKapsel.BMS;

import java.util.logging.Level;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialTransceiver extends Transceiver implements SerialPortEventListener {
	SerialPort ser;
	public SerialTransceiver(String portName) throws SerialPortException {
		ser = new SerialPort(portName);
		ser.openPort();
		ser.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_7, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
		ser.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		int mask = SerialPort.MASK_RXCHAR;
		ser.setEventsMask(mask);
		ser.addEventListener(this);
	}
	
	public void sendMsg(Protocol.Message msg) {
		try {
			byte[] encoded = EncodeMessage.encode(msg);
			ser.writeBytes(encoded);
			System.err.print("Send: "
					+ new String(encoded,
							se.ElektroKapsel.BMS.Protocol.ASCII_enc));
		} catch (SerialPortException e) {
			Logger.log(Level.WARNING, "Error while writing to serial port", e);
		}
	}

	public void serialEvent(SerialPortEvent ev) {
		if (ev.isRXCHAR()) {
			int r = ev.getEventValue();
			if (r > 0) {
				try {
					decoder.decode(ser.readBytes(r), r);

				} catch (SerialPortException e) {
					System.err.println("Error while reading serial data: "
							+ e.getMessage());
				}
			}
		}
	}
}
