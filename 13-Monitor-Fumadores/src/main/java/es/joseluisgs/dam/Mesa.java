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
public class Mesa {
    private final int TIEMPOFUMAR = 1000;
    private final int CANTIDADFUMADORES;
    private Boolean mesavacia;
    private int ingrediente01, ingrediente02;
    
    /**
     * Constructor de la clase Mesa
     * @param CANTIDADFUMADORES Cantidad de fumadores que hay
     */
    public Mesa(int CANTIDADFUMADORES){
        mesavacia = Boolean.TRUE;
        this.CANTIDADFUMADORES = CANTIDADFUMADORES;
    }
    
    /**
     * Indica si la mesa está vacía, es decir, no tiene algún ingrediente
     * @return True si falta por poner algún ingrediente
     */
    public boolean mesaVacia(){
        return !mesavacia;
    }

    /**
     * Pone en la mesa los dos ingredientes posibles de forma aleatoria
     */
    public synchronized void ponerLaMesa(){
        while (!mesavacia){
            try{
                wait();
            }
            catch (InterruptedException e){
                System.err.println ("Error en poner (Mesa): "+ e.toString());
            }
        }

        ingrediente01 = new Random().nextInt(CANTIDADFUMADORES);
        ingrediente02 = new Random().nextInt(CANTIDADFUMADORES);
        
        while(ingrediente01 == ingrediente02){
            ingrediente01 = new Random().nextInt(CANTIDADFUMADORES);
        }
        
        System.out.println ("El Estanquero ha dejado los ingredientes " + ingrediente01 + " y " + ingrediente02);
        mesavacia = Boolean.FALSE;

        notifyAll();
    }

    /**
     * Método para que un fumador obtenga los ingredientes para poder fumar
     * @param nombrefumador Nombre del fumador que quiere los ingredientes
     * @param ingredientefumador Ingrediente que posee el fumador
     */
    public synchronized void cogerIngredientes(String nombrefumador, int ingredientefumador) {
        while((mesavacia) || (ingredientefumador == ingrediente01) || (ingredientefumador == ingrediente02)) {
            try{
                wait();
            }
            catch (InterruptedException e){
                System.err.println ("Error en coger (Mesa): "+ e.toString());
            }
        }

        System.out.println ("Fumando el fumador: " + nombrefumador + ", que tiene el ingrediente: "+ ingredientefumador);
        try{
            Thread.sleep(TIEMPOFUMAR);
        }
        catch (Exception e){
            System.err.println ("Error 02 en coger (Mesa): "+ e.toString());
        }

        mesavacia = Boolean.TRUE;
        notifyAll();
    }

}