
package placement.concurrency;

import java.util.ArrayList;
import java.util.List;

public class PlacementStatsDemo {

    public static void runDemo() {

        PlacementProcessingStats stats =
                new PlacementProcessingStats();

        List<Thread> threads = new ArrayList<>();

        System.out.println(
                "\n===== REENTRANT LOCK DEMONSTRATION ====="
        );

        for (int i = 1; i <= 2; i++) {

            Thread thread = new Thread(() -> {

                for (int j = 0; j < 3; j++) {
                    stats.recordProcessedApplication();
                }

                for (int j = 0; j < 2; j++) {
                    stats.recordSelectedApplication();
                }

            }, "Stats-Thread-" + i);

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Statistics demo interrupted.");
                return;
            }
        }

        System.out.println("\n===== FINAL STATISTICS =====");
        System.out.println(
                "Applications processed: "
                        + stats.getApplicationsProcessed()
        );
        System.out.println(
                "Applications selected: "
                        + stats.getApplicationsSelected()
        );
        System.out.println("============================");
    }
}
