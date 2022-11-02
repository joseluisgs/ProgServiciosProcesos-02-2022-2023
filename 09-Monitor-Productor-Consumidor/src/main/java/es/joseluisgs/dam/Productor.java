/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Random;

/**
 *
 * @author link
 */
public class Productor implements Runnable{
    private final Random aleatorio;
    private final Contenedor contenedor;
    private final int idproductor;
    private final int TIEMPOESPERA = 1500;
 
    /**
     * Constructor de la clase
     * @param contenedor Contenedor común a los consumidores y el productor
     * @param idproductor Identificador del productor
     */
    public Productor(Contenedor contenedor, int idproductor){
        this.contenedor = contenedor;
        this.idproductor = idproductor;
        aleatorio = new Random();
    }
 
    @Override
    /**
     * Implementación de la hebra
     */
    public void run(){
        while(Boolean.TRUE){
            int poner = aleatorio.nextInt(300);
            contenedor.put(poner);
            System.out.println("El productor " + idproductor + " pone: " + poner);
            try{
                Thread.sleep(TIEMPOESPERA);
            } 
            catch (InterruptedException e){
                System.err.println("Productor " + idproductor + ": Error en run -> " + e.getMessage());
            }
        }
    }
}
