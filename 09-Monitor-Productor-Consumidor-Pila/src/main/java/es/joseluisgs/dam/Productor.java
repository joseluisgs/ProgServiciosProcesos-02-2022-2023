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
public class Productor extends Thread {
    private Pila pila;
    private String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private char c;
    private Random random = new Random();
    private int cant, ms;
    private int id;


    public Productor (int id, Pila pila, int cant, int ms){
            this.pila = pila;
            this.cant = cant;
            this.ms = ms;
            this.id = id;

    }

    public Productor (Pila p){
            this.pila = p;
    }

    public void run (){
        for (int i = 1; i < cant+1; i++){
           // Productor puede producir aletatoriamente 1 o 2 datos
            for(int j = 0; j<random.nextInt(2)+1; j++) {
                c = alfabeto.charAt(random.nextInt(26));
                System.out.println("Productor "+id+" iteracion "+i+": "+c);
                pila.poner(c);
            }
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
        }

    }
}
