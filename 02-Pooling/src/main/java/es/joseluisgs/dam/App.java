package es.joseluisgs.dam;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        // imprimirArray();
        procesarCarrosConcurrentemente();

    }

    private static void imprimirArray() {
        System.out.println("Imprimiendo números");
        // Creamos un array
        int bigArray[] = new int[100_000];
        for (int i = 0; i < bigArray.length; i++) {
            bigArray[i] = new Random().nextInt(100);
        }

        // Creamos nuestro pool y repartimos la carga de 10 en 10 para nuestras tres hilos
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            Runnable worker = new PrintArrayThread(bigArray, i, i + 10);
            executor.execute(worker);
        }
        executor.shutdown();
        // Esperamos hasta que termine
        while (!executor.isTerminated()) {
            System.out.println("Esperando a que termine...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Han terminado todos los hilos");
    }

    private static void procesarCarrosConcurrentemente() {
        System.out.println("Procesando Paralelo con Pool de 4 cajeras");
        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });
        Cliente cliente3 = new Cliente("Cliente 3", new int[] { 2, 5, 2, 1, 4 });
        Cliente cliente4 = new Cliente("Cliente 4", new int[] { 3, 3, 1, 2, 1 });


        // Tiempo inicial de referencia
        long initialTime = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Lo que va a psar es lo siguiente, creará un poll de dos hilos, y meterá luego el siguiente una vez termine uno de ellos
        CajeraHebra cajera1 = new CajeraHebra("Cajera 1", cliente1, initialTime);
        CajeraHebra cajera2 = new CajeraHebra("Cajera 2", cliente2, initialTime);
        CajeraHebra cajera3 = new CajeraHebra("Cajera 3", cliente3, initialTime);
        CajeraHebra cajera4 = new CajeraHebra("Cajera 4", cliente4, initialTime);

        executor.execute(cajera1);
        executor.execute(cajera2);
        executor.execute(cajera3);
        executor.execute(cajera4);

        executor.shutdown();
        // Esperamos hasta que termine
        while (!executor.isTerminated()) {
            System.out.println("Esperando a que termine...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Han terminado todos los hilos");
    }
}
