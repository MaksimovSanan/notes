package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiningPhilosophers {
    public static void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(6);

        Fork fork1 = new Fork(1);
        Fork fork2 = new Fork(2);
        Fork fork3 = new Fork(3);
        Fork fork4 = new Fork(4);
        Fork fork5 = new Fork(5);
        Fork fork6 = new Fork(6);
        Philosopher denis = new Philosopher("Denis", 5*3, 5*3, 2*3, 1*3, fork6, fork1);
        Philosopher nikita = new Philosopher("Nikita", 5*3, 5*3, 2*3, 1*3, fork1, fork2);
        Philosopher semen = new Philosopher("Semen", 5*3, 5*3, 2*3, 1*3, fork2, fork3);
        Philosopher vova = new Philosopher("Vova", 5*3, 5*3, 2*3, 1*3, fork3, fork4);
        Philosopher anton = new Philosopher("Anton", 5*3, 5*3, 2*3, 1*3, fork4, fork5);
        Philosopher eugene = new Philosopher("Eugene", 5*3, 5*3, 2*3, 1*3, fork5, fork6);

        executorService.submit(denis);
        executorService.submit(nikita);
        executorService.submit(semen);
        executorService.submit(vova);
        executorService.submit(anton);
        executorService.submit(eugene);


        try {
            TimeUnit.HOURS.sleep(8);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
