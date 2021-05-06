package com.company;

import java.math.BigInteger;
import java.util.*;

public class MultiThreadBigPrimes{

    public static void main(String[] args) throws InterruptedException {
        Results results = new Results();
        Runnable task = new PrimeGenerator(results);
        CurrentStatus currentStatus = new CurrentStatus(results);
        Thread statusTask = new Thread(currentStatus);
        statusTask.start();
        List<Thread> threadList = new ArrayList<>();
        for(int i=0;i<500;i++) {
            Thread t = new Thread(task);
            threadList.add(t);
            t.start();
        }
        for(Thread t : threadList) {
            t.join();
        }
        // Another set of execution
        System.out.println(results.getSize());
    }
}

class PrimeGenerator implements Runnable{
    Results results;
    PrimeGenerator(Results _results) {
        results = _results;
    }

    @Override
    public void run() {
        results.add(new BigInteger(2000, new Random()).nextProbablePrime());
    }
}
class Results {
    SortedSet<BigInteger> primeSet = new TreeSet<>();

    void add(BigInteger integer) {
        primeSet.add(integer);
    }

    int getSize() {
        return primeSet.size();
    }

    public SortedSet<BigInteger> getPrimes() {
        return primeSet;
    }
}

class CurrentStatus implements Runnable {
    Results results;

    public CurrentStatus(Results results) {
        this.results = results;
    }

    @Override
    public void run() {
        while (results.getSize() < 500) {
            System.out.println("Got size of " + results.getSize());
            results.getPrimes().forEach(System.out :: println);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}