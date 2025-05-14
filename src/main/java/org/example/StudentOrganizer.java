package org.example;

import javax.swing.*;
import java.awt.*;

public class StudentOrganizer extends JFrame {
    private TaskPanel taskPanel;
    private GradePanel gradePanel;

    public StudentOrganizer() {
        super("Organizer Studenta");
        System.out.println("[INIT] Tworzenie głównego okna aplikacji");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        System.out.println("[INIT] Inicjalizacja paneli zadań i ocen");
        taskPanel = new TaskPanel();
        gradePanel = new GradePanel();

        tabbedPane.addTab("Zadania", taskPanel);
        tabbedPane.addTab("Oceny", gradePanel);

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        System.out.println("[INIT] Aplikacja uruchomiona i widoczna");
    }

    public static void main(String[] args) {
        System.out.println("[MAIN] Uruchamianie aplikacji Organizer Studenta");
        SwingUtilities.invokeLater(StudentOrganizer::new);
    }
}