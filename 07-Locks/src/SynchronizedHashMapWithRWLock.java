import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Thread.sleep;

// Ejemplo de lector escritor
public class SynchronizedHashMapWithRWLock {

    private static final Map<String, String> syncHashMap = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock(); // lectura
    private final Lock writeLock = lock.writeLock(); // escritura


    public void put(String key, String value) throws InterruptedException {

        try {
            writeLock.lock(); // bloqueo de escritura
            System.out.println(Thread.currentThread().getName() + " escribiendo: " + key + ":" + value);
            syncHashMap.put(key, value);
            sleep(1000);
        } finally {
            writeLock.unlock(); // desbloqueo de escritura
        }

    }

    public String get(String key) {
        try {
            readLock.lock(); // bloqueo de lectura
            System.out.println(Thread.currentThread().getName() + " leyendo: " + key);
            return syncHashMap.get(key);
        } finally {
            readLock.unlock(); // desbloqueo de lectura
        }
    }

    public String remove(String key) {
        try {
            writeLock.lock(); // bloqueo de escritura
            return syncHashMap.remove(key);
        } finally {
            writeLock.unlock(); // desbloqueo de escritura
        }
    }

    public boolean containsKey(String key) {
        try {
            readLock.lock(); // bloqueo de lectura
            return syncHashMap.containsKey(key);
        } finally {
            readLock.unlock(); // desbloqueo de lectura
        }
    }

    boolean isReadLockAvailable() {
        return readLock.tryLock(); // intenta bloquear de lectura
    }

    // Lector
    public static class Reader implements Runnable {

        SynchronizedHashMapWithRWLock object;

        Reader(SynchronizedHashMapWithRWLock object) {
            this.object = object;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + " Obteniendo Clave: " + i);
                object.get(String.valueOf(i));
            }
        }
    }

    // Escritor
    public static class Writer implements Runnable {

        SynchronizedHashMapWithRWLock object;

        public Writer(SynchronizedHashMapWithRWLock object) {
            this.object = object;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + " Escribiendo Clave: " + i);
                    object.put(String.valueOf(i), String.valueOf(i));
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


