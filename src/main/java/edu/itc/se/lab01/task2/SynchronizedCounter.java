package edu.itc.se.lab01.task2;

public class SynchronizedCounter implements Counter {
    private long count;
    public synchronized void increment() { count++; }
    public synchronized long get() { return count; }
}