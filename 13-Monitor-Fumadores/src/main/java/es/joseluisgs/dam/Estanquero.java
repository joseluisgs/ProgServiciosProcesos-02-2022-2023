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
public class Estanquero implements Runnable{
    /*
        Mesa común al estanquero y los fumadores
        Aquí se pondrán los ingredientes
    */
    private final Mesa mesa;
	
    /**
     * Constructor de la clase Estanquero
     * @param mesa Mesa común al estanquero y los fumadores
     */
    public Estanquero (Mesa mesa){
        this.mesa = mesa;
    }
	
    @Override
    /**
     * Implementación de la hebra
     */
    public void run (){
        while(Boolean.TRUE){
            mesa.ponerLaMesa();
        }
    }
    
}