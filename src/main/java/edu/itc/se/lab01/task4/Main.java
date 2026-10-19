package edu.itc.se.lab01.task4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class PrimeCounter implements Callable<Long> {
    private final long from, to;
    PrimeCounter(long from, long to) { this.from = from; this.to = to; }
    @Override public Long call() { return Main.countPrimes(from, to); }
}

class FakeDownload implements Callable<String> {
    private final String url;
    private final int id;
    FakeDownload(String url, int id) { this.url = url; this.id = id; }

    @Override public String call() throws Exception {
        Thread.sleep(id == Main.SLOW_ID ? 5000 : 200);       // simulated network
        if (id % 50 == 0) throw new IOException("boom");     // every 50th fails
        return url + " -> " + url.length();
    }
}

public class Main {
    static final int DOWNLOADS = 500;
    static final int SLOW_ID = 7;                            // the one 5-second task

    static long countPrimes(long from, long to) {
        long count = 0;
        for (long n = Math.max(from, 2); n < to; n++) {
            boolean prime = true;
            for (long d = 2; d * d <= n; d++) if (n % d == 0) { prime = false; break; }
            if (prime) count++;
        }
        return count;
    }

    static void runDownloads(String label, ExecutorService ex) {
        long t0 = System.nanoTime();
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 1; i <= DOWNLOADS; i++)
            futures.add(ex.submit(new FakeDownload("http://site/" + i, i)));

        int ok = 0, failed = 0, timedOut = 0;
        for (Future<String> f : futures) {
            try {
                f.get(1, TimeUnit.SECONDS);
                ok++;
            } catch (ExecutionException e) {
                failed++;                                        // IOException("boom")
            } catch (TimeoutException e) {
                f.cancel(true);                                  // interrupt the sleeper
                timedOut++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.printf("downloads on %-26s: ok=%d failed=%d timedOut=%d in %d ms%n",
                label, ok, failed, timedOut, (System.nanoTime() - t0) / 1_000_000);
    }

    // Bonus: CompletableFuture + allOf + handle()
    static void bonus() {
        long t0 = System.nanoTime();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> cfs = new ArrayList<>();
            for (int i = 1; i <= DOWNLOADS; i++) {
                String url = "http://site/" + i;
                int id = i;
                cfs.add(CompletableFuture.supplyAsync(() -> {
                            try { return new FakeDownload(url, id).call(); }
                            catch (Exception e) { throw new CompletionException(e); }
                        }, ex)
                        .orTimeout(1, TimeUnit.SECONDS)
                        .handle((r, e) -> e == null ? r : "fallback: " + url));
            }
            CompletableFuture.allOf(cfs.toArray(new CompletableFuture[0])).join();
            long fallbacks = cfs.stream().map(CompletableFuture::join)
                    .filter(s -> s.startsWith("fallback")).count();
            System.out.printf("bonus CompletableFuture      : ok=%d fallback=%d in %d ms%n",
                    DOWNLOADS - fallbacks, fallbacks, (System.nanoTime() - t0) / 1_000_000);
        }
    }

    public static void main(String[] args) throws Exception {
        final long LIMIT = 4_000_000, CHUNKS = 16;

        // ---- 1. CPU-bound ----
        long t0 = System.nanoTime();
        long single = countPrimes(0, LIMIT);
        System.out.printf("single-threaded: %d primes in %d ms%n", single, (System.nanoTime() - t0) / 1_000_000);

        int cores = Runtime.getRuntime().availableProcessors();
        t0 = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(cores)) {
            List<Future<Long>> futures = new ArrayList<>();
            long size = LIMIT / CHUNKS;
            for (int i = 0; i < CHUNKS; i++) {
                long from = i * size;
                long to = (i == CHUNKS - 1) ? LIMIT : from + size;
                futures.add(pool.submit(new PrimeCounter(from, to)));
            }
            long total = 0;
            for (Future<Long> f : futures) total += f.get();
            System.out.printf("pool (%d threads): %d primes in %d ms (matches single: %b)%n",
                    cores, total, (System.nanoTime() - t0) / 1_000_000, total == single);
        }

        // ---- 2-5. I/O-bound, failures, timeout, shutdown ----
        try (ExecutorService fixed = Executors.newFixedThreadPool(20)) {
            runDownloads("fixed(20) platform threads", fixed);
        }
        try (ExecutorService virt = Executors.newVirtualThreadPerTaskExecutor()) {
            runDownloads("virtual threads", virt);
        }

        // ---- 6. Bonus ----
        bonus();
        // no System.exit: try-with-resources closed every executor, so the JVM exits by itself
    }
}