package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        final int NUM_SILLAS = 4;
        final int NUM_CLIENTES = 10;

        Barberia barberia = new Barberia(NUM_SILLAS);
        Barbero barbero = new Barbero(barberia);
        Cliente[] Clientes = new Cliente[10];

        barbero.start();

        for (int i = 0; i < NUM_CLIENTES; i++) {
            Clientes[i] = new Cliente(barberia, i);
            Clientes[i].start();
        }

    }

}
