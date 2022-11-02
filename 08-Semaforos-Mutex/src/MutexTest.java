import java.util.concurrent.Semaphore;

public class MutexTest {

    // es binario!!! solo 1
    static Semaphore semaphore = new Semaphore(1);

    static class MyLockerThread extends Thread {

        String name = "";

        MyLockerThread(String name) {
            this.name = name;
        }

        public void run() {

            try {

                System.out.println(name + " : adquiere el cerrojo...");
                System.out.println(name + " : Permisos de semáforo ahora: "
                        + semaphore.availablePermits());

                semaphore.acquire();
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
                    System.out.println(name + " : liberando cerrojo...");
                    semaphore.release();
                    System.out.println(name + " : Permisos de semáforo ahora: "
                            + semaphore.availablePermits());

                }

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

    }

    public static void main(String[] args) {

        System.out.println("Total Permisos de semáforo ahora : "
                + semaphore.availablePermits());

        MyLockerThread t1 = new MyLockerThread("A");
        t1.start();

        MyLockerThread t2 = new MyLockerThread("B");
        t2.start();

        MyLockerThread t3 = new MyLockerThread("C");
        t3.start();

        MyLockerThread t4 = new MyLockerThread("D");
        t4.start();

        MyLockerThread t5 = new MyLockerThread("E");
        t5.start();

        MyLockerThread t6 = new MyLockerThread("F");
        t6.start();

    }
}