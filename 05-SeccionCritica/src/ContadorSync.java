class ContadorSync implements Runnable {
    private int c = 0;

    // Nos paramos antes de incrementar, por gusto...
    public synchronized void increment() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c++;
    }

    // Decrementamos
    public synchronized void decrement() {
        c--;
    }

    // obtenemos el valor
    public synchronized int getValue() {
        return c;
    }

    @Override
    public void run() {
        this.increment();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras incrementar -> " + this.getValue());
        this.decrement();
        System.out.println(Thread.currentThread().getName() + " -> Valor tras decrementar -> " + this.getValue());
    }
}  