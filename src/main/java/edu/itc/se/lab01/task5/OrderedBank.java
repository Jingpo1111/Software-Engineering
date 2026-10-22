package edu.itc.se.lab01.task5;

// Fix A: always lock the lower id first -> no circular wait
public class OrderedBank {
    static void transfer(Bank.Account from, Bank.Account to, long amount) {
        Bank.Account first = from.id < to.id ? from : to;
        Bank.Account second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                Bank.move(from, to, amount);
            }
        }
    }
}