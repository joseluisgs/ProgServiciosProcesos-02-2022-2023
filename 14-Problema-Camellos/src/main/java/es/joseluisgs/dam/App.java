package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        HiloExt h1=new HiloExt("caballo 1");
        HiloExt h2=new HiloExt("caballo 2");
        HiloImplementado h3=new HiloImplementado("Caballo 3");
        HiloImplementado h4=new HiloImplementado("Caballo 4");

        h2.setPriority(Thread.MAX_PRIORITY);

        h1.start();
        h2.start();
        h3.start();
        h4.start();
    }
}
