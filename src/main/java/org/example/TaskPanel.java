package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TaskPanel extends JPanel {
    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList = new JList<>(taskListModel);
    private JTextField taskField = new JTextField(15);
    private JTextField deadlineField = new JTextField(10); // format: yyyy-MM-dd
    private JButton addButton = new JButton("Dodaj zadanie");
    private JButton removeButton = new JButton("Usuń zaznaczone");
    private JButton toggleButton = new JButton("Zmień status");
    private JButton saveButton = new JButton("Zapisz do pliku");
    private JButton loadButton = new JButton("Wczytaj z pliku");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Zadanie:"));
        inputPanel.add(taskField);
        inputPanel.add(new JLabel("Deadline (yyyy-MM-dd):"));
        inputPanel.add(deadlineField);
        inputPanel.add(addButton);

        JPanel actionPanel = new JPanel();
        actionPanel.add(toggleButton);
        actionPanel.add(removeButton);
        actionPanel.add(saveButton);
        actionPanel.add(loadButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        toggleButton.addActionListener(e -> toggleTask());
        saveButton.addActionListener(e -> saveTasksToFile());
        loadButton.addActionListener(e -> loadTasksFromFile());
    }

    private void addTask() {
        String description = taskField.getText().trim();
        String deadlineText = deadlineField.getText().trim();

        if (!description.isEmpty() && !deadlineText.isEmpty()) {
            try {
                LocalDate deadline = LocalDate.parse(deadlineText, formatter);
                Task task = new Task(description, deadline);
                taskListModel.addElement(task);
                taskField.setText("");
                deadlineField.setText("");
                System.out.println("[TASK] Dodano zadanie: " + description + " z terminem: " + deadline);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Błędny format daty. Użyj formatu: yyyy-mm-dd.");
            }
        }
    }

    private void removeTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            System.out.println("[TASK] Usunięto zadanie: " + taskListModel.get(index).getDescription());
            taskListModel.remove(index);
        }
    }

    private void toggleTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            Task task = taskListModel.get(index);
            task.setCompleted(!task.isCompleted());
            taskList.repaint();
            System.out.println("[TASK] Zmieniono status zadania: " + task.getDescription());
        }
    }

    private void saveTasksToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < taskListModel.size(); i++) {
                    Task task = taskListModel.get(i);
                    writer.write(task.isCompleted() + ";" + task.getDescription() + ";" + task.getDeadline().format(formatter));
                    writer.newLine();
                }
                System.out.println("[TASK] Zapisano zadania do pliku: " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage());
            }
        }
    }

    private void loadTasksFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            taskListModel.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";", 3);
                    if (parts.length == 3) {
                        Task task = new Task(parts[1], LocalDate.parse(parts[2], formatter));
                        task.setCompleted(Boolean.parseBoolean(parts[0]));
                        taskListModel.addElement(task);
                    }
                }
                System.out.println("[TASK] Wczytano zadania z pliku: " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Błąd odczytu: " + e.getMessage());
            }
        }
    }
}

