import java.util.concurrent.atomic.AtomicInteger;

class ContadorAtomic implements Runnable {
    private AtomicInteger c = new AtomicInteger(0);

    // Nos paramos antes de incrementar, por gusto...
    public void increment() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c.incrementAndGet();
    }

    // Decrementamos
    public void decrement() {
        c.decrementAndGet();
    }

    // obtenemos el valor
    public int getValue() {
        return c.get();
    }

    @Override
    public void run() {
        this.increment();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras incrementar -> " + this.getValue());
        this.decrement();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras decrementar -> " + this.getValue());
    }
}  