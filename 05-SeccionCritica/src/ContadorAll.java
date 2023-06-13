import java.util.concurrent.atomic.AtomicInteger;

public class ContadorAll implements Runnable {
    private AtomicInteger atomic = new AtomicInteger(0);
    private int synchronizedCounter = 0;
    private int counter = 0;


    public void incrementAtomicCounter() {
        try {
            Thread.sleep(10);
            atomic.getAndIncrement();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void decrementAtomicCounter() {
        atomic.getAndDecrement();
    }

    public int getAtomicCounter() {
        return atomic.get();
    }


    public synchronized void incrementSynchronizedCounter() {
        try {
            Thread.sleep(10);
            synchronizedCounter++;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void decrementSynchronizedCounter() {
        synchronizedCounter--;
    }

    public int getSynchronizedCounter() {
        return synchronizedCounter;
    }

    public void incrementCounter() {
        try {
            Thread.sleep(10);
            counter++;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void decrementCounter() {
        counter--;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void run() {
        this.incrementCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras incrementar Sin proteccíon -> " + this.getCounter());
        this.decrementCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras decrementar Sin protección -> " + this.getCounter());

        this.incrementAtomicCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras incrementar Atomic -> " + this.getAtomicCounter());
        this.decrementAtomicCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras decrementar Atomic -> " + this.getAtomicCounter());

        this.incrementSynchronizedCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras incrementar Synchronized -> " + this.getSynchronizedCounter());
        this.decrementSynchronizedCounter();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras decrementar Synchronized -> " + this.getSynchronizedCounter());

    }
}
