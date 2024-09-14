import java.util.Random;

public class QueueSimulator {
    private final BankQueue bankQueue;
    private final GroceryQueue groceryQueue;
    private final int simulationTime; // in seconds
    private final Random random = new Random();
    private volatile boolean simulationRunning = true;
    private int tellers;
    private int cashiers;
    private int k = 0;

    public QueueSimulator(BankQueue bankQueue, GroceryQueue groceryQueue, int simulationTime) {
        this.bankQueue = bankQueue;
        this.groceryQueue = groceryQueue;
        this.simulationTime = simulationTime;
    }

    public void startSimulation() {
        Thread simulationThread = new Thread(this::simulate);
        Thread bankcustomerArrivalThread = new Thread(this::bankcustomerArrival);
        Thread grocerycustomerArrivalThread = new Thread(this::grocerycustomerArrival);
        tellers = bankQueue.getTeller();
        Thread[] tellerThreads = new Thread[tellers];
        cashiers = groceryQueue.getCashier();
        Thread[] cashierThreads = new Thread[cashiers];
        simulationThread.start();
        bankcustomerArrivalThread.start();
        grocerycustomerArrivalThread.start();
        for (int i = 0; i < tellers; i++) {
            tellerThreads[i] = new Thread(this::servebankCustomer);
            tellerThreads[i].start();
        }
        for (int i = 0; i < cashiers; i++) {
            int cashierId = i;
            cashierThreads[i] = new Thread(() -> serveGroceryCustomer(cashierId));
            cashierThreads[i].start();
        }
        try {
            simulationThread.join();
            bankcustomerArrivalThread.join();
            grocerycustomerArrivalThread.join();
            for (Thread tellerThread : tellerThreads) {
                tellerThread.join();
            }
            for (Thread cashierThread : cashierThreads) {
                cashierThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void simulate() {
        while (k < simulationTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            k++;
        }
        simulationRunning = false;
        bankQueue.signalAll();
        groceryQueue.signalAll();
        System.out.println("Simulation ended");
        System.out.println("***Bank Queue***");
        System.out.println("Total Customers Arrived: " + bankQueue.getArrival());
        System.out.println("Total Customers served: " + bankQueue.getserved());
        System.out.println("Total Customers left: " + bankQueue.getleft());
        System.out.println("Remaining Customer in the queue: " + bankQueue.queueSize());
        System.out.println("***Grocery Queue***");
        System.out.println("Total Customers Arrived: " + groceryQueue.getArrival());
        System.out.println("Total Customers served: " + groceryQueue.getserved());
        System.out.println("Total Customers left: " + groceryQueue.getleft());
    }

    private void bankcustomerArrival() {
        while (simulationRunning) {
            Customer customer = new Customer(k);
            if (!bankQueue.addCustomer(customer)) {
                bankQueue.incrementLeft();
            }
            try {
                bankQueue.incrementArrival();
                Thread.sleep((2 + random.nextInt(5)) * 1000L); // sleep for 2 to 6 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void grocerycustomerArrival() {
        while (simulationRunning) {
            Customer customer = new Customer(k);
            if (!groceryQueue.addCustomer(customer)) {
                try {
                    Thread.sleep(10 * 1000L); // sleep for 10 seconds.karon queue khali na thakle 10 sec wait korbe
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!groceryQueue.addCustomer(customer)) {
                    groceryQueue.incrementLeft();
                } else {
                    groceryQueue.incrementArrival();
                }
            } else {
                groceryQueue.incrementArrival();
            }
            try {
                Thread.sleep((2 + random.nextInt(5)) * 1000L); // sleep for 2 to 6 seconds for new customer arrival
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void servebankCustomer() {
        while (simulationRunning) {
            try {
                Customer customer = bankQueue.getNextCustomer();
                if (customer != null) {
                    Thread.sleep(customer.getServiceTime() * 1000L); // service time ekdom first ei constructor dye set
                                                                     // kra ase randomly
                    bankQueue.incrementServed();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void serveGroceryCustomer(int cashierId) {
        while (simulationRunning) {
            try {
                Customer customer = groceryQueue.getNextCustomer(cashierId);
                if (customer != null) {
                    // System.out.println("New customer" + customer.getArrivalTime());
                    Thread.sleep(customer.getServiceTime() * 1000L); // same as bankQ
                    groceryQueue.incrementServed();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
