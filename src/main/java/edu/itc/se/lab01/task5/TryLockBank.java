package edu.itc.se.lab01.task5;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

// Fix B: tryLock on both; on failure release, back off randomly, retry
public class TryLockBank {
    static final AtomicLong retries = new AtomicLong();

    static void transfer(Bank.Account from, Bank.Account to, long amount) throws InterruptedException {
        while (true) {
            if (from.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                try {
                    if (to.lock.tryLock(10, TimeUnit.MILLISECONDS)) {
                        try { Bank.move(from, to, amount); return; }
                        finally { to.lock.unlock(); }
                    }
                } finally { from.lock.unlock(); }
            }
            retries.incrementAndGet();
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 5));
        }
    }
}