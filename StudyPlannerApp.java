package com.manvitha.studyplanner;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class StudyPlannerApp {
    private static final String DB_URL = "jdbc:sqlite:study_planner.db";
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        StudyPlannerApp app = new StudyPlannerApp();
        try {
            app.initializeDatabase();
            app.run();
        } catch (SQLException e) {
            System.err.println("Could not open the database: " + e.getMessage());
            System.err.println("If you are using Eclipse, import this project as a Maven project and allow Maven to download dependencies.");
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                subject TEXT NOT NULL,
                title TEXT NOT NULL,
                deadline TEXT NOT NULL,
                difficulty INTEGER NOT NULL CHECK (difficulty BETWEEN 1 AND 5),
                completed INTEGER NOT NULL DEFAULT 0
            )
            """;
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void run() {
        System.out.println("\n==================================");
        System.out.println("       SMART STUDY PLANNER");
        System.out.println("==================================");
        boolean running = true;
        while (running) {
            System.out.println("\n1. Add study task");
            System.out.println("2. View tasks by priority");
            System.out.println("3. Mark task as completed");
            System.out.println("4. View progress summary");
            System.out.println("5. Exit");
            int choice = readInt("Choose an option: ", 1, 5);
            try {
                switch (choice) {
                    case 1 -> addTask();
                    case 2 -> viewTasks();
                    case 3 -> completeTask();
                    case 4 -> showSummary();
                    case 5 -> {
                        running = false;
                        System.out.println("Good luck with your studies!");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Database operation failed: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void addTask() throws SQLException {
        System.out.println("\n--- Add a Study Task ---");
        String subject = readNonBlank("Subject (e.g., DSA): ");
        String title = readNonBlank("Task title: ");
        LocalDate deadline = readDate("Deadline (YYYY-MM-DD): ");
        int difficulty = readInt("Difficulty (1 = easy, 5 = hard): ", 1, 5);

        String sql = "INSERT INTO tasks(subject, title, deadline, difficulty) VALUES (?, ?, ?, ?)";
        try (Connection connection = connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, subject);
            ps.setString(2, title);
            ps.setString(3, deadline.toString());
            ps.setInt(4, difficulty);
            ps.executeUpdate();
        }
        System.out.println("Task added successfully.");
    }

    private void viewTasks() throws SQLException {
        List<StudyTask> tasks = getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks yet. Add your first study task!");
            return;
        }

        // PriorityQueue orders pending tasks by earliest deadline, then higher difficulty.
        PriorityQueue<StudyTask> queue = new PriorityQueue<>(
            Comparator.comparing(StudyTask::getDeadline)
                .thenComparing(Comparator.comparingInt(StudyTask::getDifficulty).reversed())
                .thenComparingInt(StudyTask::getId)
        );
        for (StudyTask task : tasks) {
            if (!task.isCompleted()) queue.offer(task);
        }

        System.out.println("\n--- Pending Tasks (highest priority first) ---");
        if (queue.isEmpty()) System.out.println("All tasks are completed!");
        while (!queue.isEmpty()) System.out.println(queue.poll());

        System.out.println("\n--- Completed Tasks ---");
        tasks.stream().filter(StudyTask::isCompleted).forEach(System.out::println);
    }

    private List<StudyTask> getTasks() throws SQLException {
        List<StudyTask> tasks = new ArrayList<>();
        String sql = "SELECT id, subject, title, deadline, difficulty, completed FROM tasks";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(new StudyTask(
                    rs.getInt("id"),
                    rs.getString("subject"),
                    rs.getString("title"),
                    LocalDate.parse(rs.getString("deadline")),
                    rs.getInt("difficulty"),
                    rs.getInt("completed") == 1
                ));
            }
        }
        return tasks;
    }

    private void completeTask() throws SQLException {
        List<StudyTask> tasks = getTasks();
        if (tasks.isEmpty()) {
            System.out.println("There are no tasks to complete yet.");
            return;
        }
        viewTasks();
        int id = readInt("Enter the task ID to mark completed: ", 1, Integer.MAX_VALUE);
        String sql = "UPDATE tasks SET completed = 1 WHERE id = ? AND completed = 0";
        try (Connection connection = connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int updated = ps.executeUpdate();
            System.out.println(updated == 1
                ? "Task marked as completed!"
                : "No pending task found with that ID.");
        }
    }

    private void showSummary() throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total,
                   SUM(CASE WHEN completed = 1 THEN 1 ELSE 0 END) AS done
            FROM tasks
            """;
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            int total = rs.getInt("total");
            int done = rs.getInt("done");
            int pending = total - done;
            double percentage = total == 0 ? 0 : (done * 100.0 / total);
            System.out.println("\n--- Study Progress ---");
            System.out.println("Total tasks     : " + total);
            System.out.println("Completed tasks: " + done);
            System.out.println("Pending tasks  : " + pending);
            System.out.printf("Completion rate: %.1f%%%n", percentage);
        }
    }

    private String readNonBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Please enter a valid date, for example 2026-11-05.");
            }
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {
                // Show the same validation message below.
            }
            System.out.println("Enter a whole number between " + min + " and " + max + ".");
        }
    }
}
