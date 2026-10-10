
package placement.concurrency;

public class ApplicationProcessor {

    private int totalApplicationsProcessed = 0;

    // Thread-safe counter update
    public synchronized void incrementApplicationsProcessed() {
        totalApplicationsProcessed++;
        System.out.println(
                "Applications processed: " + totalApplicationsProcessed
                + " | Thread: " + Thread.currentThread().getName()
        );
    }

    // Thread-safe counter read
    public synchronized int getTotalApplicationsProcessed() {
        return totalApplicationsProcessed;
    }
}
