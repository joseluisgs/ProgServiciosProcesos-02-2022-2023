package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{

    private static Contenedor contenedor;
    private static Thread productor;
    private static Thread [] consumidores;
    private static final int CANTIDADCONSUMIDORES = 5;

    public static void main( String[] args )
    {
        contenedor = new Contenedor();
        productor = new Thread(new Productor(contenedor, 1));
        consumidores = new Thread[CANTIDADCONSUMIDORES];

        for(int i = 0; i < CANTIDADCONSUMIDORES; i++){
            consumidores[i] = new Thread(new Consumidor(contenedor, i));
            consumidores[i].start();
        }

        productor.start();
    }

}
