import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class MessageQueueApp {


    public static  Queue<String> messageQueue = new LinkedList<>();
    private static final int QUEUE_CAPACITY = 10;
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Producer 
        Runnable producer = () -> {
            for (int i = 1; i <= 50; i++) {
                synchronized (messageQueue) {
                    while (messageQueue.size() == QUEUE_CAPACITY) {
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    String message = "Message " + i;
                    messageQueue.add(message);
                    System.out.println("Produced: " + message);
                    messageQueue.notifyAll();
                }
            }
        };

     
        Runnable consumer = () -> {
            while (true) {
                synchronized (messageQueue) {
                    while (messageQueue.isEmpty()) {
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    String message = messageQueue.poll();
                    try {
                        processMessage(message);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        System.err.println("Error processing message: " + message);
                    }
                    messageQueue.notifyAll();
                }
            }
        };

        executor.execute(producer);
        executor.execute(consumer);

        executor.shutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            System.out.printf("Total messages processed successfully: %d%n", successCount.get());
            System.out.printf("Total errors encountered: %d%n", errorCount.get());
        }));
    }


    public static void processMessage(String message) throws Exception {
        if (Math.random() < 0.02) {
            throw new Exception("Simulated processing error");
        }
        System.out.println("Consumed: " + message);
    }
}

