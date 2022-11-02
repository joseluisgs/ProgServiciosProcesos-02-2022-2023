package es.joseluisgs.dam;

public class Cliente extends Thread{
    private Barberia barberia;
    private int clienteId;
    private boolean cortePelo = false;

    public Cliente(Barberia barberia, int clienteId) {
        this.barberia = barberia;
        this.clienteId = clienteId;
    }

    public void run() {
        // Vamos entrando, si lo queremos limitar ponemos las veces o solo una vez...
        while (true) {
            try {
                Thread.sleep(2000);
                cortePelo = barberia.entrar(clienteId);
                // Cortar pelo
                if (cortePelo) {
                    // Espero hasta que me crezca el pelo
                    Thread.sleep(25000);
                } else {
                    // Espero y lo vuelvo a intentar
                    Thread.sleep(4000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
