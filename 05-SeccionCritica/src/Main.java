import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        System.out.println("El infierno de las Sección Crítica (SC) y las Condiciones de Carrera (CC)");
        System.out.println("=======================================================================");

        condicionesCarrera();
        valoresAtomicos();
        metodosSincronizados();
        // Si ya te cueesta verlo así, puedes probar a descomentar la siguiente línea
        metodosAll();

        // Ahora con colecciones
        // Fíjate el comportamiento de cada una...
        arrayLista();
        //blockingQueue();
    }


    private static void condicionesCarrera() {
        System.out.println("Ejemplo de condiciones de carrera");
        Contador contador = new Contador(); // Zona de memoria compartida
        Thread t1 = new Thread(contador, "Thread-1");
        Thread t2 = new Thread(contador, "Thread-2");
        Thread t3 = new Thread(contador, "Thread-3");
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Creando Thread savings
    private static void valoresAtomicos() {
        System.out.println("Ejemplo con valores atómicos");
        ContadorAtomic contador = new ContadorAtomic(); // Zona de memoria compartida
        Thread t1 = new Thread(contador, "Thread-1");
        Thread t2 = new Thread(contador, "Thread-2");
        Thread t3 = new Thread(contador, "Thread-3");
        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void metodosSincronizados() {
        System.out.println("Ejemplo con metodos Sincronizados");
        ContadorSync contador = new ContadorSync(); // Zona de memoria compartida
        Thread t1 = new Thread(contador, "Thread-1");
        Thread t2 = new Thread(contador, "Thread-2");
        Thread t3 = new Thread(contador, "Thread-3");
        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void metodosAll() {
        System.out.println("Ejemplo con metodos all");
        ContadorAll contador = new ContadorAll(); // Zona de memoria compartida
        Thread t1 = new Thread(contador, "Thread-1");
        Thread t2 = new Thread(contador, "Thread-2");
        Thread t3 = new Thread(contador, "Thread-3");
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void arrayLista() {
        System.out.println("Ejemplo con ArrayList");
        ArrayList<Integer> lista = new ArrayList<>(5);
        //Producer thread
        new Thread(() ->
        {
            int i = 0;
            try {
                while (true) {
                    lista.add(++i);
                    System.out.println(Thread.currentThread().getName() + " -> Añade : " + i);

                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        //Consumer thread
        new Thread(() ->
        {
            try {
                while (true) {
                    Integer poll = lista.get(0);
                    lista.remove(0);
                    System.out.println(Thread.currentThread().getName() + " -> Extrae : " + poll);

                    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private static void blockingQueue() {
        System.out.println("Ejemplo con BlockingQueue");
        ArrayBlockingQueue<Integer> lista = new ArrayBlockingQueue<>(5);

        //Producer thread
        new Thread(() ->
        {
            int i = 0;
            try {
                while (true) {
                    lista.put(++i);
                    System.out.println(Thread.currentThread().getName() + " -> Añade : " + i);

                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        //Consumer thread
        new Thread(() ->
        {
            try {
                while (true) {
                    Integer poll = lista.poll();
                    System.out.println(Thread.currentThread().getName() + " -> Extrae : " + poll);

                    Thread.sleep(TimeUnit.SECONDS.toMillis(2));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
