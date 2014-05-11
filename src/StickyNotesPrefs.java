import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class StickyNotesPrefs implements ItemListener, ActionListener {
	boolean saveOnClose = true;
	JCheckBox socCheckBox;
	boolean monospace = true;
	JCheckBox monCheckBox;
	boolean alwaysOnTop = true;
	JCheckBox aotCheckBox;
	JFrame jf;
	
	boolean bigScreen = false;
	StickyNotes manager;

	public StickyNotesPrefs(StickyNotes mang) {
		manager = mang;
		
		if(Toolkit.getDefaultToolkit().getScreenSize().getHeight() >= 1080) bigScreen = true;

		jf = new JFrame("Preferences");
		
		socCheckBox = new JCheckBox("Save on Close");
		if(bigScreen) socCheckBox.setFont(new Font("Dialog", Font.PLAIN, 14));
		socCheckBox.setSelected(saveOnClose);
		socCheckBox.addItemListener(this);
		
		monCheckBox = new JCheckBox("Monospace");
		if(bigScreen) monCheckBox.setFont(new Font("Dialog", Font.PLAIN, 14));
		monCheckBox.setSelected(monospace);
		monCheckBox.addItemListener(this);
		
		aotCheckBox = new JCheckBox("Always on Top");
		if(bigScreen) aotCheckBox.setFont(new Font("Dialog", Font.PLAIN, 14));
		aotCheckBox.setSelected(alwaysOnTop);
		aotCheckBox.addItemListener(this);
		
		JButton applyButton = new JButton("Apply");
		if(bigScreen) applyButton.setFont(new Font("Dialog", Font.BOLD, 14));
		applyButton.addActionListener(this);
		
		JPanel cbPanel = new JPanel(new GridLayout(0, 1));
		cbPanel.add(socCheckBox);
		cbPanel.add(monCheckBox);
		cbPanel.add(aotCheckBox);
		cbPanel.add(applyButton);
		
		jf.add(cbPanel);
		jf.setSize(132,126);
		if(bigScreen) jf.setSize(183,147);
//		jf.setResizable(false);
	}
	
	public void open() {
		jf.setVisible(true);
	}

	@Override
	public void itemStateChanged(ItemEvent iev) {
		boolean selected = (iev.getStateChange() == ItemEvent.SELECTED);
		String endisabled = "disabled";
		if(selected) endisabled = "enabled";
		Object source = iev.getItemSelectable();
		if(source == socCheckBox) {
			saveOnClose = selected;
			manager.getConsole().addMessage("Save on close " + endisabled);
		} else if(source == monCheckBox) {
			monospace = selected;
			manager.applyPrefs();
			manager.getConsole().addMessage("Monospace " + endisabled);
		} else if(source == aotCheckBox) {
			alwaysOnTop = selected;
			manager.applyPrefs();
			manager.getConsole().addMessage("Always on top " + endisabled);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ev) {
		manager.applyPrefs();
	}
}
