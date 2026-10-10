
package placement.concurrency;

public class WelcomeThread extends Thread {

    private final String userName;

    public WelcomeThread(String userName) {
        this.userName = userName;
    }

    @Override
    public void run() {
        System.out.println(
                "[Background Thread] Login activity recorded for: "
                + userName
                + " | Thread: "
                + Thread.currentThread().getName()
        );
    }
}
