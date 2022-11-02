/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;


public class Camello {

    private int posicion=0;

    public Camello() {
    }

   
    
    

    public int avanzar() {
        int random = (int) (Math.random() * 100 + 1);
        int avanza = 0;
        if (random <= 40) {
            avanza = (int) (Math.random() * 3 + 1);
        } else if (random <= 70) {
            avanza = (int) (Math.random() * 3 + 4);
        } else if (random <= 90) {
            avanza = (int) (Math.random() * 1 + 8);
        } else {
            avanza = 10;
        }
        return avanza;
    }

    /**
     * @return the posicion
     */
    public int getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
    
    
}
