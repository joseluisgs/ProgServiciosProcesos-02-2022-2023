/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.LinkedList;

/**
 *
 * @author joseluisgs
 */
public class Pila extends LinkedList<Saco>{
    boolean sacosDisponible = false;
    private int numeroSacos = 0;
    private Saco s;

    public synchronized Saco empaquetar(){
        while(!sacosDisponible && isEmpty()) {
            try {
            // Si no hay hay empaquetar, esperamos
                wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        } 

        s = this.removeFirst();
        sacosDisponible=false;
        // Activamos
        notifyAll();

        return s;
    }	

    public synchronized void apilar(Saco s){	
        while(sacosDisponible && !isEmpty()) {	
            try {
                // Si no hay que producir esperamos 
               wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        add(s);
        sacosDisponible=true;
        // Ya hay cantidas a consumir, activamos.
        notifyAll();

    }
}
