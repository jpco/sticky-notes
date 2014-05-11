// STICKYNOTES - Version 1.1.4
// By Jack Conger, 9/12/2013

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public final class StickyNotes {
	
	private Set<StickyNote> notes;
	private StickyNotesIO io;
	private StickyNotesConsole console;
	private StickyNotesPrefs prefs;
	
	// Calls init()
	public static void main(String[] args) {
		new StickyNotes().init();
	}
	
	// Creates the IO and Console classes, loads the default file(s),
	// and if those exist, create the StickyNotes associated with them.
	// If not, creates a new StickyNote.
	private void init() {
		io = new StickyNotesIO(this);
		List<String> defOpenFiles = io.loadData();
		
		console = new StickyNotesConsole();
		prefs = new StickyNotesPrefs(this);
		
		notes = new HashSet<StickyNote>();
		
		if(defOpenFiles != null) {
			for(String fName : defOpenFiles) {
				String[] bits = fName.split(",");
				
				addNote(bits[0], Integer.parseInt(bits[1]), Integer.parseInt(bits[2]), prefs.monospace);
			}
			if(notes.size() == 0) {
				addNote("default.txt", 30, 30, prefs.monospace);
			}
			if(notes.size() == 0) {
				try {
					notes.add(new StickyNote("default.txt",this,30,30, prefs.monospace, true));
				} catch(FileNotFoundException ex){
					System.out.println("...The hell? There is no help for you.");
					System.exit(0);
				}
			}
		} else {
			addNote(null, 30, 30, prefs.monospace);
		}
	}
	
	// Creates a new note with the user-created name.
	public void createNewNote(StickyNote source) {
		String s = " ";
		try {
			while(s.contains(" ")) {
				s = JOptionPane.showInputDialog(
						source.getFrame(),
						"What should the note be called?",
						"New Note",
						JOptionPane.PLAIN_MESSAGE);
				console.addMessage("s = "+s);
			}
		} catch (NullPointerException e) { }
		if(s != null && !s.trim().equals("")) {
			try {
				notes.add(new StickyNote(s+".txt",this,30,30, prefs.monospace, true));
			} catch(Exception ex){ }
		}
	}
	private void addNote(String filename, int x, int y, boolean monospace) {
		try {
			StickyNote newNote = new StickyNote(filename, this, x, y, monospace, false);
			notes.add(newNote);
		} catch(FileNotFoundException ex) {
			printError(ex);
		}
	}
	
	// Prints an error to the console.
	public void printError(Exception ex) {
		console.addMessage("Exception: "+ex);
	}
	
	// Returns the set of StickyNotes currently active.
	public Set<StickyNote> getNotes() {
		return notes;
	}
	
	// Returns the StickyNotes IO.
	public StickyNotesIO getIO() {
		return io;
	}
	
	public StickyNotesConsole getConsole() {
		return console;
	}
	
	public StickyNotesPrefs getPrefs() {
		return prefs;
	}
	
	// Removes the StickyNote from the list of active notes;
	// if there is only one, save it and exit.
	public void closeNote(StickyNote note) {
		if(notes.size() == 0) {
			System.exit(0);
		} else if(notes.size() == 1) {
			if(prefs.saveOnClose) io.saveFile(note);
			io.saveSettings();
			
			System.exit(0);
		} else {
			if(prefs.saveOnClose) io.saveFile(note);
			notes.remove(note);
		}
	}
	
	// Creates a StickyNote from the passed file.
	public void openNote(File file) {
		addNote(file.getName(), 30, 30, prefs.monospace);
	}
	
	// Saves all the notes and the settings, and exits.
	public void quit() {
		for(StickyNote note : notes) io.saveFile(note);
		io.saveSettings();
		System.exit(0);
	}
	
	public void applyPrefs() {
		for(StickyNote note : notes) {
			note.applyPrefs();
		}
	}
}
