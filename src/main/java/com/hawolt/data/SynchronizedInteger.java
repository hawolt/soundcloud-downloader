package com.hawolt.data;

/**
 * Created: 22/02/2022 17:50
 * Author: Twitter @hawolt
 **/

public class SynchronizedInteger {
    private final Object lock = new Object();

    private int integer;

    public SynchronizedInteger() {
        this(0);
    }

    public SynchronizedInteger(int integer) {
        this.integer = integer;
    }

    public void increment() {
        synchronized (lock) {
            integer += 1;
        }
    }

    public int incrementAndGet() {
        synchronized (lock) {
            return ++integer;
        }
    }

    public int getAndIncrement() {
        synchronized (lock) {
            return integer++;
        }
    }

    public int get() {
        synchronized (lock) {
            return integer;
        }
    }
}
