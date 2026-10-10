
package placement.concurrency;

import placement.model.Student;
import placement.service.AuthenticationService;
import placement.dao.StudentDAO;
import java.util.concurrent.CountDownLatch;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentRegistrationDemo {

    public static void main(String[] args) {

        AuthenticationService authService =
                new AuthenticationService();

        StudentDAO studentDAO = new StudentDAO();

        String[] usernames = {
                "rahul1101",
                "aman101",
                "tejasvi121",
                "devansh",
                "tripti"
        };

        List<Thread> threads = new ArrayList<>();

        System.out.println(
                "\n===== CONCURRENT LOGIN DEMO ====="
        );

        for (String username : usernames) {

            Thread thread = new Thread(() -> {

                Student student =
                        studentDAO.findByUsername(username);

                if (student == null) {
                    System.out.println(
                            Thread.currentThread().getName()
                            + ": Student not found: " + username
                    );
                    return;
                }

                Student loggedIn =
                        authService.loginStudent(
                                username,
                                student.getPasswordHash()
                        );

                System.out.println(
                        Thread.currentThread().getName()
                        + ": Login "
                        + (loggedIn != null ? "successful" : "failed")
                );

            }, "Login-" + username);

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Login demo interrupted.");
                return;
            }
        }

        System.out.println(
                "===== CONCURRENT LOGIN DEMO COMPLETED ====="
        );
        try {
            concurrentRegistrationDemo();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Registration demo interrupted.");
        }
    }


    private static void concurrentRegistrationDemo()
            throws InterruptedException {

        AuthenticationService authService =
                new AuthenticationService();

        int numberOfThreads = 5;

        // Use a username that does not already exist in your database.
        String sharedUsername = "concurrent_test_user";

        CountDownLatch startSignal = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        System.out.println("\n===== CONCURRENT REGISTRATION DEMO =====");

        for (int i = 1; i <= numberOfThreads; i++) {

            final int threadNumber = i;

            Thread thread = new Thread(() -> {
                try {
                    // All threads wait here until released.
                    startSignal.await();

                    Student student = new Student(
                            sharedUsername,
                            "demoPassword",
                            "Test Student " + threadNumber,
                            "concurrent" + threadNumber + "@example.com",
                            8.0,
                            "CSE"
                    );

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " attempting registration..."
                    );

                    authService.registerStudent(student);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }, "Register-" + threadNumber);

            threads.add(thread);
            thread.start();
        }

        // Release all registration threads together.
        startSignal.countDown();

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println(
                "===== CONCURRENT REGISTRATION DEMO COMPLETED ====="
        );
    }

}
