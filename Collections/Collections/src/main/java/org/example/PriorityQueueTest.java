package org.example;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityQueueTest {
    public static void priorityQueueTest() {
        Queue<Integer> queue = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(isEven(o1)) {
                    return isEven(o2)? o1.compareTo(o2) : -1;
                }
                return isEven(o2)? 1 : o1.compareTo(o2);
            }
            private boolean isEven(Integer integer) {
                return integer % 2 == 0;
            }
        });

        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);

        while(!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
