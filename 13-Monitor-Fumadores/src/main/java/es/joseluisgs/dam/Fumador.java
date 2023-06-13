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
public class Fumador implements Runnable {
    private final int ingredienteposeido;
    private final Mesa mesa;
    private final String nombrefumador;

    /**
     * Constructor de la clase Fumador
     * @param nombrefumador Nombre del fumador
     * @param ingredienteposeido Ingrediente que tiene el fumador
     * @param mesa Mesa común al estanquero y los fumadores
     */
    public Fumador(String nombrefumador, int ingredienteposeido, Mesa mesa){
        this.ingredienteposeido = ingredienteposeido;
        this.mesa = mesa;
        this.nombrefumador = nombrefumador;
    }

    @Override
    /**
     * Implementación de la hebra
     */
    public void run(){
        while(!mesa.mesaVacia())
            mesa.cogerIngredientes(nombrefumador, ingredienteposeido);             
    }
    
}