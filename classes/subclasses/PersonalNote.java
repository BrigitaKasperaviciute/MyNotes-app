package classes.subclasses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import classes.DateOfNoteException;
import classes.Note;

public class PersonalNote extends Note implements Cloneable {
    private String relation;

    public PersonalNote(String name, String note, String relation) {
        super(name, note);
        this.relation = relation;// relation: to me, my family, my friends
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String toString() {
        return getNote() + ". Relation: " + relation;
    }

    public void postpone(int daysToPostpone, LocalDate deadlineDate) throws DateOfNoteException {
        LocalDate newDeadline = deadlineDate.plusDays(daysToPostpone);
        if (newDeadline.isBefore(LocalDate.now())) {
            throw new DateOfNoteException("The new deadline cannot be in the past.", newDeadline);
        }
        this.deadlineDate = newDeadline;
    }

    public PersonalNote clone() throws CloneNotSupportedException {
        PersonalNote clonedPersonalNote = (PersonalNote) super.clone();// Shallow cloning creates a new object but does not create new
        // copies of any referenced objects, instead, it copies references
        // to the original objects
        clonedPersonalNote.setRelation(relation);// deep cloning
        return clonedPersonalNote;
    }

    public static List<Note> getNotesWithNoDeadline(List<Note> notes) {
        List<Note> notesWithNoDeadline = new ArrayList<>();
        for (Note note : notes) {
            if (note.getDeadlineDate() == null) {
                notesWithNoDeadline.add(note);
            }
        }
        return notesWithNoDeadline;
    }
}