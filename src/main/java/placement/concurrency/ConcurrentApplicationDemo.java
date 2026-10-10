
package placement.concurrency;

import placement.dao.JobDAO;
import placement.dao.StudentDAO;
import placement.model.Job;
import placement.model.Student;
import placement.service.ApplicationService;

import java.util.List;
import java.util.ArrayList;

public class ConcurrentApplicationDemo {

    public static void main(String[] args) {

        StudentDAO studentDAO = new StudentDAO();
        JobDAO jobDAO = new JobDAO();
        ApplicationService applicationService =
                new ApplicationService();

        String[] usernames = {
                "rahul1101",
                "aman101",
                "tejasvi121",
                "devansh",
                "tripti"
        };

        List<Student> students = new ArrayList<>();

        for (String username : usernames) {
            Student student = studentDAO.findByUsername(username);

            if (student != null) {
                students.add(student);
            } else {
                System.out.println(
                        "Student not found: " + username
                );
            }
        }

        List<Job> jobs = jobDAO.findAllJobs();

        if (students.isEmpty() || jobs.isEmpty()) {
            System.out.println(
                    "Demo cannot run: students or jobs are unavailable."
            );
            return;
        }

        Job selectedJob = jobs.get(0);

        System.out.println("\n===== CONCURRENT APPLICATION DEMO =====");
        System.out.println("Selected job: " + selectedJob.getJobTitle());
        System.out.println("Students loaded: " + students.size());

        List<Thread> threads = new ArrayList<>();


        for (Student student : students) {
            Thread thread = new Thread(
                    new ApplicationTask(
                            student,
                            selectedJob,
                            applicationService
                    ),
                    "Application-" + student.getUsername()
            );

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Demo interrupted.");
                return;
            }
        }
        
    System.out.println("\n===== REENTRANTLOCK DEMO =====");

    PlacementProcessingStats stats =
            new PlacementProcessingStats();

    Thread statsThread1 = new Thread(() -> {
        for (int i = 0; i < 3; i++) {
            stats.recordProcessedApplication();
        }
    }, "Stats-Thread-1");

    Thread statsThread2 = new Thread(() -> {
        for (int i = 0; i < 3; i++) {
            stats.recordProcessedApplication();
        }
    }, "Stats-Thread-2");

    statsThread1.start();
    statsThread2.start();

    try {
        statsThread1.join();
        statsThread2.join();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println("Statistics demo interrupted.");
        return;
    }

    System.out.println(
            "Final processed count: "
            + stats.getApplicationsProcessed()
    );


        System.out.println("\n===== DEMO COMPLETED =====");
        System.out.println(
            "Successfully processed applications: "
            + applicationService.getTotalApplicationsProcessed()
        );
    }

}
