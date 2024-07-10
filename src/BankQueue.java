import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankQueue {
    private final Queue<Customer> queue;
    private final int maxLength;
    private int served = 0;
    private int left = 0;
    private final Lock lock;
    private final Condition notEmpty;

    public BankQueue(int maxLength) {
        this.queue = new LinkedList<>();
        this.maxLength = maxLength;
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
    }

    public boolean addCustomer(Customer customer) {
        lock.lock();
        try {
            if (queue.size() < maxLength) {
                queue.offer(customer);
                notEmpty.signal(); // Signal a teller that a customer is available
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public Customer getNextCustomer() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (!queue.isEmpty()) {
                    return queue.poll();
                }
                notEmpty.await(); // wait for a customer to arrive.Other thread will signal him to wake
            }
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }

    public void incrementServed() {
        lock.lock();
        try {
            served++;
        } finally {
            lock.unlock();
        }
    }

    public void incrementLeft() {
        lock.lock();
        try {
            left++;
        } finally {
            lock.unlock();
        }
    }

    public int getserved() {
        return served;
    }

    public int getleft() {
        return left;
    }

    public void signalAll() {
        lock.lock();
        try {
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}