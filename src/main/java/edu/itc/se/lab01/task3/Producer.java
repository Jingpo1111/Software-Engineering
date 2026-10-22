package edu.itc.se.lab01.task3;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private final OrderQueue queue;
    private final int firstId, count, consumers;
    private final AtomicInteger remainingProducers;   // shared by all producers
    private final Random rnd = new Random();

    public Producer(OrderQueue queue, int firstId, int count, int consumers, AtomicInteger remainingProducers) {
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