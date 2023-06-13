import java.util.concurrent.BlockingQueue;

public class NumbersConsumer implements Runnable {
    private final BlockingQueue<Integer> queue;
    private final int poisonPill;

    NumbersConsumer(BlockingQueue<Integer> queue, int poisonPill) {
        this.queue = queue;
        this.poisonPill = poisonPill;
    }

    public void run() {
        try {
            while (true) {
                System.out.println(Thread.currentThread().getName() + " quiero consumir una pildora");
                Integer number = queue.take(); // Si no lo tiene espera hasta que lo tenga
                if (number.equals(poisonPill)) {
                    System.out.println(Thread.currentThread().getName() + " me he comido el veneno");
                    return;
                }
                String result = number.toString();
                System.out.println(Thread.currentThread().getName() + " ha consumido: " + result);
                System.out.println("--> El tamaño de mi almacen es " + queue.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}