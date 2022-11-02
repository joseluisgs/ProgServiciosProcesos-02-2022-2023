package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int matricula=1000;
        Edificio e=new Edificio();
        Thread [] coches=new Thread[600];

        for (int i = 0; i < coches.length; i++) {
            coches[i]=new Coche(matricula,e);
            matricula++;
            coches[i].start();
        }
    }
}
