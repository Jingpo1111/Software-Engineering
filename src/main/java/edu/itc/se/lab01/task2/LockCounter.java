package edu.itc.se.lab01.task2;

import java.util.concurrent.locks.ReentrantLock;

public class LockCounter implements Counter {
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