package es.joseluisgs.dam;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int peso = 5;
        int numero = 5;
        Tenedor[] tenedores = new Tenedor[numero];
        Filosofo[] filosofos = new Filosofo[numero];
        for (int i = 0; i < numero; i++) {
            tenedores[i] = new Tenedor();
        }
        for (int i = 0; i < numero; i++) {
            // Solo funciona con notify, no con notifyAll
            filosofos[i] = new Filosofo(tenedores[i], tenedores[(i+1)%numero],i,peso);
            filosofos[i].start();
            // Para romper el interbloqueo circular si usamos notifyAll
            /*
             if (i < (numero - 1)) {
                filosofos[i] = new Filosofo(tenedores[i], tenedores[i+1],i,peso);
                filosofos[i].start();
            }else{
               filosofos[i] = new Filosofo(tenedores[0], tenedores[i],i,peso);
               filosofos[i].start();
            }
            */

        }
    }
}
