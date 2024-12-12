package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class App {
    @Data
    @AllArgsConstructor
    private static class MyData {
        Long counter;

        public MyData(MyData other) {
            counter = other.counter;
        }
    }
    public static void main(String[] args) {
        processTwoIndependentCompletableFuture();
    }

    /*
    Результат одного вычисления, помещенного в CompletableFuture, можно передать для постобработки
    в несколько других CompletableFuture,
    каждый из которых будет иметь доступ к результату первого выполнения,
    из-за чего возможно проблемы RaceCondition
     */
    private static void concurrentProcessCompletableFuture() {
        CompletableFuture<MyData> cf1 = CompletableFuture.supplyAsync(() -> new MyData(0L));

        CompletableFuture<Void> cf2 = cf1.thenAcceptAsync((r) -> {
            printState("start cf2", r);

            processCounter(r);

            printState("complete cf2", r);
        });

        CompletableFuture<Void> cf3 = cf1.thenAcceptAsync((r) -> {
            printState("start cf3", r);

            processCounter(r);

            printState("complete cf3", r);
        });

        cf1.thenAccept((r) -> printState("cf1 before join", r));
        cf2.join();
        cf1.thenAccept((r) -> printState("cf1 after join", r));
    }

    /*
    Можно даже сделать так. Результат одного вычисления, передается для постобработки в другие
    CompletableFuture, при этом каждый поток получит собственную копию вычислений,
    не мешая при этом другим вычисления
     */
    private static void processTwoIndependentCompletableFuture() {
        CompletableFuture<MyData> cf1 = CompletableFuture.supplyAsync(() -> new MyData(0L));

        CompletableFuture<Void> cf2 = cf1
                .thenApplyAsync(r -> new MyData(r))
                .thenAcceptAsync((r) -> {
            printState("start cf2", r);

            processCounter(r);

            printState("complete cf2", r);
        });

        CompletableFuture<Void> cf3 = cf1
                .thenApplyAsync(r -> new MyData(r))
                .thenAcceptAsync((r) -> {
            printState("start cf3", r);

            processCounter(r);

            printState("complete cf3", r);
        });

        cf1.thenAccept((r) -> printState("cf1 before join", r));
        CompletableFuture.allOf(cf2, cf3).join();
        cf1.thenAccept((r) -> printState("cf1 after join", r));
    }

    private static void processCounter(MyData data) {
        for(int i = 0; i < 1000; ++i) {
            Long counter = data.getCounter();
            counter++;
            data.setCounter(counter);
            sleep();
        }
    }

    private static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printState(String state, Object r) {
        System.out.println(state + "\n" +
                "thread:" + Thread.currentThread() + "\n" +
                "list:" + r + "\n");
    }
}
