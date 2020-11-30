package com.q256.skyblockImproved.overlay;

import com.google.common.primitives.Ints;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedDamage implements Delayed {
    public final long expirationTime;
    public final double amount;
    public final int enemyCount;

    DelayedDamage(int enemyCount, double amount, long delayInMillis){
        expirationTime = System.currentTimeMillis() + delayInMillis;
        this.amount = amount;
        this.enemyCount = enemyCount;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = expirationTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Ints.saturatedCast(this.expirationTime - ((DelayedDamage) o).expirationTime);
    }
}
