package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        final int CANTIDADFUMADORES = 3;
        Mesa mesa;
        Thread estanquero;
        Thread fumadores[];

        mesa = new Mesa(CANTIDADFUMADORES);
        fumadores = new Thread[CANTIDADFUMADORES];

        // Creo los fumadores
        for (int i = 0; i < CANTIDADFUMADORES; i++) {
            String nombrefumador = "Fumador " + i;
            fumadores[i] = new Thread(new Fumador(nombrefumador, i, mesa));
        }

        // Inicio los fumadores
        for (int i = 0; i < CANTIDADFUMADORES; i++) {
            fumadores[i].start();
        }

        // Creo e inicio al estanquero
        estanquero = new Thread(new Estanquero(mesa));
        estanquero.start();
    }
}
