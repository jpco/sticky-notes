import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public final class StickyNotesIO {
	private StickyNotes manager;
	public File defPath;
	private File settingsPath;
	private String delim;
	JDialog jd;
	
	public StickyNotesIO(StickyNotes man) {
		manager = man;
		String homedir = System.getProperty("user.home");
		
		if(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			delim = "\\";
		} else {
			delim = "/";
		}
		
		defPath = new File(homedir + delim + "Dropbox" + delim + "PlainText" + delim + "StickyNotes");
		settingsPath = new File(defPath.toString() + delim + "settings");
	}
	
	public List<String> loadData() {
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			Scanner in = new Scanner(new File(settingsPath.toString() + delim + "defaults.txt"));
			
			while(in.hasNext()) {
				lines.add(in.next());
			}
			
			in.close();
		} catch(Exception ex) {
			manager.printError(ex);
		}
		
		return lines;
	}
	
	public void saveFile(StickyNote toSave) {
		manager.getConsole().addMessage("Saving file "+toSave);
		
		try {
			PrintStream out = new PrintStream(
					new File(defPath.toString() + delim + toSave.getFileString()));
			
			String[] text = toSave.getText().split("\n");
			for(String line : text) {
				out.println(line);
			}
			out.close();
			
		} catch(Exception ex) {
			manager.printError(ex);
		}
	}
	
	public void saveSettings() {
		manager.getConsole().addMessage("Saving settings");
		
		Set<StickyNote> notes = manager.getNotes();
		
		try {
			PrintStream out = new PrintStream(new File(settingsPath.toString() + delim + "defaults.txt"));
			
			for(StickyNote note : notes) {
				manager.getConsole().addMessage("Saving file");
				int[] dims = note.getWindowDims();
				out.println(note.getFileString() + "," + dims[0] + "," + dims[1]
								+ "," + dims[2] + "," + dims[3]);
			}
			
			out.close();
		} catch(Exception ex) {
			manager.printError(ex);
		}
	}

	public String loadFile(String fileString) throws FileNotFoundException {
		manager.getConsole().addMessage("Loading "+fileString);
		
		String toReturn = "";
		Scanner in = new Scanner(new File(defPath.toString() + delim + fileString));
		
		while(in.hasNext()) {
			toReturn += in.nextLine()+"\n";
		}
			
		in.close();
		
		return toReturn;
	}
	
	public void browseFolder(StickyNote source) {
		File[] files = defPath.listFiles();
		ArrayList<File> trimmedFiles = new ArrayList<File>();
		for(File file : files) {
			if(!file.isDirectory()) {
				trimmedFiles.add(file);
			}
		}
		Object[] otf = trimmedFiles.toArray();
		File[] tFiles = Arrays.copyOf(otf, otf.length, File[].class);
		
		JList<File> list = new JList<File>(tFiles);
		if(Toolkit.getDefaultToolkit().getScreenSize().getHeight() >= 1080) list.setFont(new Font("Dialog", Font.BOLD, 14));
		list.addMouseListener(new FileListHelper());
		list.addKeyListener(new FileListHelper());
		list.setCellRenderer(new FileListHelper());
		
		JScrollPane jsp = new JScrollPane(list);
		
		jd = new JDialog(source.getFrame(), "Open...", false);
		jd.getContentPane().add(jsp);
		jd.pack();
		jd.setSize(200,150);
		jd.setLocation(source.getFrame().getX()-30, source.getFrame().getY()+50);
		jd.setVisible(true);
	}
	
	private class FileListHelper extends JLabel implements ListCellRenderer<File>, MouseListener, KeyListener {
		static final long serialVersionUID = 1L;
		
		public Component getListCellRendererComponent(
					JList<? extends File> list, 
					File value, 
					int index, 
					boolean isSelected, 
					boolean cellHasFocus) {
			
			if(value != null) {
				setText(value.getName());
			}
			
			if(isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				regEvent(e);
			}
		}
		
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				regEvent(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		private void regEvent(AWTEvent e) {
			if(e.getSource() instanceof JList<?>) {
				JList<File> list = (JList<File>) e.getSource();
				manager.openNote(list.getSelectedValue());
				
				jd.setVisible(false);
				jd.dispose();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// Do nothing.
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// Do nothing.
		}
	}	
}
