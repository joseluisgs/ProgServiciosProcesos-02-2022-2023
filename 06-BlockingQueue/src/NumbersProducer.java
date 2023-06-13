import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class NumbersProducer implements Runnable {

    private final BlockingQueue<Integer> numbersQueue;
    private final int poisonPill;
    private final int poisonPillPerProducer;

    NumbersProducer(BlockingQueue<Integer> numbersQueue, int poisonPill, int poisonPillPerProducer) {
        this.numbersQueue = numbersQueue;
        this.poisonPill = poisonPill;
        this.poisonPillPerProducer = poisonPillPerProducer;
    }

    public void run() {
        try {
            generateNumbers();
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
        }
    }

    private void generateNumbers() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread()
                    .getName() + " produzco una pildora ");
            numbersQueue.put(ThreadLocalRandom.current()
                    .nextInt(100));
            System.out.println("--> El tamaño de mi almacen es " + numbersQueue.size());
        }
        for (int j = 0; j < poisonPillPerProducer; j++) {
            System.out.println(Thread.currentThread()
                    .getName() + " produzco el veneno ");
            numbersQueue.put(poisonPill);
            System.out.println("--> El tamaño de mi almacen es " + numbersQueue.size());
        }
    }
}