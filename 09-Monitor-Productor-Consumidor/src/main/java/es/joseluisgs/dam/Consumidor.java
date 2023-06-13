/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;


public class Consumidor implements Runnable {
    private final Contenedor contenedor;
    private final int idconsumidor;
 
    /**
     * Constructor de la clase
     * @param contenedor Contenedor común a los consumidores y el productor
     * @param idconsumidor Identificador del consumidor
     */
    public Consumidor(Contenedor contenedor, int idconsumidor){
        this.contenedor = contenedor;
        this.idconsumidor = idconsumidor;
    }
 
    @Override
    /**
     * Implementación de la hebra
     */
    public void run(){
        while(Boolean.TRUE){
            System.out.println("El consumidor " + idconsumidor + " consume: " + contenedor.get(idconsumidor));
        }
    }
}