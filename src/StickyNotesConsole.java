import java.awt.Font;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class StickyNotesConsole extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private List<String> message;
	private JTextArea jta;
	
	private boolean printMessages = false;
	
	public StickyNotesConsole() {
		super("Console");
		
		message = new ArrayList<String>();
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLocation(100, 100);
		
		jta = new JTextArea();
		jta.setEditable(false);
		jta.setFont(new Font("Consolas", Font.PLAIN, 12));
		if(Toolkit.getDefaultToolkit().getScreenSize().getHeight() >= 1080) jta.setFont(new Font("Consolas", Font.PLAIN, 14));
		JScrollPane jsp = new JScrollPane(jta);
		
		DefaultCaret caret = (DefaultCaret)jta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		getContentPane().add(jsp);
		pack();
		setSize(400,200);
	}
	
	public void addMessage(String m) {
		message.add(m);
		jta.append(m+"\n");
		
		if(printMessages) System.out.println(m);
	}
	
	public void display() {
		setVisible(true);
	}
}