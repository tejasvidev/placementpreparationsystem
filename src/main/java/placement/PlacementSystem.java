package placement;

import placement.dao.ApplicationDAO;
import placement.model.Application;
import placement.model.ApplicationStatus;
import placement.model.Company;
import placement.model.Student;
import placement.service.AuthenticationService;
import placement.model.Job;
import placement.service.JobService;
import java.util.List;
import java.util.Scanner;
import java.util.Map;
import placement.model.ApplicationStatus;
import placement.model.Interview;
import placement.service.InterviewService;
import placement.concurrency.WelcomeThread;
import placement.concurrency.ApplicationTask;
import placement.concurrency.PlacementProcessingStats;
import placement.concurrency.PlacementStatsDemo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import placement.service.ApplicationService;

public class PlacementSystem {
    private static final ApplicationService applicationService =
        new ApplicationService();
    private static final InterviewService interviewService =
        new InterviewService();
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthenticationService authenticationService =
            new AuthenticationService();

    private static final JobService jobService = new JobService();
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("      PLACEMENT MANAGEMENT");
            System.out.println("================================");
            System.out.println("1. Student");
            System.out.println("2. Company");
            System.out.println("3. Concurrency Demonstration");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    studentMenu(scanner);
                    break;

                case 2:
                    companyMenu(scanner);
                    break;

                case 3:
                    concurrencyMenu(scanner);
                    break;

                case 4:
                    System.out.println("Exiting application...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


    // =========================================================
    // STUDENT MENU
    // =========================================================

    private static void studentMenu(Scanner scanner) {

        while (true) {

            System.out.println();
            System.out.println("========== STUDENT ==========");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    Student student = studentLogin(scanner);

                    if (student != null) {
                        studentDashboard(scanner, student);
                    }

                    break;

                case 2:

                    boolean registered =
                            studentRegistration(scanner);

                    if (registered) {

                        System.out.println();
                        System.out.println(
                                "Please login to continue."
                        );

                        studentLogin(scanner);
                    }

                    break;

                case 3:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


    // =========================================================
    // STUDENT LOGIN
    // =========================================================

    private static Student studentLogin(Scanner scanner) {

        System.out.println();
        System.out.println("========== STUDENT LOGIN ==========");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Student student =
                authenticationService.loginStudent(
                        username,
                        password
                );

        if (student != null) {
            System.out.println();
            Thread welcomeThread =
                new WelcomeThread(student.getUsername());

            welcomeThread.start();

            return student;
        }

        return null;
    }


    // =========================================================
    // STUDENT REGISTRATION
    // =========================================================

    
    private static boolean studentRegistration(Scanner scanner) {

        try {
            System.out.println();
            System.out.println("====== STUDENT REGISTRATION ======");

            String username = readValidatedUsername(scanner);
            String password = readValidatedPassword(scanner);
            String name = readNonEmptyString(scanner, "Name: ");
            String email = readNonEmptyString(scanner, "Email: ");
            double cgpa = readValidatedCgpa(scanner);
            String branch = readNonEmptyString(scanner, "Branch: ");

            if (authenticationService.usernameExists(username)) {
                System.out.println("Username already exists.");
                return false;
            }

            Student student = new Student(
                    username,
                    password,
                    name,
                    email,
                    cgpa,
                    branch
            );

            authenticationService.registerStudent(student);

            return true;

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Invalid student details: " + e.getMessage()
            );
            return false;

        } catch (Exception e) {
            System.out.println(
                    "Student registration failed. Please try again."
            );
            e.printStackTrace();
            return false;
        }
    }





    // =========================================================
    // STUDENT DASHBOARD
    // =========================================================

    private static void studentDashboard(
            Scanner scanner,
            Student student) {

        while (true) {

            System.out.println();
            System.out.println(
                    "========== STUDENT DASHBOARD =========="
            );

            System.out.println(
                    "Welcome, " + student.getName()
            );

            System.out.println();
            System.out.println("1. View Available Jobs");
            System.out.println("2. Submit Student Application");
            System.out.println("3. View Interview Schedule");
            System.out.println("4. Concurrent Application Demo");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    viewAvailableJobs();
                    break;

                case 2:
                    submitStudentApplication(student);
                    break;

                case 3:
                    viewInterviewSchedule(student);
                    break;
                
                case 4:
                    concurrentApplicationDemo(student);
                    break;

                case 5:
                    System.out.println(
                            "Logging out..."
                    );
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }


    // =========================================================
    // STUDENT OPERATIONS
    // =========================================================

    
    private static void viewAvailableJobs() {

        System.out.println();
        System.out.println("========== AVAILABLE JOBS ==========");

        List<Job> jobs = jobService.getAvailableJobs();

        if (jobs.isEmpty()) {
            System.out.println("No jobs are currently available.");
            return;
        }

        System.out.printf(
                "%-8s %-25s %-12s %-25s %-20s%n",
                "Job ID", "Job Title", "Min CGPA",
                "Eligible Branch", "Location"
        );

        System.out.println(
                "------------------------------------------------------------------------------------------"
        );

        for (Job job : jobs) {
            System.out.printf(
                    "%-8d %-25s %-12.2f %-25s %-20s%n",
                    job.getJobId(),
                    job.getJobTitle(),
                    job.getMinimumCgpa(),
                    job.getEligibleBranch(),
                    job.getLocation()
            );
        }
    }


    
    private static void submitStudentApplication(Student student) {

        if (student == null) {
            System.out.println("Please log in as a student first.");
            return;
        }

        List<Job> jobs = jobService.getAvailableJobs();

        if (jobs == null || jobs.isEmpty()) {
            System.out.println("No jobs are currently available.");
            return;
        }

        System.out.println("\n========== AVAILABLE JOBS ==========");

        for (Job job : jobs) {
            System.out.println(
                    "Job ID: " + job.getJobId()
                    + " | Title: " + job.getJobTitle()
                    + " | Minimum CGPA: " + job.getMinimumCgpa()
                    + " | Eligible Branch: " + job.getEligibleBranch()
                    + " | Location: " + job.getLocation()
            );
        }

        System.out.print("\nEnter the Job ID to apply for: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Invalid Job ID. Please enter a number.");
            scanner.nextLine();
            return;
        }

        int jobId = scanner.nextInt();
        scanner.nextLine();

        Job selectedJob = null;

        for (Job job : jobs) {
            if (job.getJobId() == jobId) {
                selectedJob = job;
                break;
            }
        }

        if (selectedJob == null) {
            System.out.println("Job not found.");
            return;
        }

        // Check CGPA eligibility.
        if (student.getCgpa() < selectedJob.getMinimumCgpa()) {
            System.out.println("You are not eligible: your CGPA is below the requirement.");
            return;
        }

        // Supports a specific eligible branch or ALL branches.
        String eligibleBranch = selectedJob.getEligibleBranch();

        if (eligibleBranch != null
                && !eligibleBranch.equalsIgnoreCase("ALL")
                && !eligibleBranch.equalsIgnoreCase(student.getBranch())) {
            System.out.println("You are not eligible for this job's branch requirement.");
            return;
        }

        ApplicationDAO applicationDAO = new ApplicationDAO();

        // Prevent duplicate applications.
        if (applicationDAO.hasStudentApplied(
                student.getStudentId(),
                selectedJob.getJobId())) {

            System.out.println("You have already applied for this job.");
            return;
        }

        Application application = new Application(student, selectedJob);

        int applicationId = applicationService.submitApplication(application);

        if (applicationId != -1) {
            System.out.println("Application submitted successfully!");
            System.out.println("Application ID: " + applicationId);
            System.out.println("Status: " + application.getStatus());
        } else {
            System.out.println("Application submission failed.");
        }
    }




    private static void viewInterviewSchedule(Student student) {

        if (student == null) {
            System.out.println("Please log in as a student first.");
            return;
        }

        List<Interview> interviews =
                interviewService.getStudentInterviewsChronologically(student.getStudentId());

        if (interviews.isEmpty()) {
            System.out.println("No interviews scheduled for you yet.");
            return;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");

        System.out.println("\n========== YOUR INTERVIEW SCHEDULE ==========");

        for (Interview interview : interviews) {

            System.out.println("---------------------------------------------");
            System.out.println("Interview ID : " + interview.getInterviewId());
            System.out.println("Job Title    : "
                    + interview.getApplication().getJob().getJobTitle());
            System.out.println("Interview Type: " + interview.getInterviewType());
            System.out.println("Scheduled At : "
                    + interview.getScheduledAt().format(formatter));
            System.out.println("Status       : " + interview.getStatus());
        }

        System.out.println("---------------------------------------------");
    }

    private static void concurrentApplicationDemo(Student student) {

        if (student == null) {
            System.out.println("Please log in first.");
            return;
        }

        List<Job> jobs = jobService.getAvailableJobs();

        if (jobs == null || jobs.isEmpty()) {
            System.out.println("No jobs are currently available.");
            return;
        }

        System.out.println("\n===== CONCURRENT APPLICATION DEMO =====");
        System.out.println("Logged-in student: " + student.getName());

        for (Job job : jobs) {
            System.out.println(
                    "Job ID: " + job.getJobId()
                    + " | Title: " + job.getJobTitle()
            );
        }

        System.out.print("Enter Job ID: ");
        int jobId = readInt(scanner);

        Job selectedJob = null;

        for (Job job : jobs) {
            if (job.getJobId() == jobId) {
                selectedJob = job;
                break;
            }
        }

        if (selectedJob == null) {
            System.out.println("Invalid Job ID.");
            return;
        }

        final Job job = selectedJob;
        final int numberOfRequests = 3;

        List<Thread> threads = new java.util.ArrayList<>();

        System.out.println(
                "\nLaunching " + numberOfRequests
                + " concurrent application requests..."
        );

        for (int i = 1; i <= numberOfRequests; i++) {

            Thread thread = new Thread(
                    new ApplicationTask(student, job, applicationService),
                    "Application-Thread-" + i
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

        System.out.println(
                "\nAll concurrent application requests have finished."
        );

        System.out.println(
                "Check the company application list to verify "
                + "that no duplicate application was inserted."
        );

        System.out.println("========================================");
    }


    // =========================================================
    // COMPANY MENU
    // =========================================================

    private static void companyMenu(Scanner scanner) {

        while (true) {

            System.out.println();
            System.out.println("========== COMPANY ==========");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:

                    Company company =
                            companyLogin(scanner);

                    if (company != null) {
                        companyDashboard(
                                scanner,
                                company
                        );
                    }

                    break;

                case 2:

                    boolean registered =
                            companyRegistration(scanner);

                    if (registered) {

                        System.out.println();
                        System.out.println(
                                "Please login to continue."
                        );

                        companyLogin(scanner);
                    }

                    break;

                case 3:
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }


    // =========================================================
    // COMPANY LOGIN
    // =========================================================

    private static Company companyLogin(
            Scanner scanner) {

        System.out.println();
        System.out.println(
                "========== COMPANY LOGIN =========="
        );

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Company company =
                authenticationService.loginCompany(
                        username,
                        password
                );

        if (company != null) {

            System.out.println();
            System.out.println(
                    "Welcome, " + company.getName()
            );

            System.out.println(
                    "Industry: " + company.getIndustry()
            );

            return company;
        }

        return null;
    }


    // =========================================================
    // COMPANY REGISTRATION
    // =========================================================

    
    private static boolean companyRegistration(Scanner scanner) {

        try {
            System.out.println();
            System.out.println("====== COMPANY REGISTRATION ======");

            String username = readValidatedUsername(scanner);
            String password = readValidatedPassword(scanner);
            String name = readNonEmptyString(
                    scanner, "Company Name: "
            );
            String email = readNonEmptyString(scanner, "Email: ");
            String industry = readNonEmptyString(
                    scanner, "Industry: "
            );

            if (authenticationService.companyUsernameExists(username)) {
                System.out.println(
                        "Company username already exists."
                );
                return false;
            }

            Company company = new Company(
                    username,
                    password,
                    name,
                    email,
                    industry
            );

            authenticationService.registerCompany(company);

            return true;

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Invalid company details: " + e.getMessage()
            );
            return false;

        } catch (Exception e) {
            System.out.println(
                    "Company registration failed. Please try again."
            );
            e.printStackTrace();
            return false;
        }
    }


    // =========================================================
    // COMPANY DASHBOARD
    // =========================================================

    private static void companyDashboard(
            Scanner scanner,
            Company company) {

        while (true) {

            System.out.println();
            System.out.println(
                    "========== COMPANY DASHBOARD =========="
            );

            System.out.println(
                    "Welcome, " + company.getName()
            );

            System.out.println();
            System.out.println("1. Post Job");
            System.out.println("2. View Applications");
            System.out.println("3. Schedule Interview");
            System.out.println(
                    "4. Update Application Status"
            );
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {

                case 1:
                    postJob(company);
                    break;

                case 2:
                    viewApplications(company);
                    break;

                case 3:
                    scheduleInterview(scanner, company);
                    break;

                case 4:
                    updateApplicationStatus(scanner, company);
                    break;

                case 5:
                    System.out.println(
                            "Logging out..."
                    );
                    return;

                default:
                    System.out.println(
                            "Invalid choice."
                    );
            }
        }
    }


    // =========================================================
    // COMPANY OPERATIONS
    // =========================================================


    private static void postJob(Company company) {

        System.out.println("\n========== POST JOB ==========");

        try {
            System.out.print("Job Title: ");
            String jobTitle = scanner.nextLine().trim();

            if (jobTitle.isEmpty()) {
                System.out.println("Job title cannot be empty.");
                return;
            }

            double minimumCgpa = readValidatedCgpa(scanner);

            System.out.print("Eligible Branch (CSE/ECE/ALL, etc.): ");
            String eligibleBranch = scanner.nextLine().trim();

            if (eligibleBranch.isEmpty()) {
                System.out.println("Eligible branch cannot be empty.");
                return;
            }

            System.out.print("Job Location: ");
            String location = scanner.nextLine().trim();

            if (location.isEmpty()) {
                System.out.println("Job location cannot be empty.");
                return;
            }

            // Temporary ID because the current Job constructor requires a positive ID.
            // JobDAO should omit job_id so MySQL can generate the actual ID.
            Job job = new Job(
                    1,
                    jobTitle,
                    minimumCgpa,
                    eligibleBranch,
                    location
            );

            boolean success = jobService.postJob(job,company.getCompanyId());

            if (!success) {
                System.out.println("Could not post the job. Please try again.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid job details: " + e.getMessage());
        }
    }

    private static void viewApplications(Company company) {

        System.out.println("\n========== YOUR JOB APPLICATIONS ==========");

        if (company == null) {
            System.out.println("Please log in as a company first.");
            return;
        }

        List<Application> applications =
                applicationService.getApplicationsSortedById(
                        company.getCompanyId()
                );

        if (applications.isEmpty()) {
            System.out.println("No applications found for your jobs.");
            return;
        }

        System.out.printf(
                "%-8s %-20s %-25s %-8s %-15s %-15s%n",
                "App ID", "Student", "Job Title",
                "CGPA", "Branch", "Status"
        );

        System.out.println(
                "------------------------------------------------------------------------------------------"
        );

        for (Application application : applications) {

            Student student = application.getStudent();
            Job job = application.getJob();

            System.out.printf(
                    "%-8d %-20s %-25s %-8.2f %-15s %-15s%n",
                    application.getApplicationId(),
                    student.getName(),
                    job.getJobTitle(),
                    student.getCgpa(),
                    student.getBranch(),
                    application.getStatus()
            );
        }
        
        System.out.println("\n===== APPLICATION STATUS SUMMARY =====");

        Map<ApplicationStatus, Integer> statusCounts =
                applicationService.getApplicationStatusCounts(
                        company.getCompanyId()
                );

        for (ApplicationStatus status : ApplicationStatus.values()) {
            System.out.println(
                    status + " : " + statusCounts.getOrDefault(status, 0)
            );
        }
    }





    private static void scheduleInterview(
            Scanner scanner, Company company) {

        System.out.println("\n========== SCHEDULE INTERVIEW ==========");

        if (company == null) {
            System.out.println("Please log in as a company first.");
            return;
        }

        System.out.print("Application ID: ");
        int applicationId = readInt(scanner);

        System.out.print("Interview Type (Technical/HR/Managerial): ");
        String interviewType = scanner.nextLine().trim();

        if (interviewType.isEmpty()) {
            System.out.println("Interview type cannot be empty.");
            return;
        }

        System.out.println("Enter date and time in format yyyy-MM-dd HH:mm");
        System.out.print("Scheduled Date and Time: ");

        String dateTimeInput = scanner.nextLine().trim();

        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime scheduledAt =
                    LocalDateTime.parse(dateTimeInput, formatter);

            boolean success = interviewService.scheduleInterview(
                    applicationId,
                    company.getCompanyId(),
                    interviewType,
                    scheduledAt
            );

            if (success) {
                System.out.println("Interview scheduled successfully.");
            } else {
                System.out.println("Interview scheduling failed.");
            }

        } catch (java.time.format.DateTimeParseException e) {
            System.out.println(
                    "Invalid date format. Example: 2026-10-15 14:30"
            );
        }
    }




    private static void updateApplicationStatus(
            Scanner scanner, Company company) {

        System.out.println("\n========== UPDATE APPLICATION STATUS ==========");

        if (company == null) {
            System.out.println("Please log in as a company first.");
            return;
        }

        System.out.print("Enter Application ID: ");
        int applicationId = readInt(scanner);

        System.out.println("\nAvailable statuses:");
        System.out.println("1. SHORTLISTED");
        System.out.println("2. REJECTED");
        System.out.println("3. SELECTED");
        System.out.println("4. SUBMITTED");

        System.out.print("Choose new status: ");
        int choice = readInt(scanner);

        ApplicationStatus newStatus;

        switch (choice) {
            case 1:
                newStatus = ApplicationStatus.SHORTLISTED;
                break;
            case 2:
                newStatus = ApplicationStatus.REJECTED;
                break;
            case 3:
                newStatus = ApplicationStatus.SELECTED;
                break;
            case 4:
                newStatus = ApplicationStatus.SUBMITTED;
                break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        boolean success = applicationService.updateStatus(
                applicationId,
                company.getCompanyId(),
                newStatus
        );

        if (success) {
            System.out.println("Application status updated to " + newStatus);
        } else {
            System.out.println("Application status update failed.");
        }
    }
    private static void concurrencyMenu(Scanner scanner) {

        while (true) {
            System.out.println();
            System.out.println("================================");
            System.out.println("   CONCURRENCY DEMONSTRATIONS");
            System.out.println("================================");
            System.out.println("1. Concurrent Application Processing");
            System.out.println("2. Concurrent Registration and Login");
            System.out.println("3. Placement Processing Stats Demo");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    placement.concurrency.ConcurrentApplicationDemo.main(new String[0]
                    );
                    break;

                case 2:
                    placement.concurrency.ConcurrentRegistrationDemo.main(new String[0]
                    );
                    break;

                case 3:
                    PlacementStatsDemo.runDemo();
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


    // =========================================================
    // INPUT HELPERS
    // =========================================================

    private static int readInt(Scanner scanner) {

        while (true) {

            try {

                return Integer.parseInt(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Please enter a valid number: "
                );
            }
        }
    }


    private static double readDouble(
            Scanner scanner) {

        while (true) {

            try {

                return Double.parseDouble(
                        scanner.nextLine()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Please enter a valid CGPA: "
                );
            }
        }
    }

        
    private static String readValidatedUsername(Scanner scanner) {
        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } else if (!username.matches("[a-zA-Z0-9_.]+")) {
                System.out.println(
                        "Username can contain only letters, digits, "
                        + "underscore (_) and dot (.)."
                );
            } else {
                return username;
            }
        }
    }

    private static String readValidatedPassword(Scanner scanner) {
        while (true) {
            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (password.length() < 6) {
                System.out.println(
                        "Password must contain at least 6 characters."
                );
            } else if (!password.matches("(?=.*[a-zA-Z])(?=.*[0-9]).+")) {
                System.out.println(
                        "Password must contain at least one alphabet "
                        + "and one digit."
                );
            } else {
                return password;
            }
        }
    }

    private static String readNonEmptyString(
            Scanner scanner, String prompt) {

        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.matches(".*[a-zA-Z].*")) {
                System.out.println("Name must contain at least one alphabet.");
            } else if (value.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else {
                return value;
            }
        }
    }

    private static double readValidatedCgpa(Scanner scanner) {
        while (true) {
            System.out.print("CGPA: ");
            String input = scanner.nextLine().trim();

            try {
                double cgpa = Double.parseDouble(input);

                if (!Double.isFinite(cgpa)
                        || cgpa < 0.0
                        || cgpa > 10.0) {
                    System.out.println(
                            "CGPA must be between 0 and 10."
                    );
                } else {
                    return cgpa;
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid CGPA. Please enter a numeric value."
                );
            }
        }
    }

}