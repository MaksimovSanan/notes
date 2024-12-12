package org.example;

import java.util.concurrent.TimeUnit;

public class Philosopher implements Runnable{
    private final String name;
    private final int starvationTolerance;
    private int actualStarvation;
    private final int thinkingTime;
    private final int eatingTime;
    private final int waitingTime;
    private final Fork fork1;
    private final Fork fork2;

    public Philosopher(String name, int starvationTolerance, int thinkingTime, int eatingTime, int waitingTime, Fork fork1, Fork fork2) {
        this.name = name;
        this.starvationTolerance = starvationTolerance;
        this.actualStarvation = starvationTolerance;
        this.thinkingTime = thinkingTime;
        this.eatingTime =eatingTime;
        this.waitingTime =waitingTime;
        this.fork1 = fork1;
        this.fork2 = fork2;
    }

    @Override
    public void run() {
        System.out.printf("Philosopher %s is born%n", name);
        Thread.currentThread().setName(name);
        while(!Thread.currentThread().isInterrupted()) {
            try {
                thinking();
                if(!feeding()) {
                    System.out.println("Someone was dead");
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("Philosopher %s is dead!%n", name);
    }

    private void thinking() throws InterruptedException {
        System.out.printf("Philosopher %s is thinking%n", name);
        TimeUnit.SECONDS.sleep(thinkingTime);
    }

    private boolean feeding() throws InterruptedException {
        while (true) {
            if(actualStarvation < 0) {
                System.out.printf("Philosopher %s is dead!%n", name);
                return false;
            }
            System.out.printf("Philosopher %s wants to eat!%n", name);
            if (fork1.tryOccupy()) {
                if (fork2.tryOccupy()) {
                    System.out.printf("Philosopher %s starts eating%n", name);
                    TimeUnit.SECONDS.sleep(eatingTime);
                    System.out.printf("Philosopher %s is feed up%n", name);
                    fork1.release();
                    fork2.release();
                    actualStarvation = starvationTolerance;
                    return true;
                } else {
                    System.out.printf("Philosopher %s cant take fork %s and release fork %s%n", name, fork2.getId(), fork1.getId());
                    fork1.release();
                }
            }
            waitingForks();
        }
    }

    private void waitingForks() throws InterruptedException {
        System.out.printf("Philosopher %s couldn't pick up both forks, need wait%n", name);
        TimeUnit.SECONDS.sleep(waitingTime);
        actualStarvation -= waitingTime;
    }

}
