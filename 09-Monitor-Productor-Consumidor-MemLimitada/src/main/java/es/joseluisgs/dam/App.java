package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args) {
        int ranuras = 10; // Tamaño de la memoria compartida
        Buffer monitor = new Buffer (ranuras);

        Productor p = new Productor(monitor);
        Consumidor c = new Consumidor(monitor);
        p.start();
        c.start();

    }

}
