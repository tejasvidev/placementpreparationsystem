
package placement.concurrency;

import placement.model.Application;
import placement.model.Job;
import placement.model.Student;
import placement.service.ApplicationService;

public class ApplicationTask implements Runnable {

    private final Student student;
    private final Job job;
    private final ApplicationService applicationService;

    public ApplicationTask(
            Student student,
            Job job,
            ApplicationService applicationService) {
        this.student = student;
        this.job = job;
        this.applicationService = applicationService;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();

        System.out.println(
                threadName + " is processing an application for "
                + student.getName()
        );

        Application application = new Application(student, job);

        int applicationId =
                applicationService.submitApplication(application);

        if (applicationId != -1) {
            System.out.println(
                    threadName + ": Application submitted for "
                    + student.getName()
                    + ", Application ID: " + applicationId
            );
        } else {
            System.out.println(
                    threadName + ": Application failed for "
                    + student.getName()
            );
        }
    }
}
