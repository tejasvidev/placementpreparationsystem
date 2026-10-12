package placement.concurrency;

import java.util.concurrent.locks.ReentrantLock;

public class PlacementProcessingStats {

    private final ReentrantLock lock = new ReentrantLock();

    private int applicationsProcessed;
    private int applicationsSelected;

    public void recordProcessedApplication() {
        lock.lock();
        try {
            applicationsProcessed++;

            System.out.println(
                    "Applications processed: " + applicationsProcessed
                    + " | Thread: "
                    + Thread.currentThread().getName()
            );
        } finally {
            lock.unlock();
        }
    }

    public void recordSelectedApplication() {
        lock.lock();
        try {
            applicationsSelected++;

            System.out.println(
                    "Applications selected: " + applicationsSelected
                    + " | Thread: "
                    + Thread.currentThread().getName()
            );
        } finally {
            lock.unlock();
        }
    }

    public int getApplicationsProcessed() {
        lock.lock();
        try {
            return applicationsProcessed;
        } finally {
            lock.unlock();
        }
    }

    public int getApplicationsSelected() {
        lock.lock();
        try {
            return applicationsSelected;
        } finally {
            lock.unlock();
        }
    }
}