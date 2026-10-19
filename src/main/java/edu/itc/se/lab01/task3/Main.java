package edu.itc.se.lab01.task3;

import java.util.ArrayDeque;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

record Order(int id, String item, int qty) {}

interface OrderQueue {
    void put(Order order) throws InterruptedException;
    Order take() throws InterruptedException;
}

// Adapter so a java.util.concurrent queue can be used where OrderQueue is expected
final class BlockingOrderQueue implements OrderQueue {
    private final BlockingQueue<Order> q;
    BlockingOrderQueue(int capacity) { this.q = new ArrayBlockingQueue<>(capacity); }
    public void put(Order o) throws InterruptedException { q.put(o); }
    public Order take() throws InterruptedException { return q.take(); }
}

class BoundedBuffer implements OrderQueue {
    private final ArrayDeque<Order> items = new ArrayDeque<>();
    private final int capacity;
    BoundedBuffer(int capacity) { this.capacity = capacity; }

    @Override public synchronized void put(Order order) throws InterruptedException {
        while (items.size() == capacity) wait();          // full -> wait
        items.addLast(order);
        assert items.size() <= capacity : "buffer exceeded capacity";
        notifyAll();
    }

    @Override public synchronized Order take() throws InterruptedException {
        while (items.isEmpty()) wait();                    // empty -> wait
        assert !items.isEmpty() : "take from empty buffer";
        Order o = items.removeFirst();
        notifyAll();
        return o;
    }
}

class Producer implements Runnable {
    private final OrderQueue queue;
    private final int firstId, count, consumers;
    private final AtomicInteger remainingProducers;   // shared by all producers
    private final Random rnd = new Random();

    Producer(OrderQueue queue, int firstId, int count, int consumers, AtomicInteger remainingProducers) {
        this.queue = queue; this.firstId = firstId; this.count = count;
        this.consumers = consumers; this.remainingProducers = remainingProducers;
    }

    @Override public void run() {
        try {
            for (int i = firstId; i < firstId + count; i++) {
                Thread.sleep(rnd.nextInt(21));                         // 0-20 ms
                queue.put(new Order(i, "item-" + i, 1 + rnd.nextInt(5)));
            }
            // only the LAST producer to finish sends the poison pills
            if (remainingProducers.decrementAndGet() == 0) {
                for (int c = 0; c < consumers; c++) queue.put(new Order(-1, "POISON", 0));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final OrderQueue queue;
    private final AtomicInteger processed;
    private final Random rnd = new Random();

    Consumer(OrderQueue queue, AtomicInteger processed) {
        this.queue = queue; this.processed = processed;
    }

    @Override public void run() {
        String name = Thread.currentThread().getName();
        try {
            while (true) {
                Order o = queue.take();
                if (o.id() == -1) {
                    System.out.println(name + " received poison pill, stopping");
                    return;
                }
                Thread.sleep(5 + rnd.nextInt(26));                     // 5-30 ms
                System.out.println(name + " processed " + o);
                processed.incrementAndGet();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

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

// Step 6: same Producer/Consumer, different buffer
class Main2 {
    public static void main(String[] args) throws InterruptedException {
        Main.runWith(new BlockingOrderQueue(Main.CAPACITY));
    }
}