package edu.itc.se.lab01.task3;

// Step 6: same Producer/Consumer, different buffer
public class Main2 {
    public static void main(String[] args) throws InterruptedException {
        Main.runWith(new BlockingOrderQueue(Main.CAPACITY));
    }
}