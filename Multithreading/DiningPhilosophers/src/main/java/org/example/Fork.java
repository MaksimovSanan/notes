package org.example;

import java.util.concurrent.atomic.AtomicBoolean;

public class Fork {
    private final int id;
    private final AtomicBoolean vacancy = new AtomicBoolean(true);
    private String owner;

    public Fork(int id) {
        this.id = id;
    }

    public boolean tryOccupy() {
        if(vacancy.compareAndSet(true, false)) {
            System.out.printf("Philosopher %s occupy fork %s%n", Thread.currentThread().getName(), id);
            setOwner(Thread.currentThread().getName());
            return true;
        }
        System.out.printf("Fork %s is occupied by %s%n", id, owner);
        return false;
    }

    public void release() {
        if(vacancy.compareAndSet(false, true)) {
            System.out.printf("Philosopher %s release fork %s%n", Thread.currentThread().getName(), id);
            setOwner("");
        } else {
            System.out.printf("Philosopher %s try to release free fork %s%n", Thread.currentThread().getName(), id);
        }
    }

    public int getId() {
        return id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
