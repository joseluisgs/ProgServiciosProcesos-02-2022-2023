/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 *
 * @author joseluisgs
 */
public class Productor extends Thread {
    private Pila pila;
    private int cant, ms;


    public Productor (Pila pila, int cant, int ms){
        this.pila = pila;
        this.cant = cant;
        this.ms = ms;
    }

    public Productor (Pila p){
            this.pila = p;
    }

    public void run (){
        for (int i = 1; i < cant+1; i++){
            Saco s = new Saco(i);
            System.out.println("Productor-> Produzco Saco: "+i+": "+s.getCodigo()+" "+s.getPeso()+"KG");
            pila.apilar(s);
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }    
        

    }
    
}
