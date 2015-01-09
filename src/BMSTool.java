import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

















import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;



import se.ElektroKapsel.BMS.SerialTransceiver;
import jssc.SerialPortException;



public class BMSTool {
	static class QuitAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3368964938649367920L;

		/**
		 * 
		 */

		public QuitAction(App app) {
			super("Quit");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_Q,
					java.awt.event.InputEvent.CTRL_DOWN_MASK));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}

	static class OpenAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4752327444725892126L;
		private App app;

		public OpenAction(App app) {
			super("Open");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_O,
					java.awt.event.InputEvent.CTRL_DOWN_MASK));
			this.app = app;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Log database", "rdb");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(app.main);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				open_file(app, chooser.getSelectedFile());
			}
		}
	};

	static private void open_file(App app, File file) {
		if (file.exists()) {
		} else {
			JOptionPane.showMessageDialog(app.main,
					"File doesn't exists");
		}
	}
	
	
	static class App {
		
		public JFrame main;
		public SerialTransceiver transceiver;
	};

	
	private static class FileTransferHandler extends TransferHandler {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8299947423982568582L;
		protected App app;
		public FileTransferHandler(App app) {
			this.app = app;
			
		}
		@Override
		public boolean canImport(TransferSupport supp) {
			if ((supp.getSourceDropActions() & COPY) != COPY) return false;
			supp.setDropAction(COPY); // Always use copy action
			return supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor);		
		}
		
		@Override
		public boolean importData(TransferSupport supp) {
			if (!canImport(supp)) return false;
			Transferable t = supp.getTransferable();
			try {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() < 1) return false;
				File file = files.get(0);
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return true;
		}
	}
	
	
	private static void createMainWindow(final App app) {
		JFrame win = new JFrame("Log reader");
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		
	

		Container body = win.getContentPane();
		body.setLayout(new BoxLayout(win.getContentPane(), BoxLayout.Y_AXIS));

		Container top_box = new Box(BoxLayout.X_AXIS);
		
		top_box.add(Box.createHorizontalGlue());
	
		String [] port_names = jssc.SerialPortList.getPortNames();
		System.err.println("Ports "+port_names.length);
		final JComboBox<String> ser_name = new JComboBox<String>(port_names);
		
		top_box.add(ser_name);
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) {
			
				try {
					app.transceiver = new SerialTransceiver((String)ser_name.getSelectedItem());
				} catch(SerialPortException e) {
					JOptionPane.showMessageDialog(
							app.main,
							"Error while opening serial port: "
									+ e.getClass().getName() + ": "
									+ e.getMessage());
				}
			}
		});
		top_box.add(connect);
		body.add(top_box);
		win.setTransferHandler(new FileTransferHandler(app));

		JMenuBar menu_bar = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		file_menu.add(new OpenAction(app));
		file_menu.add(new QuitAction(app));
		menu_bar.add(file_menu);
		win.setJMenuBar(menu_bar);
		win.pack();
		win.setVisible(true);
		app.main = win;
	}

	
	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				App app = new App();
				
				createMainWindow(app);

				
				System.err.println("Running");
			}
		});

	}
}
