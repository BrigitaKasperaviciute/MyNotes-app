package classes.subclasses;

import classes.LongPostponeable;
import classes.Note;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.DayOfWeek;
import classes.factories.WorkNoteFactory;
import java.util.ArrayList;
import java.util.List;

public class WorkNote extends Note implements Cloneable, LongPostponeable {

    private String workType;
    private LocalDateTime time = LocalDateTime.now();// workingHours

    public WorkNote(String name, String note, String workType) {
        super(name, note);
        this.workType = workType;
    }

    public long getdaysUntillDeadline(LocalDate deadlineDate) {
        LocalDate today = LocalDate.now();
        return ChronoUnit.DAYS.between(today, deadlineDate);
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkingTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getWorkingTime() {
        return time;
    }

    public void changeTime(int hoursToPostpone) {
        time = time.plusHours(hoursToPostpone);
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    // užklojimas
    public void postpone(int daysToPostpone, LocalDate deadlineDate) {
        deadlineDate = deadlineDate.plusDays(daysToPostpone);
        if (deadlineDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
            deadlineDate = deadlineDate.plusDays(2);
        } else if (deadlineDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            deadlineDate = deadlineDate.plusDays(1);
        }
        // super.metodas() - galimybė nurodyti, kad bus naudojama bazinės klasės metodo realizacija
        super.postpone((int) ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate));
    }

    public void postpone(int daysToPostpone, int weeksToPostpone, LocalDate deadlineDate) {
        deadlineDate = deadlineDate.plusDays(daysToPostpone);
        deadlineDate = deadlineDate.plusWeeks(weeksToPostpone);
    }

    public String toString() {
        return getNote() + ". Work type: " + workType;
    }
    
    public WorkNote clone() throws CloneNotSupportedException {
        WorkNoteFactory factory = new WorkNoteFactory();
        WorkNote clonedWorkNote = factory.createNote(this.getName(), this.getNote(), this.getWorkType());
        return clonedWorkNote;
    }
    
    @SuppressWarnings("unlikely-arg-type")
    public static List<Note> getNotesByDeadline(List<Note> notes, LocalDate date) {
        List<Note> notesByDeadline = new ArrayList<>();
        for (Note note : notes) {
            if (note.getDeadlineDate() != null && note.getDeadlineDate().equals(date)) {
                notesByDeadline.add(note);
            }
        }
        return notesByDeadline;
    }

}