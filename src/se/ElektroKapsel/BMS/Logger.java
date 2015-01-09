package se.ElektroKapsel.BMS;
import java.util.logging.Level;
public class Logger {
	static java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger("se.ElektroKapsel.BMS");

	public static void log(Level level, String msg) {
		logger.log(level, msg);
	}

	public static void log(Level level, String msg, Object param1) {
		logger.log(level, msg, param1);
	}

	public static void log(Level level, String msg, Object[] params) {
		logger.log(level, msg, params);
	}

	public static void log(Level level, String msg, Throwable thrown) {
		logger.log(level, msg, thrown);
	}
}
