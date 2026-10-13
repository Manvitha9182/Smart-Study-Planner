package com.manvitha.studyplanner;

import java.time.LocalDate;

public class StudyTask {
    private final int id;
    private final String subject;
    private final String title;
    private final LocalDate deadline;
    private final int difficulty;
    private final boolean completed;

    public StudyTask(int id, String subject, String title, LocalDate deadline,
                     int difficulty, boolean completed) {
        this.id = id;
        this.subject = subject;
        this.title = title;
        this.deadline = deadline;
        this.difficulty = difficulty;
        this.completed = completed;
    }

    public int getId() { return id; }
    public String getSubject() { return subject; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public int getDifficulty() { return difficulty; }
    public boolean isCompleted() { return completed; }

    @Override
    public String toString() {
        String status = completed ? "DONE" : "PENDING";
        return String.format("#%-3d %-10s %-25s Due: %s | Difficulty: %d/5 | %s",
                id, subject, title, deadline, difficulty, status);
    }
}
