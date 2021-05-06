package com.company;

import java.math.BigInteger;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class SingleThreadBigPrimes {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        SortedSet<BigInteger> bigIntegerSortedSet = new TreeSet<>();

        while(bigIntegerSortedSet.size() < 100) {
            bigIntegerSortedSet.add(new BigInteger(2000, new Random()).nextProbablePrime());
        }
        System.out.println(bigIntegerSortedSet);
        System.out.println("Time taken to populate the integer " + (System.currentTimeMillis() - start));
    }
}
