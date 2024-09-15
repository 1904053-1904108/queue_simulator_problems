import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GroceryQueue {
    private List<Queue<Customer>> queues;
    private final int maxLength;
    private final int numQueues;
    private int left = 0;
    private int served = 0;
    private int arrived = 0;
    private final Lock lock;
    private final Condition notEmpty;

    public GroceryQueue(int numQueues, int maxLength) {
        this.maxLength = maxLength;
        this.numQueues = numQueues;
        this.queues = new ArrayList<>(numQueues);
        for (int i = 0; i < numQueues; i++) {
            queues.add(new LinkedList<>());
        }
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
    }

    public boolean addCustomer(Customer customer) {
        lock.lock();
        try {
            Queue<Customer> selectedq = queues.get(0);
            for (int i = 1; i < queues.size(); i++) {
                if (queues.get(i).size() < selectedq.size()) {
                    selectedq = queues.get(i);
                }
            }

            if (selectedq.size() < maxLength) {
                selectedq.add(customer);
                notEmpty.signal();
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public Customer getNextCustomer(int i) throws InterruptedException {
        lock.lock();
        try {
            while (queues.get(i).isEmpty()) {
                if (!queues.get(i).isEmpty()) {
                    return queues.get(i).poll();
                }
                notEmpty.await(); // wait for a customer to arrive.Other thread will signal him to wake
            }
            return queues.get(i).poll();
        } finally {
            lock.unlock();
        }
    }

    public int getCashier() {
        return numQueues;
    }

    public void incrementLeft() {
        lock.lock();
        try {
            left++;
        } finally {
            lock.unlock();
        }
    }

    public void incrementArrival() {
        lock.lock();
        try {
            arrived++;
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

    public void signalAll() {
        lock.lock();
        try {
            notEmpty.signalAll();
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

    public int getArrival() {
        return arrived;
    }
}
