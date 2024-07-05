package example;

import java.util.ArrayList;
import classes.Note;

public class NoteMain {
    public static void main(String[] args) {
                ArrayList<Note> notes = new ArrayList<Note>();
                notes.add(new Note("Note 1", "hi"));
                notes.add(new Note("Note 2", "2"));
                notes.add(new Note("Note 3", "3"));
        
                for (Note note : notes) {
                    System.out.println(note);
                }
        
        // Input input = new Input("input.bin", true, false); // save input data to file
        // Thread inputThread = new Thread(input);
        // inputThread.start();
    }
}