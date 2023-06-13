package es.joseluisgs.dam;

public class Barbero extends Thread {

    private Barberia barberia;

    public Barbero(Barberia barberia) {
        this.barberia = barberia;
    }

    public void run() {
        // Puede acabar cuando no haya clientes o simplemente cuando haya hecho 10...
        while (true) {
            try {
                barberia.esperarCliente();
                // Cortar pelo
                Thread.sleep(5000);
                barberia.acabarCorte();
                // Decansa un poco
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ;
        }
    }
}