import java.util.Random;

public class Customer {
    private static final Random random = new Random();
    private final int arrivalTime;
    private final int serviceTime;
    private boolean served;
    private boolean left;

    public Customer(int arrivalTime) {
        this.arrivalTime = arrivalTime;
        // System.out.println(this.arrivalTime);
        this.serviceTime = 30 + random.nextInt(10); // 6 to 15 seconds
        this.served = false;
        this.left = false;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }
}
