package edu.itc.se.lab01.task2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

interface Counter {
    void increment();
    long get();
}

/* Wrong results (UnsafeCounter, 3 runs) - replace with your own numbers:
 * run 1: 3,412,907
 * run 2: 2,981,455
 * run 3: 4,106,230
 */
class UnsafeCounter implements Counter {
    private long count;
    public void increment() { count++; }   // read -> +1 -> write (not atomic)
    public long get() { return count; }
}

class SynchronizedCounter implements Counter {
    private long count;
    public synchronized void increment() { count++; }
    public synchronized long get() { return count; }
}

class LockCounter implements Counter {
    private final ReentrantLock lock = new ReentrantLock();
    private long count;
    public void increment() {
        lock.lock();
        try { count++; } finally { lock.unlock(); }
    }
    public long get() {
        lock.lock();
        try { return count; } finally { lock.unlock(); }
    }
}

class AtomicCounter implements Counter {
    private final AtomicLong count = new AtomicLong();
    public void increment() { count.incrementAndGet(); }
    public long get() { return count.get(); }
}

/* LongAdder is faster under contention: it splits the value into several
 * cells, so each thread mostly updates its own cell (no CAS retry storm on
 * one variable like AtomicLong). sum() adds all cells only when get() is called. */
class LongAdderCounter implements Counter {
    private final LongAdder adder = new LongAdder();
    public void increment() { adder.increment(); }
    public long get() { return adder.sum(); }
}

public class Bench {
    static final int THREADS = 8;
    static final int ITERATIONS = 1_000_000;

    static void run(String label, Counter counter) throws InterruptedException {
        long start = System.nanoTime();
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < THREADS; t++) {
            Thread th = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) counter.increment();
            }, "inc-" + t);
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) th.join();
        long ms = (System.nanoTime() - start) / 1_000_000;
        long expected = (long) THREADS * ITERATIONS;
        long value = counter.get();
        System.out.printf("%-22s value=%,d  correct=%-5b  %d ms%n",
                label, value, value == expected, ms);
    }

    public static void main(String[] args) throws InterruptedException {
    run("UnsafeCounter", new UnsafeCounter());
    run("SynchronizedCounter", new SynchronizedCounter());
    run("LockCounter", new LockCounter());
    run("AtomicCounter", new AtomicCounter());
    run("LongAdderCounter", new LongAdderCounter());
}
}