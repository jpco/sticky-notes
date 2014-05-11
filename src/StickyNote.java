import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public final class StickyNote {
	private JFrame frame;
	private JTextArea jta;
	private StickyNotes manager;
	private WindowManager wm;
	private File file;
	private boolean bigScreen = false;
	
	public StickyNote(String filename, StickyNotes man, int x, int y, boolean monospace, boolean newNote) throws FileNotFoundException {
		if(filename != null) {
			file = new File(filename);
		} else {
			file = new File(toString());
		}
		manager = man;
		initGui(x, y, monospace);
		
		if(!newNote) jta.append(manager.getIO().loadFile(getFileString()));
		frame.setVisible(true);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	// Sets up the gui, leaving only for the frame to become visible.
	private void initGui(int x, int y, boolean monospace) {
		wm = new WindowManager();
		WindowCloseManager wcm = new WindowCloseManager();
		WindowMinManager wmm = new WindowMinManager();
		
		if(Toolkit.getDefaultToolkit().getScreenSize().getHeight() >= 1080) bigScreen = true;
		
		frame = new JFrame(file.getName() + " - StickyNotes");
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.addWindowListener(new WindowManager()); // should this be wm as declared above??
		frame.setAlwaysOnTop(manager.getPrefs().alwaysOnTop);
		frame.setLocation(x, y);
		
		JPanel pan = new JPanel();
		JLabel tlabel = new JLabel(file.getName());
		if(bigScreen) tlabel.setFont(new Font("Arial", Font.BOLD, 14));
		tlabel.setPreferredSize(new Dimension(211,15));
		if(bigScreen) tlabel.setPreferredSize(new Dimension(252,18));
		tlabel.addMouseListener(wm);
		tlabel.addMouseMotionListener(wm);
		pan.add(tlabel);
		
		JButton mButton = new JButton();
		mButton.setPreferredSize(new Dimension(17,15));
		if(bigScreen) mButton.setPreferredSize(new Dimension(21,18));
		mButton.addActionListener(wmm);
		pan.add(mButton);
		
		JButton eButton = new JButton();
		eButton.setPreferredSize(new Dimension(17,15));
		if(bigScreen) eButton.setPreferredSize(new Dimension(21,18));
		eButton.addActionListener(wcm);
		pan.add(eButton);
		
		pan.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		FlowLayout panLayout = (FlowLayout) pan.getLayout();
		panLayout.setVgap(2);
		panLayout.setHgap(0);
		
		jta = new JTextArea();
		jta.setLineWrap(true);
		jta.setWrapStyleWord(true);
		
		jta.setEditable(true);

		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "save");
		jta.getActionMap().put("save", new SaveAction());
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "open");
		jta.getActionMap().put("open", new OpenAction());
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK), "quit");
		jta.getActionMap().put("quit", new QuitAction());
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "new");
		jta.getActionMap().put("new", new NewAction());
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK), "console");
		jta.getActionMap().put("console", new ShowConsoleAction());
		jta.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK), "prefs");
		jta.getActionMap().put("prefs", new OpenPrefsAction());
		
		if(monospace) setMonospace(true);
		
		JScrollPane jsp = new JScrollPane(jta);
		jsp.setPreferredSize(new Dimension(250,230));
		if(bigScreen) jsp.setPreferredSize(new Dimension(300,280));
		jsp.setBorder(BorderFactory.createEmptyBorder());
		
		frame.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black));
		
		frame.getContentPane().add(pan, BorderLayout.NORTH);
		frame.getContentPane().add(jsp, BorderLayout.CENTER);
		frame.pack();
		
		manager.getConsole().addMessage(this+" GUI initialized");
	}
	
	public void setMonospace(boolean mono) {
		if(mono) {
			jta.setFont(new Font("Courier", Font.PLAIN, 12));
			if(bigScreen) jta.setFont(new Font("Courier", Font.PLAIN, 14));
		} else {
			jta.setFont(new Font("Arial", Font.PLAIN, 12));
			if(bigScreen) jta.setFont(new Font("Arial", Font.PLAIN, 14));
		}
	}
	
	public String getText() {
		return jta.getText();
	}
	
	public String getFileString() {
		if(file != null) {
			return file.getName();
		} else {
			return toString()+".txt";
		}
	}
	
	public int[] getWindowDims() {
		int[] toReturn = {frame.getX(), frame.getY(),
							frame.getWidth(), frame.getHeight()};
		
		return toReturn;
	}
	
	@Override
	public String toString() {
		if(file != null) {
			return file.getName();
		} else {
			return super.toString();
		}
	}
	
	public void applyPrefs() {
		frame.setAlwaysOnTop(manager.getPrefs().alwaysOnTop);
		setMonospace(manager.getPrefs().monospace);
	}
	
	private class QuitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+q");
			manager.quit();
		}
	}
	
	private class OpenAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+o");
			manager.getIO().browseFolder(StickyNote.this);
		}
	}
	
	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+s");
			manager.getIO().saveFile(StickyNote.this);
		}
	}
	
	private class NewAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+n");
			manager.createNewNote(StickyNote.this);
		}
	}
	
	private class ShowConsoleAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+alt+c");
			manager.getConsole().display();
		}
	}
	
	private class OpenPrefsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getConsole().addMessage(">ctrl+p");
			manager.getPrefs().open();
		}
	}
	
	private class WindowCloseManager implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			WindowEvent windowClosing = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
			frame.dispatchEvent(windowClosing);
		}
	}
	
	private class WindowMinManager implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			frame.setState(Frame.ICONIFIED);
		}
	}
	
	// Does the ugly event-listening bullshit that comes with frame being undecorated.
	private class WindowManager implements WindowListener, MouseListener, MouseMotionListener {
		int dragPosX;
		int dragPosY;

		@Override
		public void mouseDragged(MouseEvent me) {
			frame.setLocation(me.getXOnScreen()-dragPosX, me.getYOnScreen()-dragPosY);
		}
		
		@Override
		public void mousePressed(MouseEvent me) {
			dragPosX = me.getX();
			dragPosY = me.getY();
		}
		
		@Override
		public void windowClosing(WindowEvent arg0) {
			manager.getConsole().addMessage(StickyNote.this+" closing");
			manager.closeNote(StickyNote.this);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// Do nothing.
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
		public void mouseReleased(MouseEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// Do nothing.
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// Do nothing.
		}
	}

}
