import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoresPC {
    private static int index = 0;

    private static final int SIZE = 50;

    private static Integer[] queue = new Integer[SIZE];

    private static Semaphore semaphore = new Semaphore(1);
    private static Semaphore maxSemaphore = new Semaphore(SIZE); // Tamaño máximo para producir
    private static Semaphore minSemaphore = new Semaphore(0);

    private static final Random rand = new Random();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(SemaphoresPC::producer);
        executor.submit(SemaphoresPC::producer2);
        executor.submit(SemaphoresPC::consumer);
        executor.submit(SemaphoresPC::consumer2);
    }

    private static Void producer() throws InterruptedException {
        // Bucle infinito
        for (;;) {
            Thread.sleep(500);
            // Si quiero tener varios productores
            maxSemaphore.acquire(); // Solo permito 1 de este tipo
            // Y este es el de sección critica
            semaphore.acquire();
            System.out.println("Thread "+Thread.currentThread().getName() + " esta produciendo");
            var produced = rand.nextInt() % 5 + 5;
            queue[index++] = produced;

            print();
            System.out.println(" P1: " + produced);
            System.out.println("Thread "+Thread.currentThread().getName() + " ha terminado de producir");
            minSemaphore.release();
            semaphore.release();
        }
    }

    private static  Void producer2() throws InterruptedException {
        for (;;) {
            Thread.sleep(200);
            maxSemaphore.acquire(2); // Este gasta dos slots
            // sección crítica
            semaphore.acquire();

            System.out.println("Thread "+Thread.currentThread().getName() + " esta produciendo");
            var next = rand.nextInt() % 5 + 5;
            var next2 = rand.nextInt() % 5 + 5;

            queue[index++] = next;
            queue[index++] = next2;

            print();
            System.out.println(" P2: " + next + "," + next2);
            System.out.println("Thread "+Thread.currentThread().getName() + " ha terminado de producir");

            minSemaphore.release(2);

            semaphore.release();
        }
    }

    private static Void consumer() throws InterruptedException {
        for (;;) {
            Thread.sleep(300);
            minSemaphore.acquire(); // Soy el de consumidores
            // Sección crítica
            semaphore.acquire();
            System.out.println("Thread "+Thread.currentThread().getName() + " esta consumiendo");
            var eaten = queue[--index];

            print();
            System.out.println(" C1: " + eaten);
            System.out.println("Thread "+Thread.currentThread().getName() + " ha terminado de consumir");
            maxSemaphore.release();
            semaphore.release();
        }
    }

    private static Void consumer2() throws InterruptedException {
        for (;;) {
            Thread.sleep(400);
            minSemaphore.acquire(2); // Gasto dos slots
            semaphore.acquire();
            System.out.println("Thread "+Thread.currentThread().getName() + " esta consumiendo");
            var eaten = queue[--index];
            var eaten2 = queue[--index];

            print();
            System.out.println(" C2: " + eaten + "," + eaten2);
            System.out.println("Thread "+Thread.currentThread().getName() + " ha terminado de consumir");
            maxSemaphore.release(2);
            semaphore.release();
        }
    }


    private static void print(){
        for (int i = 0; i < SIZE; i++) {
            if (i >= index)
                System.out.print(" -");
            else
                System.out.print(" " + queue[i]);
        }
    }
}