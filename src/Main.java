public class Main {
    public static void main(String[] args) {
        int tellers = 3;
        int bankmaxLength = 5;
        int grocerymaxLength = 2;
        int numQueues = 3;
        int simulationMinutes = 2;
        int simulationSeconds = simulationMinutes * 60;
        GroceryQueue groceryQueue = new GroceryQueue(numQueues, grocerymaxLength);
        BankQueue bankQueue = new BankQueue(tellers, bankmaxLength);
        QueueSimulator simulator = new QueueSimulator(bankQueue, groceryQueue, simulationSeconds);
        simulator.startSimulation();
    }
}
