package es.joseluisgs.dam;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hilo extends Thread {

    //Opción a)
    //private static Semaphore sem = new Semaphore(2);
    //O también
    //Opción b)
    private Semaphore sem;

    public Hilo(Semaphore sem) {
        this.sem = sem;
    }

    //    @Override private Semaphore sem;
//
//    public Hilo(Semaphore sem) {
//        this.sem = sem;
//    }
    public void run() {
        int espera = 0;

        while (true) {
            try {
                sem.acquire();
                //if (sem.tryAcquire()) {
                //if (sem.tryAcquire(10, TimeUnit.SECONDS)){
                System.out.println("Semáforo adquirido por " + this.getName());
                espera = (int) (Math.random() * 2000 + 500);
                System.out.println(this.getName() + " procesando acción de espera " + espera);
                System.out.println("Semáforo liberado por " + this.getName());
                sleep(espera);
                sem.release();
                // } else {
                //    System.out.println("No lo pillo " + this.getName());
                // }
                espera = (int) (Math.random() * 500);
                sleep(espera);

            } catch (InterruptedException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
