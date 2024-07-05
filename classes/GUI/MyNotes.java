import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import classes.Note;
import classes.NoteException;

public class MyNotes {
    private static List<Note> notes;
    private static JFrame frame;

    public static void main(String[] args) throws NoteException {
        notes = loadNotes();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        frame = new JFrame("My Notes");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem loadMenuItem = new JMenuItem("Load");
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0, 1, 15, 10));

        JScrollPane scrollPane = new JScrollPane(cardPanel);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        loadMenuItem.addActionListener(e -> {
            JDialog loadDialog = createLoadDialog(cardPanel);
            loadDialog.setVisible(true);
        });

        JButton newButton = new JButton("New");
        Color lightPurple = new Color(181, 174, 228);
        newButton.setBackground(lightPurple);
        newButton.addActionListener(e -> createNewNote(cardPanel));
        frame.getContentPane().add(newButton, BorderLayout.SOUTH);

        frame.setBackground(new Color(60, 50, 90));
        fileMenu.setForeground(Color.black);
        fileMenu.setBackground(new Color(135, 206, 250));

        displayNotes(cardPanel);

        saveMenuItem.addActionListener(e -> saveNotes());

        frame.setVisible(true);
    }

    private static JDialog createLoadDialog(JPanel cardPanel) {
        JDialog loadDialog = new JDialog(frame, "Load Note", true);
        loadDialog.setSize(200, 200);
        loadDialog.setLayout(new BorderLayout());

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(0, 1));

        JButton loadAllButton = new JButton("Load All");
        loadAllButton.addActionListener(e -> {
            displayNotes(cardPanel);
            loadDialog.dispose();
        });

        optionsPanel.add(loadAllButton);

        for (Note note : notes) {
            JButton noteButton = new JButton(note.getName());
            noteButton.addActionListener(e -> {
                displayNoteByName(cardPanel, note.getName());
                loadDialog.dispose();
            });
            optionsPanel.add(noteButton);
        }

        loadDialog.add(optionsPanel, BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> loadDialog.dispose());
        loadDialog.add(cancelButton, BorderLayout.SOUTH);

        return loadDialog;
    }

    private static void displayNoteByName(JPanel cardPanel, String noteName) {
        Note note = null;
        try {
            note = loadNoteByName(noteName);
        } catch (NoteException e) {
            JOptionPane.showMessageDialog(frame, "Error loading note: " + e.getMessage());
        }

        if (note != null) {
            displayNote(cardPanel, note);
        }
    }

    private static Note loadNoteByName(String noteName) throws NoteException {
        String noteFileName = noteName + ".txt";
        File noteFile = new File(System.getProperty("user.dir"), noteFileName);
        Note note = null;

        try (FileReader reader = new FileReader(noteFile);
                BufferedReader bufferedReader = new BufferedReader(reader)) {
            String formattedDateTime = bufferedReader.readLine(); // Read the formatted date from the first line
            StringBuilder noteText = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                noteText.append(line).append("\n");
            }
            note = new Note();
            note.setName(noteName);
            note.setNoteCreationDate(formattedDateTime); // Set the formatted date
            note.setNote(noteText.toString());

        } catch (IOException e) {
            throw new NoteException("Error loading note: " + e.getMessage());
        }

        return note;
    }

    public static List<Note> loadNotes() throws NoteException {
        List<Note> notes = new ArrayList<>();

        // Get the directory path of the Java source files
        File directory = new File(System.getProperty("user.dir"));

        // Get all files in the directory with the .txt extension
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(reader)) {
                    String noteName = file.getName().replace(".txt", "");
                    String formattedDateTime = bufferedReader.readLine(); // Read the formatted date from the first line
                    StringBuilder noteContent = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        noteContent.append(line).append("\n");
                    }
                    if (noteContent.length() > 0) { // Check if the note content is not empty
                        Note note = new Note();
                        note.setName(noteName);
                        note.setNoteCreationDate(formattedDateTime); // Set the formatted date
                        note.setNote(noteContent.toString());
                        notes.add(note);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading note from file: " + file.getName() + " - " + e.getMessage());
                }
            }
        }

        return notes;
    }

    private static void saveNotes() {
        Thread saveThread = new Thread(() -> {
            try {
                for (Note note : notes) {
                    deleteNote(note);
                }
                // Create a .txt file for each note in the same directory as the code
                File directory = new File(System.getProperty("user.dir"));
                for (Note note : notes) {
                    String noteFileName = note.getName() + ".txt";
                    File noteFile = new File(directory, noteFileName);
                    try (FileWriter writer = new FileWriter(noteFile)) {
                        writer.write(note.getNoteCreationDate() + "\n");
                        writer.write(note.getNote());
                    } catch (IOException e) {
                        System.err.println("Error saving note: " + note.getName() + " - " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error saving notes: " + e.getMessage());
            }
        });
        saveThread.start();
    }

    private static void createNewNote(JPanel cardPanel) {
        JFrame newFrame = new JFrame("Create New Note");
        newFrame.setSize(400, 400);

        JPanel newPanel = new JPanel(new BorderLayout(5, 5));
        newPanel.setPreferredSize(new Dimension(400, 300));

        JTextField nameTextField = new JTextField();
        JTextArea newNoteArea = new JTextArea();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameTextField.getText();
                String noteText = newNoteArea.getText();
                createNoteWithText(name, noteText);
                newFrame.dispose(); // Close the new frame
                displayNotes(cardPanel);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFrame.dispose(); // Close the new frame
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        newPanel.add(nameTextField, BorderLayout.NORTH);
        newPanel.add(new JScrollPane(newNoteArea), BorderLayout.CENTER);
        newPanel.add(buttonPanel, BorderLayout.SOUTH);

        newFrame.getContentPane().add(newPanel);
        newFrame.setVisible(true);
    }

    private static void createNoteWithText(String name, String noteText) {
        try {
            Note note = new Note();
            note.setName(name);
            note.setNote(noteText);
            notes.add(note);
            saveNotes();
        } catch (NoteException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void deleteNoteFile(Note noteToDelete) {
        String fileName = noteToDelete.getName() + ".txt";
        File file = new File(fileName);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted file: " + fileName);
            } else {
                System.out.println("Failed to delete file: " + fileName);
            }
        } else {
            System.out.println("File not found: " + fileName);
        }
    }

    private static void deleteNote(Note noteToDelete) {
        for (Note note : notes) {
            String fileName = note.getName() + ".txt";
            File file = new File(fileName);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + fileName);
                } else {
                    System.out.println("Failed to delete file: " + fileName);
                }
            } else {
                System.out.println("File not found: " + fileName);
            }
        }
    }

    private static void deleteNote(JPanel cardPanel, Note noteToDelete) {
        if (noteToDelete == null) {
            JOptionPane.showMessageDialog(null, "Note not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        notes.remove(noteToDelete);
        String fileName = noteToDelete.getName() + ".txt";
        File file = new File(fileName);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted file: " + fileName);
            } else {
                System.out.println("Failed to delete file: " + fileName);
            }
        } else {
            System.out.println("File not found: " + fileName);
        }
    }

    private static void displayNotes(JPanel cardPanel) {
        cardPanel.removeAll();
        cardPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Adjust the spacing between cards as needed
        for (Note note : notes) {
            JPanel noteCardPanel = new JPanel();
            noteCardPanel.setPreferredSize(new Dimension(200, 100));
            noteCardPanel.setLayout(new BorderLayout());
            noteCardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border to the card

            // Note Name and Date Panel
            JPanel nameDatePanel = new JPanel();
            nameDatePanel.setLayout(new BorderLayout());

            JLabel nameLabel = new JLabel(note.getName());
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

            JLabel dateLabel = new JLabel(note.getNoteCreationDate().toString());
            dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            nameDatePanel.add(nameLabel, BorderLayout.WEST);
            nameDatePanel.add(dateLabel, BorderLayout.EAST);

            // Note Content Card
            JPanel contentCard = new JPanel();
            contentCard.setLayout(new BorderLayout());

            JTextArea noteArea = new JTextArea(note.getNote());
            noteArea.setPreferredSize(new Dimension(200, 150));
            noteArea.setLineWrap(true);
            noteArea.setWrapStyleWord(true);
            noteArea.setEditable(false); // Set the note area to non-editable

            contentCard.add(noteArea, BorderLayout.CENTER);

            ImageIcon editIcon = new ImageIcon("edit.png");
            Image editImage = editIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon editSmallIcon = new ImageIcon(editImage);
            JButton editButton = new JButton(editSmallIcon);
            editButton.setToolTipText("Edit");

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Create a new frame for editing the note

                    JFrame editFrame = new JFrame("Edit");
                    editFrame.setSize(400, 400);

                    JPanel editPanel = new JPanel(new BorderLayout(5, 5));
                    editPanel.setPreferredSize(new Dimension(400, 300));

                    JTextField nameTextField = new JTextField(note.getName());
                    JTextArea editNoteArea = new JTextArea(note.getNote());
                    JButton saveButton = new JButton("Save");
                    JButton cancelButton = new JButton("Cancel");

                    saveButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            deleteNoteFile(note);
                            note.setName(nameTextField.getText());
                            try {
                                note.setNote(editNoteArea.getText());
                            } catch (NoteException e1) {
                                e1.printStackTrace();
                            }
                            note.setNoteCreationDate();
                            saveNotes();
                            editFrame.dispose(); // Close the edit frame
                            displayNotes(cardPanel);
                        }
                    });

                    cancelButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            editFrame.dispose(); // Close the edit frame
                        }
                    });

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    buttonPanel.add(saveButton);
                    buttonPanel.add(cancelButton);

                    editPanel.add(nameTextField, BorderLayout.NORTH);
                    editPanel.add(new JScrollPane(editNoteArea), BorderLayout.CENTER);
                    editPanel.add(buttonPanel, BorderLayout.SOUTH);

                    editFrame.getContentPane().add(editPanel);
                    editFrame.setVisible(true);
                }
            });

            ImageIcon deleteIcon = new ImageIcon("delete.png");
            Image deleteImage = deleteIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            ImageIcon deleteSmallIcon = new ImageIcon(deleteImage);
            JButton deleteButton = new JButton(deleteSmallIcon);
            deleteButton.setToolTipText("Delete");

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this note?",
                            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        deleteNote(cardPanel, note);
                        displayNotes(cardPanel);
                    }
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            noteCardPanel.add(nameDatePanel, BorderLayout.NORTH);
            noteCardPanel.add(contentCard, BorderLayout.CENTER);
            noteCardPanel.add(buttonPanel, BorderLayout.SOUTH);
            cardPanel.add(noteCardPanel);
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private static void displayNote(JPanel cardPanel, Note note) {
        cardPanel.removeAll();
        cardPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Adjust the spacing between cards as needed

        JPanel noteCardPanel = new JPanel();
        noteCardPanel.setPreferredSize(new Dimension(200, 100));
        noteCardPanel.setLayout(new BorderLayout());
        noteCardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border to the card

        // Note Name and Date Panel
        JPanel nameDatePanel = new JPanel();
        nameDatePanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel(note.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel dateLabel = new JLabel(note.getNoteCreationDate().toString());
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        nameDatePanel.add(nameLabel, BorderLayout.WEST);
        nameDatePanel.add(dateLabel, BorderLayout.EAST);

        // Note Content Card
        JPanel contentCard = new JPanel();
        contentCard.setLayout(new BorderLayout());

        JTextArea noteArea = new JTextArea(note.getNote());
        noteArea.setPreferredSize(new Dimension(200, 150));
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setEditable(false); // Set the note area to non-editable

        contentCard.add(noteArea, BorderLayout.CENTER);

        ImageIcon editIcon = new ImageIcon("edit.png");
        Image editImage = editIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
        ImageIcon editSmallIcon = new ImageIcon(editImage);
        JButton editButton = new JButton(editSmallIcon);
        editButton.setToolTipText("Edit");

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new frame for editing the note

                JFrame editFrame = new JFrame("Edit");
                editFrame.setSize(400, 400);

                JPanel editPanel = new JPanel(new BorderLayout(5, 5));
                editPanel.setPreferredSize(new Dimension(400, 300));

                JTextField nameTextField = new JTextField(note.getName());
                JTextArea editNoteArea = new JTextArea(note.getNote());
                JButton saveButton = new JButton("Save");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteNoteFile(note);
                        note.setName(nameTextField.getText());
                        try {
                            note.setNote(editNoteArea.getText());
                        } catch (NoteException e1) {
                            e1.printStackTrace();
                        }
                        note.setNoteCreationDate();
                        saveNotes();
                        editFrame.dispose(); // Close the edit frame
                        displayNote(cardPanel, note);
                    }
                });

                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editFrame.dispose(); // Close the edit frame
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                editPanel.add(nameTextField, BorderLayout.NORTH);
                editPanel.add(new JScrollPane(editNoteArea), BorderLayout.CENTER);
                editPanel.add(buttonPanel, BorderLayout.SOUTH);

                editFrame.getContentPane().add(editPanel);
                editFrame.setVisible(true);
            }
        });

        ImageIcon deleteIcon = new ImageIcon("delete.png");
        Image deleteImage = deleteIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
        ImageIcon deleteSmallIcon = new ImageIcon(deleteImage);
        JButton deleteButton = new JButton(deleteSmallIcon);
        deleteButton.setToolTipText("Delete");

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this note?",
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    deleteNote(cardPanel, note);
                    displayNotes(cardPanel);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        noteCardPanel.add(nameDatePanel, BorderLayout.NORTH);
        noteCardPanel.add(contentCard, BorderLayout.CENTER);
        noteCardPanel.add(buttonPanel, BorderLayout.SOUTH);
        cardPanel.add(noteCardPanel);
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}