
package edu.itc.se.lab01.task2;

import java.util.concurrent.atomic.LongAdder;

/* LongAdder is faster under contention: it splits the value into several
 * cells, so each thread mostly updates its own cell (no CAS retry storm on
 * one variable like AtomicLong). sum() adds all cells only when get() is called. */
public class LongAdderCounter implements Counter {
    private final LongAdder adder = new LongAdder();
    public void increment() { adder.increment(); }
    public long get() { return adder.sum(); }
}