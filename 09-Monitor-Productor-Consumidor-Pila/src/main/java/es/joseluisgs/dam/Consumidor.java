/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Random;

/**
 *
 * @author joseluisgs
 */

// Consumidro es una hebra
public class Consumidor extends Thread {
	private Pila pila;
	private int cant,ms;
        private Random random = new Random();
        private int id;
	    
	public Consumidor (int id, Pila pila, int cant, int ms){
		this.pila = pila;
		this.cant = cant;
		this.ms = ms;
                this.id = id;
		
	}

    public void run (){
        char c;
        for (int i = 1 ; i < cant + 1; i++){
            // Consumidor puede consumir aletatoriamente 3 datos
            for(int j = 0; j<random.nextInt(3)+1; j++) {
                c = pila.sacar();
                System.out.println("Consumidor "+id+" iteraccion "+i+": "+c);
            }
            try {
                Thread.sleep(ms);
                } catch (InterruptedException e) {
                e.printStackTrace();
        }
       }
    }

}
