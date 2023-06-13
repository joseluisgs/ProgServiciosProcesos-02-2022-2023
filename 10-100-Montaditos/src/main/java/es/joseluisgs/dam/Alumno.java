/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 * Clase Alumno, que es el consumidor de Botellines
 * @author link
 */
public class Alumno extends Thread {
    private final Cubo cubo; // Recurso compartido
    private String nombre; // Nombre del alumno
    private int numBotellines; // Numero de Botellines que consumirá
    
    //Tiempo que le llevará consumir un botellín
    private int tEspera = 2000;
    
    // Vamos a mirar el tiempo de cada uno para sacar cuentas
    private long tInicio;
    private long tFin;
    
    /**
     * Constructor de alumno
     * @param cubo Recurso compartido, cubo de cervezas
     * @param nombre Nombre del alumno
     * @param numBotellines Nuúmero de botellines que consumirá
     * @param tEspera Tiempo en beber 
     */
    public Alumno(Cubo cubo, String nombre, int numBotellines, int tEspera) {
        this.cubo = cubo;
        this.nombre = nombre;
        this.numBotellines = numBotellines;
        this.tEspera = tEspera;
     }
    
    /**
     * Devuelve el nombre del alumno
     * @return Nombre del alumno
     */
    public String getNombre(){
        return this.nombre;
    }
    
    /**
     * Metodo run de la hebra
     */
    @Override
    public void run() {
        // Tomamos el tiempo de inicio
        this.tInicio = System.currentTimeMillis();
        
        // Repetiremos tantas veces como botellines queremos consumir
        for(int i=0; i<this.numBotellines;i++){
            // Cogemos el botellin (nos hará esperar si no lo tenemos)
            cubo.coger(this.nombre);
            this.tFin= System.currentTimeMillis();
            System.out.println("El alumno " + this.nombre + " ha consumido el botellin nº "+(i+1)+" a los " + ((this.tFin-this.tInicio)/1000)+"s");
            //System.out.println("El cubo tiene ahora: " + cubo.getBotellines());   
            try {
                Thread.sleep(this.tEspera*i); // tiempo en beber, cada vez nos cuesta más beber
            } catch (Exception e) {
                e.printStackTrace();
            }
           
        } // for
        // Fin 
        this.tFin= System.currentTimeMillis();
        System.out.println("El alumno " + this.nombre + " ha terminado de consumir sus botellines a los " + ((this.tFin-this.tInicio)/1000)+"s");
    }
}
