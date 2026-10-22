package edu.itc.se.lab01.task3;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static final int PRODUCERS = 2, ORDERS_EACH = 50, CONSUMERS = 3, CAPACITY = 5;

    static void runWith(OrderQueue queue) throws InterruptedException {
        long start = System.nanoTime();
        AtomicInteger processed = new AtomicInteger();
        AtomicInteger remaining = new AtomicInteger(PRODUCERS);
        Thread[] threads = new Thread[PRODUCERS + CONSUMERS];

        for (int p = 0; p < PRODUCERS; p++)
            threads[p] = new Thread(new Producer(queue, p * ORDERS_EACH + 1, ORDERS_EACH, CONSUMERS, remaining),
                                    "producer-" + (p + 1));
        for (int c = 0; c < CONSUMERS; c++)
            threads[PRODUCERS + c] = new Thread(new Consumer(queue, processed), "consumer-" + (c + 1));

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        int expected = PRODUCERS * ORDERS_EACH;
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (processed.get() != expected)
            throw new AssertionError("processed " + processed.get() + " != " + expected);
        System.out.println("Total processed: " + processed.get() + " (expected " + expected + ") in " + ms + " ms");
    }

    public static void main(String[] args) throws InterruptedException {
        runWith(new BoundedBuffer(CAPACITY));
    }
}