package classes.factories;
import classes.Note;

public abstract class NoteFactory {
    public abstract Note createNote(String name, String note, String additionalInfo);
}