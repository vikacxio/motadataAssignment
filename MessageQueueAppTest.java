public class MessageQueueAppTest {

    public static void main(String[] args) {
        System.out.println("Starting message queue tests...");

        testSuccessfulMessageProcessing();
        testErrorInMessageProcessing();
        testQueueOverflow();
        testEmptyQueueConsumption();

        System.out.println("All tests completed.");
    }

    private static void testSuccessfulMessageProcessing() {
        try {
            System.out.println("Running testSuccessfulMessageProcessing...");
            MessageQueueApp.processMessage("Test Message");
            System.out.println("Test successful: No errors during processing.");
        } catch (Exception e) {
            System.err.println("Test failed: Exception encountered.");
            e.printStackTrace();
        }
    }

    private static void testErrorInMessageProcessing() {
        try {
            System.out.println("Running testErrorInMessageProcessing...");
            MessageQueueApp.processMessage("ForceErrorMessage");
            System.out.println("Test failed: Expected an error but none occurred.");
        } catch (Exception e) {
            System.out.println("Test successful: Error encountered as expected.");
        }
    }

    private static void testQueueOverflow() {
        System.out.println("Running testQueueOverflow...");
        try {
            for (int i = 0; i < 15; i++) { 
                synchronized (MessageQueueApp.messageQueue) {
                    if (MessageQueueApp.messageQueue.size() < 10) {
                        MessageQueueApp.messageQueue.add("Overflow Test Message " + i);
                    }
                }
            }
            System.out.println("Test passed: Queue overflow handling successful.");
        } catch (Exception e) {
            System.err.println("Test failed: Unexpected exception in queue overflow.");
            e.printStackTrace();
        }
    }

    private static void testEmptyQueueConsumption() {
        System.out.println("Running testEmptyQueueConsumption...");
        try {
            synchronized (MessageQueueApp.messageQueue) {
                if (MessageQueueApp.messageQueue.isEmpty()) {
                    System.out.println("Test passed: No errors when queue is empty.");
                }
            }
        } catch (Exception e) {
            System.err.println("Test failed: Unexpected exception when consuming from an empty queue.");
            e.printStackTrace();
        }
    }
}

