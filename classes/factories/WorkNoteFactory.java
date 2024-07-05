package classes.factories;
import classes.subclasses.WorkNote;

public class WorkNoteFactory extends NoteFactory {
    public WorkNote createNote(String name, String note, String workType) {
        return new WorkNote(name, note, workType);
    }
}