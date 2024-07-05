package classes.factories;
import classes.subclasses.PersonalNote;

public class PersonalNoteFactory extends NoteFactory {
    public PersonalNote createNote(String name, String note, String relation) {
        return new PersonalNote(name, note, relation);
    }
}
