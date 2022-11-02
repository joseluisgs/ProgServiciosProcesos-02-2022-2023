package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RecursoEL RW = new RecursoEL();
        int veces = 7;
        Lector l1= new Lector(1,veces, RW);
        l1.start();
        Lector l2= new Lector(2,veces, RW);
        l2.start();
        Escritor e1= new Escritor(1,veces, RW);
        e1.start();
        Escritor e2= new Escritor(2,veces, RW);
        e2.start();
    }
}
