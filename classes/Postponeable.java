package classes;

import java.time.LocalDate;

public interface Postponeable {
    void postpone(int daysToPostpone, LocalDate deadlineDate) throws DateOfNoteException;
}