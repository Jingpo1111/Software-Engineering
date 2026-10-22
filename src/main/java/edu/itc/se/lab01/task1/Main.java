package edu.itc.se.lab01.task1;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        boolean sequential = args.length > 0 && args[0].equals("--sequential");

        Thread apollo  = new Countdown("apollo");
        Thread gemini  = new Thread(new CountdownTask(), "gemini");
        Thread mercury = new Thread(() -> new CountdownTask().run(), "mercury");
        Thread artemis = Thread.ofVirtual().name("artemis").unstarted(new CountdownTask());

        Thread[] threads = {apollo, gemini, mercury, artemis};

        for (Thread t : threads) {
            if (sequential) {
                t.run();      // runs on the main thread, one after another
            } else {
                t.start();    // runs concurrently
            }
        }

        System.out.println("Mission control: all launched");

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Mission control: all landed");

        for (Thread t : threads) {
            System.out.printf("%-8s virtual=%-5b state=%s%n",
                    t.getName(), t.isVirtual(), t.getState());
        }
    }
}