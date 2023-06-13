package es.joseluisgs.dam;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ExecutionException, InterruptedException, TimeoutException {
        // Sistema operativo
        System.out.println( "Sistema operativo: " + System.getProperty("os.name") );
        // Versión de java
        System.out.println( "Versión de java: " + System.getProperty("java.version") );
        // Numero de cores del sistema
        System.out.println( "Numero de cores del sistema: " + Runtime.getRuntime().availableProcessors() );
        // Numero de hilos del sistema


       //Future1();
       //Future2();
       //Future3();
       //Future4();
       //Future5();
       //Future6();
       Future7();
    }

    private static void Future1() throws ExecutionException, InterruptedException {
        System.out.println("Ejemplo Callable y Get");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<String> callable = () -> {
            // Perform some computation
            System.out.println("Entramos en el Callable");
            Thread.sleep(2_000);
            return "Hola desde Callable";
        };

        System.out.println("Enviando Callable");
        Future<String> future = executorService.submit(callable);

        // This line executes immediately
        System.out.println("Podemos hacer otras cosas hasta que se ejecute el Callable");

        System.out.println("Obtenemos el resultado de Callable");
        // Future.get() bloquea el programa hasta que se resuleve la promesa
        String result = future.get();
        System.out.println(result);

        executorService.shutdown();
    }

    private static void Future2() throws InterruptedException, ExecutionException {
        System.out.println("Ejemplo Future y isDone");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2_000);
            return "Hola desde Callable";
        });

        // Esperamos mientras no esté terminada
        while(!future.isDone()) {
            System.out.println("Tarea no terminada...");
            Thread.sleep(200);
        }

        System.out.println("Tarea completada! Recibiendo el resultado");
        String result = future.get();
        System.out.println(result);

        executorService.shutdown();
    }

    private static void Future3() throws InterruptedException, ExecutionException {
        System.out.println("Ejemplo Future y Cancelled");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        long startTime = System.nanoTime();
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            return "Hola desde Callable";
        });

        while(!future.isDone()) {
            System.out.println("Tarea no terminada...");
            Thread.sleep(200);
            double elapsedTimeInSec = (System.nanoTime() - startTime)/1000000000.0;

            // Si pasa este determinado tiempo, la cancelamos
            if(elapsedTimeInSec > 1) {
                future.cancel(true);
            }
        }

        if(!future.isCancelled()) {
            System.out.println("Tarea completada! Recibiendo el resultado");
            String result = future.get();
            System.out.println(result);
        } else {
            System.out.println("La Tarea fue cancelada");
        }

        executorService.shutdown();
    }

    private static void Future4() throws InterruptedException, ExecutionException {
        System.out.println("Ejemplo Future y TimeOut");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executorService.submit(() -> {
                Thread.sleep(2000);
                return "Hola desde Callable";
            });

            // Le añadimos un timeout para no esperar para siempre. Nos arrojará una excepción si llega el caso
            String result = future.get(1, TimeUnit.SECONDS);
            System.out.println("Tarea completada! Recibiendo el resultado");
            System.out.println(result);
        } catch(TimeoutException e) {
            System.err.println("Ha terminado el tiempo");
        }
        executorService.shutdown();
    }

    private static void Future5() throws InterruptedException, ExecutionException {
        System.out.println("Ejemplo Future y invokeAll");
        // Ejecutamos multiples tareas y esperamos que terminen todas
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<String> task1 = () -> {
            System.out.println("Comenzando Task1");
            Thread.sleep(2000);
            System.out.println("Terminando Task1");
            return "Resultado Task1";
        };

        Callable<String> task2 = () -> {
            System.out.println("Comenzando Task2");
            Thread.sleep(1000);
            System.out.println("Terminando Task2");
            return "Resultado Task2";
        };

        Callable<String> task3 = () -> {
            System.out.println("Comenzando Task3");
            Thread.sleep(5000);
            System.out.println("Terminando Task3");
            return "Resultado Task3";
        };

        List<Callable<String>> taskList = Arrays.asList(task1, task2, task3);

        List<Future<String>> futures = executorService.invokeAll(taskList); // todas

        for(Future<String> future: futures) {
            // El resultado solo se imprime si todas las promesas/futuros se cumplen
            System.out.println(future.get());
        }

        executorService.shutdown();
    }

    private static void Future6() throws InterruptedException, ExecutionException {
        System.out.println("Ejemplo Future y invokeAny");
        // Ejecutamos multiples tareas y esperamos a que termine una
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Callable<String> task1 = () -> {
            System.out.println("Comenzando Task1");
            Thread.sleep(2000);
            System.out.println("Terminando Task1");
            return "Resultado Task1";
        };

        Callable<String> task2 = () -> {
            System.out.println("Comenzando Task2");
            Thread.sleep(1000);
            System.out.println("Terminando Task2");
            return "Resultado Task2";
        };

        Callable<String> task3 = () -> {
            System.out.println("Comenzando Task3");
            Thread.sleep(5000);
            System.out.println("Terminando Task3");
            return "Resultado Task3";
        };

        List<Callable<String>> taskList = Arrays.asList(task1, task2, task3);

        String result = executorService.invokeAny(taskList); //al menos 1

        // El resultado se imprime cuando termine ua
        System.out.println(result);

        executorService.shutdown();
    }

    public static void Future7() throws ExecutionException, InterruptedException {
        // No necesitamos  ExecutorService.
        // CompletableFuture internamente usa ForkJoinPool para implementar la asincronía
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return factorial(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return 0L;
            }
        });
        while (!completableFuture.isDone()) {
            System.out.println("CompletableFuture aun no ha terminado...");
            Thread.sleep(200);
        }
        long result = completableFuture.get();
        System.out.println("Resultado: " + result);
    }

    private static long factorial (long num) throws InterruptedException {
        Thread.sleep(2000);
        if (num >= 1) {
            return num * factorial(num - 1);
        }
        else
            return 1L;
    }


}
