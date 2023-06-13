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
public class Secadero extends LinkedList<Jamon>{
    private boolean jamonDisponible = false;
    private int maxJamones;
    private Jamon s;
    
    public Secadero(int max){
        this.maxJamones = max;
    }
    
    public synchronized Jamon sacar(){
        while((this.size()==0)) {
            try {
            // Si no hay hay sacar, esperamos, 
                wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        } 

        s = this.removeFirst(); // Saco el primero
        System.out.println("\t*El secadero tiene: " + this.size());
        jamonDisponible=false;
        // Activamos
        notifyAll();

        return s; // DEvolvemos el jamon
    }	

    public synchronized void meter(Jamon s){	
        while((this.size()==this.maxJamones)) {	// Condición de memoria limitada
            try {
                // Si no hay que producir esperamos 
               wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        this.addLast(s); //Metemos al final
        System.out.println("\t\tEl secadero tiene: " + this.size());
        jamonDisponible=true;
        // Ya hay cantidas a consumir, activamos.
        notifyAll();

    }
}
