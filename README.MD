##### Lab 01  

# Task 1: Threads
Creates four threads (subclass, Runnable, lambda, virtual), then joins them and prints their states.
## Run (Java 21+)
   `java .\src\main\java\edu\itc\se\lab01\task1\Main.java `

## Output: 
    ```
    artemis: 3
    mercury: 2
    gemini: 2
    apollo: 2
    artemis: 2
    gemini: 1
    mercury: 1
    apollo: 1
    artemis: 1
    gemini: liftoff!
    mercury: liftoff!
    apollo: liftoff!
    artemis: liftoff!
    Mission control: all landed
    apollo   virtual=false state=TERMINATED
    gemini   virtual=false state=TERMINATED
    mercury  virtual=false state=TERMINATED
    artemis  virtual=true  state=TERMINATED
    ```

# Task2: Fixing a race condition
    +   get() don't use lock so it's might read value from cache CPU.
    +   long is not volatile on JVM read/write long not automate.
    +   solution:  make get() and synchronized use the same lock or volatile/AtomicLong.
## How to Run: java src/main/java/edu/itc/se/lab01/task2/Bench.java
## Output: 
        UnsafeCounter          value=4,851,589  correct=false  32 ms
        SynchronizedCounter    value=8,000,000  correct=true   497 ms
        LockCounter            value=8,000,000  correct=true   77 ms
        AtomicCounter          value=8,000,000  correct=true   126 ms
        LongAdderCounter       value=8,000,000  correct=true   12 ms


# Task3: Producer–consumer with wait()/notify() and BlockingQueue
## Why while + notifyAll()
- **`while`:** after `wait()` returns, the condition may be false again (another
  thread got there first, or a spurious wakeup). With `if`, a producer could add
  to a full buffer or a consumer could remove from an empty one.
- **`notifyAll()`:** producers and consumers wait on the same monitor. `notify()`
  may wake the wrong kind of thread; it sleeps again, the signal is lost, and all
  threads can end up waiting. `notifyAll()` wakes everyone and each
  re-checks its own condition.
## How to run it : 
```
javac -d out edu/itc/se/lab01/task3/Main.java
java -ea -cp out edu.itc.se.lab01.task3.Main     
java -ea -cp out edu.itc.se.lab01.task3.Main2    
```
### java -ea -cp out edu.itc.se.lab01.task3.Main  
    Output: 
    ```
    consumer-3 processed Order[id=51, item=item-51, qty=2]
    consumer-2 processed Order[id=2, item=item-2, qty=4]
    consumer-1 processed Order[id=1, item=item-1, qty=3]
    consumer-3 processed Order[id=52, item=item-52, qty=4]
    consumer-2 processed Order[id=53, item=item-53, qty=3]
    consumer-3 processed Order[id=3, item=item-3, qty=1]
    consumer-2 processed Order[id=55, item=item-55, qty=3]
    consumer-1 processed Order[id=54, item=item-54, qty=5]
    consumer-3 processed Order[id=4, item=item-4, qty=4]
    consumer-3 processed Order[id=5, item=item-5, qty=3]
    consumer-2 processed Order[id=56, item=item-56, qty=2]
    consumer-1 processed Order[id=57, item=item-57, qty=2]
    consumer-2 processed Order[id=59, item=item-59, qty=2]
    consumer-3 processed Order[id=58, item=item-58, qty=5]
    consumer-1 processed Order[id=6, item=item-6, qty=2]
    consumer-1 processed Order[id=61, item=item-61, qty=1]
    consumer-2 processed Order[id=7, item=item-7, qty=2]
    consumer-3 processed Order[id=60, item=item-60, qty=1]
    consumer-1 processed Order[id=8, item=item-8, qty=4]
    consumer-2 processed Order[id=62, item=item-62, qty=3]
    consumer-3 processed Order[id=9, item=item-9, qty=2]
    consumer-1 processed Order[id=10, item=item-10, qty=5]
    consumer-3 processed Order[id=11, item=item-11, qty=3]
    consumer-2 processed Order[id=63, item=item-63, qty=1]
    consumer-3 processed Order[id=64, item=item-64, qty=4]
    consumer-1 processed Order[id=12, item=item-12, qty=4]
    consumer-3 processed Order[id=14, item=item-14, qty=3]
    consumer-1 processed Order[id=65, item=item-65, qty=5]
    consumer-2 processed Order[id=13, item=item-13, qty=4]
    consumer-3 processed Order[id=15, item=item-15, qty=1]
    consumer-2 processed Order[id=16, item=item-16, qty=5]
    consumer-1 processed Order[id=66, item=item-66, qty=1]
    consumer-2 processed Order[id=18, item=item-18, qty=1]
    consumer-3 processed Order[id=17, item=item-17, qty=2]
    consumer-2 processed Order[id=19, item=item-19, qty=3]
    consumer-1 processed Order[id=67, item=item-67, qty=4]
    consumer-2 processed Order[id=69, item=item-69, qty=1]
    consumer-3 processed Order[id=68, item=item-68, qty=1]
    consumer-1 processed Order[id=20, item=item-20, qty=3]
    consumer-2 processed Order[id=21, item=item-21, qty=5]
    consumer-3 processed Order[id=70, item=item-70, qty=4]
    consumer-2 processed Order[id=22, item=item-22, qty=5]
    consumer-1 processed Order[id=71, item=item-71, qty=3]
    consumer-3 processed Order[id=23, item=item-23, qty=4]
    consumer-2 processed Order[id=24, item=item-24, qty=4]
    consumer-1 processed Order[id=72, item=item-72, qty=5]
    consumer-3 processed Order[id=25, item=item-25, qty=5]
    consumer-2 processed Order[id=73, item=item-73, qty=3]
    consumer-3 processed Order[id=75, item=item-75, qty=1]
    consumer-2 processed Order[id=26, item=item-26, qty=1]
    consumer-1 processed Order[id=74, item=item-74, qty=3]
    consumer-1 processed Order[id=77, item=item-77, qty=4]
    consumer-2 processed Order[id=27, item=item-27, qty=1]
    consumer-3 processed Order[id=76, item=item-76, qty=2]
    consumer-1 processed Order[id=78, item=item-78, qty=3]
    consumer-3 processed Order[id=28, item=item-28, qty=5]
    consumer-1 processed Order[id=80, item=item-80, qty=4]
    consumer-3 processed Order[id=29, item=item-29, qty=5]
    consumer-2 processed Order[id=79, item=item-79, qty=4]
    consumer-3 processed Order[id=30, item=item-30, qty=4]
    consumer-2 processed Order[id=31, item=item-31, qty=1]
    consumer-1 processed Order[id=81, item=item-81, qty=5]
    consumer-1 processed Order[id=33, item=item-33, qty=4]
    consumer-3 processed Order[id=82, item=item-82, qty=4]
    consumer-2 processed Order[id=32, item=item-32, qty=3]
    consumer-1 processed Order[id=83, item=item-83, qty=3]
    consumer-2 processed Order[id=84, item=item-84, qty=3]
    consumer-3 processed Order[id=34, item=item-34, qty=5]
    consumer-1 processed Order[id=85, item=item-85, qty=1]
    consumer-3 processed Order[id=86, item=item-86, qty=1]
    consumer-2 processed Order[id=35, item=item-35, qty=1]
    consumer-1 processed Order[id=87, item=item-87, qty=3]
    consumer-3 processed Order[id=36, item=item-36, qty=3]
    consumer-1 processed Order[id=37, item=item-37, qty=1]
    consumer-2 processed Order[id=88, item=item-88, qty=4]
    consumer-2 processed Order[id=39, item=item-39, qty=4]
    consumer-1 processed Order[id=38, item=item-38, qty=5]
    consumer-3 processed Order[id=89, item=item-89, qty=1]
    consumer-1 processed Order[id=40, item=item-40, qty=5]
    consumer-2 processed Order[id=90, item=item-90, qty=5]
    consumer-1 processed Order[id=91, item=item-91, qty=4]
    consumer-1 processed Order[id=92, item=item-92, qty=5]
    consumer-3 processed Order[id=41, item=item-41, qty=5]
    consumer-2 processed Order[id=42, item=item-42, qty=1]
    consumer-3 processed Order[id=93, item=item-93, qty=3]
    consumer-1 processed Order[id=43, item=item-43, qty=3]
    consumer-2 processed Order[id=44, item=item-44, qty=5]
    consumer-3 processed Order[id=94, item=item-94, qty=2]
    consumer-2 processed Order[id=46, item=item-46, qty=4]
    consumer-3 processed Order[id=47, item=item-47, qty=1]
    consumer-1 processed Order[id=45, item=item-45, qty=1]
    consumer-2 processed Order[id=95, item=item-95, qty=3]
    consumer-3 processed Order[id=48, item=item-48, qty=2]
    consumer-1 processed Order[id=49, item=item-49, qty=4]
    consumer-3 processed Order[id=50, item=item-50, qty=3]
    consumer-2 processed Order[id=96, item=item-96, qty=3]
    consumer-3 processed Order[id=98, item=item-98, qty=4]
    consumer-1 processed Order[id=97, item=item-97, qty=2]
    consumer-1 received poison pill, stopping
    consumer-2 processed Order[id=99, item=item-99, qty=5]
    consumer-2 received poison pill, stopping
    consumer-3 processed Order[id=100, item=item-100, qty=4]
    consumer-3 received poison pill, stopping
    Total processed: 100 (expected 100) in 633 ms
    ```                                    
### java -ea -cp out edu.itc.se.lab01.task3.Main2  
    Output: 
    ```
    consumer-1 processed Order[id=51, item=item-51, qty=1]
    consumer-2 processed Order[id=1, item=item-1, qty=5]
    consumer-3 processed Order[id=52, item=item-52, qty=1]
    consumer-3 processed Order[id=4, item=item-4, qty=5]
    consumer-2 processed Order[id=3, item=item-3, qty=5]
    consumer-3 processed Order[id=53, item=item-53, qty=5]
    consumer-1 processed Order[id=2, item=item-2, qty=5]
    consumer-3 processed Order[id=54, item=item-54, qty=5]
    consumer-2 processed Order[id=5, item=item-5, qty=2]
    consumer-1 processed Order[id=6, item=item-6, qty=1]
    consumer-2 processed Order[id=56, item=item-56, qty=3]
    consumer-3 processed Order[id=55, item=item-55, qty=4]
    consumer-1 processed Order[id=7, item=item-7, qty=5]
    consumer-2 processed Order[id=8, item=item-8, qty=4]
    consumer-1 processed Order[id=9, item=item-9, qty=5]
    consumer-3 processed Order[id=57, item=item-57, qty=3]
    consumer-2 processed Order[id=58, item=item-58, qty=2]
    consumer-1 processed Order[id=10, item=item-10, qty=1]
    consumer-3 processed Order[id=59, item=item-59, qty=3]
    consumer-2 processed Order[id=11, item=item-11, qty=1]
    consumer-3 processed Order[id=60, item=item-60, qty=4]
    consumer-2 processed Order[id=13, item=item-13, qty=2]
    consumer-1 processed Order[id=12, item=item-12, qty=2]
    consumer-1 processed Order[id=14, item=item-14, qty=2]
    consumer-3 processed Order[id=61, item=item-61, qty=2]
    consumer-2 processed Order[id=62, item=item-62, qty=4]
    consumer-1 processed Order[id=15, item=item-15, qty=5]
    consumer-3 processed Order[id=63, item=item-63, qty=1]
    consumer-2 processed Order[id=16, item=item-16, qty=5]
    consumer-1 processed Order[id=64, item=item-64, qty=4]
    consumer-2 processed Order[id=17, item=item-17, qty=1]
    consumer-3 processed Order[id=65, item=item-65, qty=4]
    consumer-1 processed Order[id=18, item=item-18, qty=4]
    consumer-3 processed Order[id=66, item=item-66, qty=2]
    consumer-2 processed Order[id=19, item=item-19, qty=3]
    consumer-1 processed Order[id=67, item=item-67, qty=4]
    consumer-3 processed Order[id=20, item=item-20, qty=1]
    consumer-2 processed Order[id=68, item=item-68, qty=5]
    consumer-1 processed Order[id=69, item=item-69, qty=5]
    consumer-3 processed Order[id=21, item=item-21, qty=3]
    consumer-2 processed Order[id=70, item=item-70, qty=2]
    consumer-3 processed Order[id=23, item=item-23, qty=5]
    consumer-1 processed Order[id=22, item=item-22, qty=3]
    consumer-3 processed Order[id=71, item=item-71, qty=3]
    consumer-1 processed Order[id=72, item=item-72, qty=2]
    consumer-2 processed Order[id=24, item=item-24, qty=5]
    consumer-3 processed Order[id=25, item=item-25, qty=2]
    consumer-2 processed Order[id=73, item=item-73, qty=1]
    consumer-3 processed Order[id=74, item=item-74, qty=5]
    consumer-3 processed Order[id=75, item=item-75, qty=5]
    consumer-1 processed Order[id=26, item=item-26, qty=4]
    consumer-2 processed Order[id=27, item=item-27, qty=4]
    consumer-3 processed Order[id=28, item=item-28, qty=2]
    consumer-1 processed Order[id=76, item=item-76, qty=2]
    consumer-3 processed Order[id=30, item=item-30, qty=4]
    consumer-2 processed Order[id=29, item=item-29, qty=1]
    consumer-1 processed Order[id=77, item=item-77, qty=4]
    consumer-3 processed Order[id=78, item=item-78, qty=5]
    consumer-1 processed Order[id=79, item=item-79, qty=4]
    consumer-3 processed Order[id=80, item=item-80, qty=3]
    consumer-2 processed Order[id=31, item=item-31, qty=3]
    consumer-1 processed Order[id=32, item=item-32, qty=5]
    consumer-3 processed Order[id=81, item=item-81, qty=3]
    consumer-2 processed Order[id=82, item=item-82, qty=5]
    consumer-1 processed Order[id=33, item=item-33, qty=5]
    consumer-3 processed Order[id=34, item=item-34, qty=2]
    consumer-2 processed Order[id=83, item=item-83, qty=1]
    consumer-1 processed Order[id=84, item=item-84, qty=4]
    consumer-3 processed Order[id=35, item=item-35, qty=1]
    consumer-2 processed Order[id=85, item=item-85, qty=3]
    consumer-1 processed Order[id=36, item=item-36, qty=2]
    consumer-3 processed Order[id=86, item=item-86, qty=3]
    consumer-1 processed Order[id=38, item=item-38, qty=1]
    consumer-2 processed Order[id=37, item=item-37, qty=4]
    consumer-1 processed Order[id=39, item=item-39, qty=5]
    consumer-3 processed Order[id=87, item=item-87, qty=1]
    consumer-2 processed Order[id=40, item=item-40, qty=2]
    consumer-1 processed Order[id=41, item=item-41, qty=3]
    consumer-2 processed Order[id=42, item=item-42, qty=1]
    consumer-3 processed Order[id=88, item=item-88, qty=3]
    consumer-2 processed Order[id=43, item=item-43, qty=5]
    consumer-2 processed Order[id=44, item=item-44, qty=2]
    consumer-1 processed Order[id=89, item=item-89, qty=3]
    consumer-2 processed Order[id=91, item=item-91, qty=2]
    consumer-3 processed Order[id=90, item=item-90, qty=4]
    consumer-3 processed Order[id=46, item=item-46, qty=2]
    consumer-1 processed Order[id=45, item=item-45, qty=3]
    consumer-2 processed Order[id=92, item=item-92, qty=2]
    consumer-3 processed Order[id=47, item=item-47, qty=1]
    consumer-2 processed Order[id=94, item=item-94, qty=4]
    consumer-1 processed Order[id=93, item=item-93, qty=3]
    consumer-2 processed Order[id=96, item=item-96, qty=4]
    consumer-3 processed Order[id=95, item=item-95, qty=5]
    consumer-3 processed Order[id=48, item=item-48, qty=3]
    consumer-1 processed Order[id=97, item=item-97, qty=3]
    consumer-2 processed Order[id=98, item=item-98, qty=4]
    consumer-3 processed Order[id=99, item=item-99, qty=2]
    consumer-3 processed Order[id=100, item=item-100, qty=4]
    consumer-2 processed Order[id=50, item=item-50, qty=3]
    consumer-3 received poison pill, stopping
    consumer-2 received poison pill, stopping
    consumer-1 processed Order[id=49, item=item-49, qty=4]
    consumer-1 received poison pill, stopping
    Total processed: 100 (expected 100) in 622 ms
    ```

# Task4 Thread pools, Future, virtual threads
## Run : `java .\src\main\java\edu\itc\se\lab01\task4\Main.java`
## Output : 
    ```
    single-threaded: 283146 primes in 770 ms
    pool (16 threads): 283146 primes in 149 ms (matches single: true)
    downloads on fixed(20) platform threads: ok=489 failed=10 timedOut=1 in 5223 ms
    downloads on virtual threads           : ok=489 failed=10 timedOut=1 in 1220 ms
    bonus CompletableFuture      : ok=489 fallback=11 in 1034 ms
    ```
## CPU-bound
Primes below 4,000,000 split into 16 chunks on a fixed pool (one thread per core).
The total equals the single-threaded count.
Timings: single = <ms>, pool = <ms>.
## I/O-bound: platform pool vs virtual threads
500 downloads, each sleeps 200 ms.
- **Fixed pool (20):** only 20 run at once, so 500 / 20 = 25 rounds x 200 ms = ~5 s.
- **Virtual threads:** one cheap thread per task. A sleeping virtual thread releases
  its carrier thread, so all 500 wait at the same time = ~200 ms.
Timings: fixed(20) = <ms>, virtual = <ms>.
Virtual threads help blocking I/O, not CPU-bound work (cores are the limit).
## Failures and timeout
- Every 50th download throws `IOException`. `future.get()` throws
  `ExecutionException`, which is caught and counted.
- sleeps 5 s. `get(1, SECONDS)` throws `TimeoutException`, then
  `cancel(true)` interrupts it and it is reported as timed out.
- Summary: `ok=489 failed=10 timedOut=1`. The program never crashes.
## Shutdown
`close()` waits for tasks and shuts
the pool down, so the JVM exits by itself without `System.exit`.




# Task 5: Deadlock and graceful shutdown

## Run
```
javac -d out edu/itc/se/lab01/task5/Bank.java
java -cp out edu.itc.se.lab01.task5.Bank broken    # hangs; watchdog reports it
java -cp out edu.itc.se.lab01.task5.Bank           # OrderedBank, TryLockBank, shutdown
```

## Deadlock
`transfer(from, to)` locks `from`, then `to`. One thread doing acc-1 -> acc-2 and
another doing acc-2 -> acc-1 each hold one monitor and wait for the other
(circular wait). `deadlock.txt` is the real `jstack` report. The watchdog daemon
calls `ThreadMXBean.findDeadlockedThreads()` every second and prints the names.

## Fix A: lock ordering (OrderedBank)
Always lock the account with the lower id first, so no cycle can form.
Result: transfers=<n> total=4000 in <ms> ms.

## Fix B: tryLock (TryLockBank)
`tryLock(10 ms)` on both locks. On failure, release what is held, sleep a random
1-4 ms, and retry.
Result: transfers=<n> retries=<n> total=4000 in <ms> ms.

## Graceful shutdown
Workers loop `while (!Thread.currentThread().isInterrupted())`. After 2 s main
interrupts them and joins each with a 1 s timeout. A blocking call (`tryLock`,
`sleep`) that throws `InterruptedException` clears the flag, so the worker
restores it with `Thread.currentThread().interrupt()`. Locks are released in
`finally`, so the invariant holds: total = 4000 after shutdown.
Result: all workers stopped=true, total=4000.