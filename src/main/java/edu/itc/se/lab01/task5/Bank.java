package edu.itc.se.lab01.task5;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Bank {
    static final int ACCOUNTS = 4, THREADS = 8, PER_THREAD = 100_000;

    static final class Account {
        final int id; private long balance;
        final java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
        Account(int id, long balance) { this.id = id; this.balance = balance; }
        long balance() { return balance; }
        void withdraw(long a) { balance -= a; }
        void deposit(long a)  { balance += a; }
    }

    interface Transfer { void run(Account from, Account to, long amount) throws InterruptedException; }

    static void move(Account from, Account to, long amount) {
        if (from.balance() >= amount) { from.withdraw(amount); to.deposit(amount); }
    }

    // BUG: lock order depends on argument order -> circular wait
    static void brokenTransfer(Account from, Account to, long amount) {
        synchronized (from) { synchronized (to) { move(from, to, amount); } }
    }

    static Account[] newAccounts() {
        Account[] acc = new Account[ACCOUNTS];
        for (int i = 0; i < acc.length; i++) acc[i] = new Account(i, 1_000);
        return acc;
    }

    static long total(Account[] acc) {
        long t = 0; for (Account a : acc) t += a.balance(); return t;
    }

    // Step 3: watchdog daemon thread
    static void startWatchdog() {
        Thread w = new Thread(() -> {
            ThreadMXBean mx = ManagementFactory.getThreadMXBean();
            while (true) {
                try { Thread.sleep(1000); } catch (InterruptedException e) { return; }
                long[] ids = mx.findDeadlockedThreads();
                if (ids != null) {
                    StringBuilder sb = new StringBuilder();
                    for (ThreadInfo ti : mx.getThreadInfo(ids)) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(ti.getThreadName());
                    }
                    System.out.println("[watchdog] DEADLOCK detected between: " + sb);
                }
            }
        }, "watchdog");
        w.setDaemon(true);
        w.start();
    }

    static void runAll(String label, Transfer tr, boolean showRetries) throws InterruptedException {
        long t0 = System.nanoTime();
        TryLockBank.retries.set(0);
        Account[] acc = newAccounts();
        AtomicLong done = new AtomicLong();
        Thread[] ws = new Thread[THREADS];
        for (int w = 0; w < THREADS; w++) {
            ws[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                try {
                    for (int i = 0; i < PER_THREAD; i++) {
                        Account a = acc[rnd.nextInt(ACCOUNTS)], b = acc[rnd.nextInt(ACCOUNTS)];
                        if (a != b) tr.run(a, b, rnd.nextInt(1, 50));
                        done.incrementAndGet();
                    }
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "transfer-" + w);
            ws[w].start();
        }
        for (Thread t : ws) t.join();
        long ms = (System.nanoTime() - t0) / 1_000_000;
        System.out.printf("# %s%ntransfers=%d %stotal=%d (expected %d) in %d ms%n", label, done.get(),
                showRetries ? "retries=" + TryLockBank.retries.get() + " " : "", total(acc), ACCOUNTS * 1_000L, ms);
    }

    // Step 6: graceful shutdown
    static void shutdownDemo() throws InterruptedException {
        Account[] acc = newAccounts();
        long[] done = new long[THREADS];
        Thread[] ws = new Thread[THREADS];
        for (int w = 0; w < THREADS; w++) {
            final int id = w;
            ws[w] = new Thread(() -> {
                var rnd = ThreadLocalRandom.current();
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Account a = acc[rnd.nextInt(ACCOUNTS)], b = acc[rnd.nextInt(ACCOUNTS)];
                        if (a != b) { TryLockBank.transfer(a, b, rnd.nextInt(1, 50)); done[id]++; }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();            // restore the flag
                }
            }, "transfer-" + w);
            ws[w].start();
        }
        Thread.sleep(2000);
        System.out.println("[main] interrupting " + THREADS + " workers after 2000 ms");
        for (Thread t : ws) t.interrupt();

        boolean allStopped = true;
        for (int w = 0; w < THREADS; w++) {
            ws[w].join(1000);                                      // 1 s timeout
            if (ws[w].isAlive()) allStopped = false;
            else System.out.println(ws[w].getName() + " stopped after " + done[w] + " transfers");
        }
        System.out.println("all workers stopped=" + allStopped + " total=" + total(acc) + " (expected 4000)");
    }

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "all";
        startWatchdog();
        switch (mode) {
            case "broken"   -> runAll("BrokenBank (will hang, press Ctrl+C)", Bank::brokenTransfer, false);
            case "ordered"  -> runAll("OrderedBank", OrderedBank::transfer, false);
            case "trylock"  -> runAll("TryLockBank", TryLockBank::transfer, true);
            case "shutdown" -> shutdownDemo();
            default -> {
                runAll("OrderedBank", OrderedBank::transfer, false);
                runAll("TryLockBank", TryLockBank::transfer, true);
                shutdownDemo();
            }
        }
    }
}