package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class TaskPanel extends JPanel {
    private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
    private JList<Task> taskList = new JList<>(taskListModel);
    private JTextField taskField = new JTextField(20);
    private JButton addButton = new JButton("Dodaj zadanie");
    private JButton removeButton = new JButton("Usuń zaznaczone");
    private JButton toggleButton = new JButton("Zmień status");
    private JButton saveButton = new JButton("Zapisz do pliku");
    private JButton loadButton = new JButton("Wczytaj z pliku");

    public TaskPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(taskField);
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
        String text = taskField.getText().trim();
        if (!text.isEmpty()) {
            Task task = new Task(text);
            taskListModel.addElement(task);
            taskField.setText("");
            System.out.println("[TASK] Dodano zadanie: " + text);
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
                    writer.write(task.isCompleted() + ";" + task.getDescription());
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
                    String[] parts = line.split(";", 2);
                    if (parts.length == 2) {
                        Task task = new Task(parts[1]);
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
