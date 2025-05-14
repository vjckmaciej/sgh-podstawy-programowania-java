
package org.example;

import java.time.LocalDate;

public class Task {
    private String description;
    private boolean completed;
    private LocalDate deadline;

    public Task(String description, LocalDate deadline) {
        this.description = description;
        this.deadline = deadline;
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return (completed ? "[X] " : "[ ] ") + description + " (do: " + deadline + ")";
    }
}