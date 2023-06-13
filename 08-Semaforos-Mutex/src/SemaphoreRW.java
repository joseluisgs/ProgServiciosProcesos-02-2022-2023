import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;

class SemaphoreRW {

    static String resource = "";
    static int readerCount = 0;
    static Semaphore x = new Semaphore(1);
    static Semaphore rsem = new Semaphore(1);
    static Semaphore wsem = new Semaphore(1);

    static class Read implements Runnable {
        @Override
        public void run() {
            try {
                rsem.acquire();
                x.acquire();
                readerCount++;
                if (readerCount == 1) wsem.acquire();
                x.release();

                System.out.println("Thread "+Thread.currentThread().getName() + " está leyendo");
                Thread.sleep(1500);
                System.out.println("Thread "+Thread.currentThread().getName() + resource);
                System.out.println("Thread "+Thread.currentThread().getName() + " ha finalizado de leer");

                x.acquire();
                readerCount--;
                if (readerCount == 0) wsem.release();
                x.release();
                rsem.release();

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static class Write implements Runnable {
        @Override
        public void run() {
            try {
                rsem.acquire();
                wsem.acquire();
                System.out.println("Thread "+Thread.currentThread().getName() + " esta escribiendo");
                Thread.sleep(2500);
                resource = " Tiene el valor " + LocalDateTime.now();
                System.out.println("Thread "+Thread.currentThread().getName() + " ha finalizado de escribir");
                wsem.release();
                rsem.release();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Read read = new Read();
        Write write = new Write();
        Thread t1 = new Thread(read);
        t1.setName("thread1");
        Thread t2 = new Thread(read);
        t2.setName("thread2");
        Thread t3 = new Thread(write);
        t3.setName("thread3");
        Thread t4 = new Thread(read);
        t4.setName("thread4");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
}