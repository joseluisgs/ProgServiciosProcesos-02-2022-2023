/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 *
 * @author link
 */
public class CienMontaditos {
    private static CienMontaditos bar;
    
    //Vamos a tener dos camareros y le pasamos el cubo (recurso compartido)
    private static final int NCAMAREROS = 2;
    private Camarero[] camareros = new Camarero[NCAMAREROS];

    //Vamos a tener 6 alumnos
    private static final int NALUMNOS = 6;
    private Alumno[] alumnos = new Alumno[NALUMNOS];
    // Recurso compartido es el cubo, 
    
    // Creamos el cubo de cervezas (el límite que le vamos a poner es de 5 botellines)
    private static final int NBOTELLINES = 5;
    private Cubo cubo = new Cubo(NBOTELLINES);

    private CienMontaditos() {
        
    }
    //Patron Singleton
    /**
     * Patrón singleton para asegurar una sola instancia
     * @return bar
     */
    public static CienMontaditos nuevaInstancia() {
        if (bar== null){
            bar = new CienMontaditos();
        }
        else{
            //System.out.println("No se puede crear el objeto "+ nombre + " porque ya existe un objeto de la clase SoyUnico");
        }       
        return bar;
    }
    
    /**
     * Crea los camareros y los echa a trabajar. Usar un Pool
     */
    private void crearCamareros(){
           // Creamos los Camareros 
        // Le asignamos prioridad, un camarero será más "efiencte" que otro
        for(int i = 0; i<NCAMAREROS;i++){
            camareros[i] = new Camarero(cubo, i+1);
            camareros[i].setPriority(i+1);
            // Los inicializamos
            camareros[i].start();
            System.out.println("Camarero " + (i+1) + " activado y listo para servir");
			//Damos una pausa entre cada generacion de productor
                try {
                    Thread.sleep(100);
		} catch (Exception e) {
                    e.printStackTrace();
		}
        }
	//Creamos una pausa de 1 segundo entre la inicializacion
	//de productores y consumidores
	try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
	}
        
    }
    
    /**
     * Crea a los allumnos con unas características determinadas
     */
    private void crearAlumnos(){
        // Creamos los alumnos. De hecho habrá mas alumnos que botellines, veremos 
        // quien se fastidia sin beber y cuantas rondas lo hará
        // También le decimos los botellines que cada uno tomará
        alumnos[0] = new Alumno(cubo, "Jose", 6, 3000);
        alumnos[0].setPriority(7); 
        
        alumnos[1] = new Alumno(cubo, "Boris", 7, 2000);
        alumnos[1].setPriority(7); 
        
        alumnos[2] = new Alumno(cubo, "Javier", 7, 2000);
        alumnos[2].setPriority(8); 
        
        alumnos[3] = new Alumno(cubo, "Juan", 7, 2000);
        alumnos[3].setPriority(8); 

        alumnos[4] = new Alumno(cubo, "Victor", 8, 1700); // El que más bebe y más rápido
        alumnos[4].setPriority(9); 

        alumnos[5] = new Alumno(cubo, "Andres", 7, 2500);
        alumnos[5].setPriority(9); 
        
        for(int i =0; i<NALUMNOS;i++){
            alumnos[i].start();
            System.out.println("Alumno " + alumnos[i].getNombre() + " activado y listo para beber");
			//Damos una pausa entre cada generacion de productor
                try {
                    Thread.sleep(100);
		} catch (Exception e) {
                    e.printStackTrace();
		}
        }
        
        // Vamos a hacer un Join de las hebras de alumnos
        // antes de cortar el programa
        for(int i=0; i<NALUMNOS;i++){
            try {
                alumnos[i].join();
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    /**
     * Fin de la simulación
     */
    private void fin(){
        System.exit(0);
    }
    
    /**
     * Pone en marcha la simulación
     */
    void iniciarSimulacion() {
        this.crearCamareros();
        this.crearAlumnos();
        this.fin();
    }
    
}
