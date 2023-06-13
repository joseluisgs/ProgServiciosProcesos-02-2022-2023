class Contador implements Runnable {
    private int c = 0;

    // Nos paramos antes de incrementar, por gusto...
    public void increment() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        c++;
    }

    // Decrementamos
    public void decrement() {
        c--;
    }

    // obtenemos el valor
    public int getValue() {
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