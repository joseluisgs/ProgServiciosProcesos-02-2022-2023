package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        // procesarCarrosSecuencial();
        // procesarCarrosConcurrentemente();
        cuantosHilosMeComo();
    }

    private static void procesarCarrosSecuencial() {
        System.out.println("Procesando secuencial");
        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3});
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });

        Cajera cajera1 = new Cajera("Cajera 1");
        Cajera cajera2 = new Cajera("Cajera 2");

        // Tiempo inicial de referencia
        long initialTime = System.currentTimeMillis();

        cajera1.procesarCompra(cliente1, initialTime);
        cajera2.procesarCompra(cliente2, initialTime);
    }

    private static void procesarCarrosConcurrentemente() {
        System.out.println("Procesando Paralelo con Hilos");
        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });

        // Tiempo inicial de referencia
        long initialTime = System.currentTimeMillis();
        CajeraHebra[] cajeras = new CajeraHebra[2];
        cajeras[0] = new CajeraHebra("Cajera 1", cliente1, initialTime);
        cajeras[1] = new CajeraHebra("Cajera 2", cliente2, initialTime);
        for (CajeraHebra cajera : cajeras) {
            cajera.start();
        }

        try {
            // Esperamos que termine el programa
            for (CajeraHebra cajera : cajeras) {
                cajera.join();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cajera cajera3 = new Cajera("Cajera 3");
        cajera3.run(); // No se utilza solo, si no con un hilo
        System.out.println("Procesando Paralelo con Hilos");

        Thread cajera4 = new Thread(new Cajera("Cajera 4"));
        cajera4.start();
        System.out.println("Procesando Paralelo con Hilos 2");


    }

    public static void cuantosHilosMeComo() {
        System.out.println("Comenzamos");
        int maximo = 10_000;
        for (int i = 0; i < maximo; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Soy un hilo" + Thread.currentThread().getName());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t.start();
        }
    }
}
