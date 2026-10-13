# Smart Study Planner

A Java console application that helps students organize study tasks by deadline and difficulty. Tasks are saved in a local SQLite database, so they remain available after the app closes.

## Features
- Add tasks with a subject, title, deadline, and difficulty (1–5)
- Prioritize pending tasks using a `PriorityQueue`: earliest deadline first, then higher difficulty
- Mark tasks as completed
- View total, completed, and pending tasks plus completion percentage
- Store data persistently in SQLite

## Tech stack
- Java 17
- SQLite
- JDBC
- Maven
- Eclipse (optional IDE)

## Requirements
- JDK 17 or newer
- Maven (or Eclipse with Maven support)
- Internet access on the first Maven build so the SQLite JDBC dependency can be downloaded

## Run in Eclipse
1. Extract this ZIP file.
2. Open Eclipse.
3. Select **File → Import → Maven → Existing Maven Projects**.
4. Browse to the extracted `SmartStudyPlanner` folder and finish the import.
5. Wait for Maven to download dependencies.
6. Open `src/main/java/com/manvitha/studyplanner/StudyPlannerApp.java`.
7. Right-click the file and select **Run As → Java Application**.

## Run from a terminal
From the project folder, run:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.manvitha.studyplanner.StudyPlannerApp"
```

If your Maven setup does not have the exec plugin available, run the app from Eclipse as described above.

## Example task
- Subject: DSA
- Task title: Practice array problems
- Deadline: 2026-11-05
- Difficulty: 3

## Database
The app creates `study_planner.db` in the working directory the first time it runs. This local database is generated at runtime and should not be committed to GitHub.

## Suggested GitHub repository description
A Java + SQLite study planner that prioritizes study tasks by deadline and difficulty, tracks completion, and stores tasks locally.

## Before publishing
- Run the program and test adding, viewing, and completing tasks.
- Add screenshots of the running app to a `screenshots/` folder if you want.
- Update this README with your own screenshots and any improvements you implement.
