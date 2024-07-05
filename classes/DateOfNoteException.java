package classes;

import java.time.LocalDate;

public class DateOfNoteException extends NoteException {
    public LocalDate wrongDate ;
    public DateOfNoteException(String message, LocalDate wrongDate) {
        super(message);
        this.wrongDate = wrongDate;
    }
}