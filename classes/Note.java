package classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Note implements Postponeable, Serializable, Runnable {
    private static final long serialVersionUID = 1L;
    private static int notesCreated = 0;
    private String name = "No name";
    private String note;
    protected LocalDate deadlineDate;
    private String formattedDateTime;

    public static int getNotesCreated() {
        return notesCreated;
    }

    public Note() {
        this("Do hw");
        LocalDateTime noteCreationTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.formattedDateTime = noteCreationTime.format(formatter);
    }

    public Note(String note) {
        this.note = note;
        LocalDateTime noteCreationTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.formattedDateTime = noteCreationTime.format(formatter);
        ++notesCreated;
    }

    public Note(String name, String note) {
        this.note = note;
        this.name = name;
        LocalDateTime noteCreationTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.formattedDateTime = noteCreationTime.format(formatter);
        ++notesCreated;
    }

    public Duration getTimeLeft() {
        LocalDate today = LocalDate.now();
        if (deadlineDate.isBefore(today)) {
            return Duration.ZERO; // Deadline has already passed
        }
        return Duration.between(LocalDateTime.now(), deadlineDate.atStartOfDay());
    }

    public String getDeadlineDate() {
        Duration timeLeft = getTimeLeft();

        if (timeLeft.isZero()) {
            return "Deadline has passed";
        } else if (timeLeft.isNegative()) {
            return "Deadline is in the future";
        } else {
            long days = timeLeft.toDays();
            long hours = timeLeft.toHours() % 24;
            long minutes = timeLeft.toMinutes() % 60;

            String timeLeftString = String.format("%d days, %d hours, %d minutes", days, hours, minutes);
            return "Time left: " + timeLeftString;
        }
    }

    public void setDeadlineDate(LocalDate deadlineDate) throws NoteException {
        if (deadlineDate != null && deadlineDate.isBefore(LocalDate.now())) {
            throw new NoteException("Invalid deadline date - given date is in the past");
        }
        this.deadlineDate = deadlineDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNoteCreationDate() {
        LocalDateTime noteCreationTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.formattedDateTime = noteCreationTime.format(formatter);
    }

    public void setNoteCreationDate(String date) {
        this.formattedDateTime = date;
    }

    public String getNoteCreationDate() {
        return formattedDateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) throws NoteException {
        if (note.trim().isEmpty()) {
            throw new NoteException("Note cannot be empty");
        }
        this.note = note;
    }

    public void postpone(int daysToPostpone) {
        if (deadlineDate != null) {
            deadlineDate = deadlineDate.plusDays(daysToPostpone);
        }
    }

    @Override
    public void postpone(int daysToPostpone, LocalDate newDeadlineDate) throws DateOfNoteException {
        if (deadlineDate != null) {
            deadlineDate = deadlineDate.plusDays(daysToPostpone);
            if (newDeadlineDate != null && !newDeadlineDate.isBefore(LocalDate.now())) {
                deadlineDate = newDeadlineDate;
            }
        }
    }

    public String toString() {
        return "Name: " + getName() + "\nNote: " + note + "\nNotes created: " + getNotesCreated() + "\nDeadline: "
                + getDeadlineDate();
    }

    public static void saveNotes(List<Note> notes, String filename) {
        Thread saveThread = new Thread(new Runnable() {
            public void run() {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                    oos.writeObject(notes);
    
                    // Get the directory path of the Java source files
                    File directory = new File(Note.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
    
                    // Create a .txt file for each note in the directory
                    for (Note note : notes) {
                        String noteFileName = note.getName() + ".txt";
                        File noteFile = new File(directory, noteFileName);
                        try (FileWriter writer = new FileWriter(noteFile)) {
                            writer.write(note.getNote());
                        } catch (IOException e) {
                            System.err.println("Error saving note: " + note.getName() + " - " + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error writing notes to file: " + e.getMessage());
                } catch (URISyntaxException e) {
                    System.err.println("Error getting directory path: " + e.getMessage());
                }
            }
        });
        saveThread.start();
    }    


    public static List<Note> loadNotes(String fileName) {

        List<Note> notes = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> list = (List<?>) obj;
                for (Object o : list) {
                    if (o instanceof Note) {
                        notes.add((Note) o);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading notes from file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading notes from file: class not found - " + e.getMessage());
        }
        return notes;
    }

    public static void addOrUpdateNote(List<Note> notes, Note note, String fileName) {
        int index = notes.indexOf(note);
        if (index == -1) {
            notes.add(note);
        } else {
            notes.set(index, note);
        }
        saveNotes(notes, fileName);
    }

    public static void deleteNote(List<Note> notes, Note note, String fileName) throws IllegalArgumentException {
        if (notes == null || notes.isEmpty()) {
            throw new IllegalArgumentException("Notes list is null or empty");
        }
        if (note == null) {
            throw new IllegalArgumentException("Note is null");
        }
        notes.remove(note);
        saveNotes(notes, fileName);
    }

    public static List<Note> getNotesByName(List<Note> notes, String name) {
        List<Note> notesByName = new ArrayList<>();
        for (Note note : notes) {
            if (note.getName().equalsIgnoreCase(name)) {
                notesByName.add(note);
            }
        }
        return notesByName;
    }

    @Override
    public void run() {
     throw new UnsupportedOperationException("Method 'run' is yet to be implemented.");
    }

}