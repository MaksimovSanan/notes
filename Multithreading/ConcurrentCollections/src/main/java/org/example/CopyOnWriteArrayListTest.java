package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class CopyOnWriteArrayListTest {
    public static void copyOnWriteArrayListTest() {
        List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
        List<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>(); // CopyOnWriteArraySet

        fillList(synchronizedList, 1000);
        fillList(copyOnWriteArrayList, 1000);

        try {
            System.out.println("synchronizedList test:");
            testList(synchronizedList);
            System.out.println("copyOnWriteArrayList test:");
            testList(copyOnWriteArrayList);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static void fillList(List<Integer> list, int counter) {
        for (int i = 0; i < counter; ++i) {
            list.add(i);
        }
    }

    private static void testList(List<Integer> list) throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Future<Long> takingTimePart1Future = executorService.submit(new Runner(list, 0, 200, latch));
        Future<Long> takingTimePart2Future = executorService.submit(new Runner(list, 200, 400, latch));
        Future<Long> takingTimePart3Future = executorService.submit(new Runner(list, 400, 600, latch));
        Future<Long> takingTimePart4Future = executorService.submit(new Runner(list, 600, 800, latch));
        Future<Long> takingTimePart5Future = executorService.submit(new Runner(list, 800, 1000, latch));

        executorService.shutdown();
        TimeUnit.SECONDS.sleep(3);
        System.out.println("Lets start");
        latch.countDown();

        System.out.printf("Part1 take a %s \n", takingTimePart1Future.get() / 1000);
        System.out.printf("Part2 take a %s \n", takingTimePart2Future.get() / 1000);
        System.out.printf("Part3 take a %s \n", takingTimePart3Future.get() / 1000);
        System.out.printf("Part4 take a %s \n", takingTimePart4Future.get() / 1000);
        System.out.printf("Part5 take a %s \n", takingTimePart5Future.get() / 1000);
        System.out.printf("Summary their take a %s \n", (takingTimePart1Future.get()
                + takingTimePart2Future.get()
                + takingTimePart3Future.get()
                + takingTimePart4Future.get()
                + takingTimePart5Future.get()) / 1000);
    }

    private static class Runner implements Callable<Long> {
        private final List<Integer> list;
        private final int start;
        private final int stop;
        private final CountDownLatch latch;

        public Runner(List<Integer> list, int start, int stop, CountDownLatch latch) {
            this.list = list;
            this.start = start;
            this.stop = stop;
            this.latch = latch;
        }

        @Override
        public Long call() throws Exception {
            latch.await();
//            System.out.printf("Thread %s: latch is broken%n", Thread.currentThread());
            long startTime = System.nanoTime();

            for (int i = start; i < stop; ++i) {
                list.get(i);
//                list.add(i); //test this, very slow
            }
            return System.nanoTime() - startTime;
        }
    }
}
