package org.example;

import static org.example.CopyOnWriteArrayListTest.copyOnWriteArrayListTest;

public class App {
    public static void main(String[] args) {

//        copyOnWriteArrayListTest();

        Thread th = new Thread(() -> System.out.println("hi"));
        th.start();
    }
}
