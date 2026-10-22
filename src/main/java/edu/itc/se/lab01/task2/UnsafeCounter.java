package edu.itc.se.lab01.task2;

/* Wrong results (UnsafeCounter, 3 runs) - replace with your own numbers:
 * run 1: 6,693,491
 * run 2: 5,000,475
 * run 3: 7,215,330
 */
public class UnsafeCounter implements Counter {
    private long count;
    public void increment() { count++; }   // read -> +1 -> write (not atomic)
    public long get() { return count; }
}