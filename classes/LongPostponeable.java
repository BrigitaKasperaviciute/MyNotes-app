package classes;
import java.time.LocalDate;

public interface LongPostponeable extends Postponeable {
    void postpone(int daysToPostpone, int weeksToPostpone, LocalDate deadlineDate);
}