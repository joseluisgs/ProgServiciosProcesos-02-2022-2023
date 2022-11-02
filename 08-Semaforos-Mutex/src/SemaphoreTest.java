import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    // max 4
    static Semaphore semaphore = new Semaphore(4);

    static class MyATMThread extends Thread {

        String name = "";

        MyATMThread(String name) {
            this.name = name;
        }

        public void run() {

            try {


                System.out.println(name + " : adquiere el cerrojo...");
                System.out.println(name + " : Permisos de semáforo ahora: "
                        + semaphore.availablePermits());

                semaphore.acquire();
                // Aqui es la seción crítica
                System.out.println(name + " : tengo permiso!");

                try {

                    for (int i = 1; i <= 5; i++) {

                        System.out.println(name + " : realizando operación " + i
                                + ", permisos de semáforo ahora : "
                                + semaphore.availablePermits());

                        // sleep 1 second
                        Thread.sleep(1000);

                    }

                } finally {

                    // debes llamar a release() despues de un acquire()
                    System.out.println(name + " : liberando el cerrojo...");
                    semaphore.release(); // Lo libero
                    System.out.println(name + " : Permisos de semáforo ahora: "
                            + semaphore.availablePermits());

                }

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

    public static void main(String[] args) {

        System.out.println("Total de Permisos de semáforo ahora : "
                + semaphore.availablePermits());

        MyATMThread t1 = new MyATMThread("A");
        t1.start();

        MyATMThread t2 = new MyATMThread("B");
        t2.start();

        MyATMThread t3 = new MyATMThread("C");
        t3.start();

        MyATMThread t4 = new MyATMThread("D");
        t4.start();

        MyATMThread t5 = new MyATMThread("E");
        t5.start();

        MyATMThread t6 = new MyATMThread("F");
        t6.start();


    }
}