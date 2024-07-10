public class Main {
    public static void main(String[] args) {
        int tellers = 3;
        int maxLength = 5;
        int simulationMinutes = 200;
        int simulationSeconds = simulationMinutes / 10;
        BankQueue bankQueue = new BankQueue(maxLength);
        QueueSimulator simulator = new QueueSimulator(bankQueue, simulationSeconds, tellers);
        simulator.startSimulation();
    }
}