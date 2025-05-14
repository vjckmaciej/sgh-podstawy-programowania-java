package org.example;

public class Grade {
    private String subject;
    private String type; // np. "kolokwium", "egzamin"
    private double value;

    public Grade(String subject, String type, double value) {
        this.subject = subject;
        this.type = type;
        this.value = value;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return subject + " (" + type + "): " + value;
    }
}
