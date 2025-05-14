package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class GradePanel extends JPanel {
    private DefaultListModel<Grade> gradeListModel = new DefaultListModel<>();
    private JList<Grade> gradeList = new JList<>(gradeListModel);
    private JTextField subjectField = new JTextField(10);
    private JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Kolokwium", "Projekt", "Inne"});
    private JComboBox<Double> valueComboBox = new JComboBox<>(new Double[]{2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0});
    private JButton addButton = new JButton("Dodaj ocenę");
    private JButton removeButton = new JButton("Usuń zaznaczoną");
    private JButton averageButton = new JButton("Oblicz średnią");
    private JButton saveButton = new JButton("Zapisz do pliku");
    private JButton loadButton = new JButton("Wczytaj z pliku");

    public GradePanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fieldsPanel.add(new JLabel("Przedmiot:"));
        fieldsPanel.add(subjectField);
        fieldsPanel.add(new JLabel("Typ:"));
        fieldsPanel.add(typeComboBox);
        fieldsPanel.add(new JLabel("Ocena:"));
        fieldsPanel.add(valueComboBox);

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButtonPanel.add(addButton);

        inputPanel.add(fieldsPanel);
        inputPanel.add(addButtonPanel);

        JPanel actionPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        actionPanel.add(removeButton);
        actionPanel.add(averageButton);
        actionPanel.add(saveButton);
        actionPanel.add(loadButton);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        listPanel.add(new JScrollPane(gradeList), BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gradeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Grade) {
                    Grade grade = (Grade) value;
                    setText(grade.getSubject() + " - " + grade.getType() + ": " + grade.getValue());
                }
                return this;
            }
        });

        addButton.addActionListener(e -> addGrade());
        removeButton.addActionListener(e -> removeGrade());
        averageButton.addActionListener(e -> showAverage());
        saveButton.addActionListener(e -> saveGradesToFile());
        loadButton.addActionListener(e -> loadGradesFromFile());
    }

    private void addGrade() {
        String subject = subjectField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        Double value = (Double) valueComboBox.getSelectedItem();

        if (!subject.isEmpty() && type != null && value != null) {
            Grade grade = new Grade(subject, type, value);
            gradeListModel.addElement(grade);
            subjectField.setText("");
            System.out.println("[GRADE] Dodano ocenę: " + grade);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Uzupełnij wszystkie pola.",
                    "Brak danych",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeGrade() {
        int index = gradeList.getSelectedIndex();
        if (index != -1) {
            System.out.println("[GRADE] Usunięto ocenę: " + gradeListModel.get(index));
            gradeListModel.remove(index);
        }
    }

    private void showAverage() {
        if (gradeListModel.size() == 0) {
            JOptionPane.showMessageDialog(this, "Brak ocen.");
            return;
        }
        double sum = 0;
        for (int i = 0; i < gradeListModel.size(); i++) {
            sum += gradeListModel.get(i).getValue();
        }
        double avg = sum / gradeListModel.size();
        JOptionPane.showMessageDialog(this, "Średnia ocen: " + String.format("%.2f", avg));
    }

    private void saveGradesToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (int i = 0; i < gradeListModel.size(); i++) {
                    Grade grade = gradeListModel.get(i);
                    writer.write(grade.getSubject() + ";" + grade.getType() + ";" + grade.getValue());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this,
                        "Pomyślnie zapisano dane do pliku: " + file.getName(),
                        "Zapis zakończony",
                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("[GRADE] Zapisano oceny do pliku: " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Błąd zapisu: " + e.getMessage(),
                        "Błąd operacji",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadGradesFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            gradeListModel.clear();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    String[] parts = line.split(";", 3);
                    if (parts.length == 3) {
                        try {
                            String subject = parts[0];
                            String type = parts[1];
                            double value = Double.parseDouble(parts[2]);

                            // Walidacja wartości oceny podczas wczytywania
                            if (value < 2 || value > 5) {
                                System.out.println("[GRADE] Ostrzeżenie: Pominięto nieprawidłową ocenę w linii " +
                                        lineCount + ": wartość poza zakresem 2-5");
                                continue;
                            }

                            Grade grade = new Grade(subject, type, value);
                            gradeListModel.addElement(grade);
                        } catch (NumberFormatException e) {
                            System.out.println("[GRADE] Błąd: Nieprawidłowy format liczby w linii " + lineCount);
                        }
                    } else {
                        System.out.println("[GRADE] Ostrzeżenie: Pominięto nieprawidłowy format danych w linii " + lineCount);
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "Pomyślnie wczytano " + gradeListModel.size() + " ocen z pliku.",
                        "Odczyt zakończony",
                        JOptionPane.INFORMATION_MESSAGE);
                System.out.println("[GRADE] Wczytano oceny z pliku: " + file.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Błąd odczytu: " + e.getMessage(),
                        "Błąd operacji",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
