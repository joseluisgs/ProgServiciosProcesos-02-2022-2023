// Java code to illustrate Reentrant Locks

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;


public class Main {


    public static void main(String[] args) {
        // reentratLock();
        // lockCondiciones();
        lockReadWrite();
    }

    private static void reentratLock() {
        final int MAX_T = 2;
        ReentrantLock rel = new ReentrantLock();
        ExecutorService pool = Executors.newFixedThreadPool(MAX_T);
        Runnable w1 = new Worker(rel, "Job1");
        Runnable w2 = new Worker(rel, "Job2");
        Runnable w3 = new Worker(rel, "Job3");
        Runnable w4 = new Worker(rel, "Job4");
        pool.execute(w1);
        pool.execute(w2);
        pool.execute(w3);
        pool.execute(w4);
        pool.shutdown();
    }

    // Productor consumidor
    private static void lockCondiciones() {
        final int threadCount = 2;
        ReentrantLockWithCondition object = new ReentrantLockWithCondition();
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        service.execute(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    object.pushToStack("Item apilado: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        service.execute(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    System.out.println("Item obtenido: " + object.popFromStack());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        service.shutdown();
    }

    private static void lockReadWrite() {
        final int threadCount = 3;
        final ExecutorService service = Executors.newFixedThreadPool(threadCount);
        SynchronizedHashMapWithRWLock object = new SynchronizedHashMapWithRWLock();

        service.execute(new Thread(new SynchronizedHashMapWithRWLock.Writer(object), "Escritor"));
        service.execute(new Thread(new SynchronizedHashMapWithRWLock.Reader(object), "Lector 1"));
        service.execute(new Thread(new SynchronizedHashMapWithRWLock.Reader(object), "Lector 2"));

        service.shutdown();
    }
}

