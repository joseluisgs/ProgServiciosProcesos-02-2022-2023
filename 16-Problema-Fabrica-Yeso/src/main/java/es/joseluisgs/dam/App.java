package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FabricaYeso fabrica = FabricaYeso.nuevaInstancia();
        fabrica.fabricarSacos();
    }
}
