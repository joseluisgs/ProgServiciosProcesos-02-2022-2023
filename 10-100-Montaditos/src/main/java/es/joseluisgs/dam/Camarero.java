/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 * Camarero simula el productor
 * @author link
 */
public class Camarero extends Thread {
    private final Cubo cubo;
    private final int idCamarero;
    
    //Tiempo a la hora de poner el cubo en ms
    private final static int TIEMPOESPERA = 10000;
    
    /**
     * Constructor
     * @param cubo Recurso compartido
     * @param idCamarero id del Camarero
     */
    public Camarero(Cubo cubo, int idCamarero) {
        this.cubo = cubo;
        this.idCamarero = idCamarero;
    }
    
    /**
     * Método run de la hebra
     */
    public void  run(){
    	//Buble sin fin, bueno hasta que acabara su horario
        while(Boolean.TRUE){
            //cantidad aleatoria a colocar
            //Colocamos los seis botellines del tirón
            cubo.poner(this.idCamarero);
            System.out.println("El camarero nº" + idCamarero + " ha llenado el cubo con " + cubo.getCapacidad() + " botellines");
            //System.out.println("El cubo tiene ahora: " + cubo.getBotellines());
            try{
            	//pausa entre producciones
                Thread.sleep(TIEMPOESPERA);
            }catch (InterruptedException e) {
                System.err.println("Camarero " + idCamarero + ": Error en run -> " + e.getMessage());
            }
        }
    }
}
