/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.ArrayList;

/**
 * @author joseluisgs
 */
public class Pila {
    ArrayList<Character> pila = new ArrayList<Character>();
    boolean itemDisponible = false;
    int TAM = 4; // Ahora tenemos memoria limitada
    private char c;

    public synchronized char sacar() {
        // Si no hay que consuimir y el tamaño es cero
        // Que diferencia es entre un while y otro, porque resuleven cosas distintas
        //while(!itemDisponible && pila.size()==0) {
        while (pila.size() == 0) {
            //if(pila.size()==0){

            try {
                // Si no hay hay consumir, esperamos
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        c = pila.remove(0);
        System.out.println("\t->Pila tiene el tamaño " + pila.size());
        itemDisponible = false;
        // Activamos
        notifyAll();

        return c;
    }

    public synchronized void poner(char c) {
        // Que diferencia hay entre un while y otro
        // Dan problemas distintos
        while (pila.size() < this.TAM && pila.size() != 0) {
            //while(pila.size()==this.TAM) {
            //while(itemDisponible && pila.size()<this.TAM && pila.size()!=0) {

            //if (pila.size()!=0){

            try {
                // Si no hay que producir esperamos
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        pila.add(c);
        System.out.println("\t->Pila tiene el tamaño " + pila.size());
        itemDisponible = true;
        // Ya hay cantidas a consumir, activamos.
        notifyAll();

    }

    public int size() {
        return this.pila.size();
    }

}