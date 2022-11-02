/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.ArrayList;

/**
 *
 * @author joseluisgs
 */
public class Empaquetador extends Thread {
    private Pila pila;
    private int cant,ms;
    
    public Empaquetador (Pila pila, int cant, int ms){
        this.pila = pila;
        this.cant = cant;
        this.ms = ms;
    }

    public void run (){
        //ArrayList<Saco> listaSacos = new ArrayList<Saco>();
        for (int i = 1 ; i < cant + 1; i++){
            // Empaquetador saca 10 sacos
            for(int j = 0; j<10; j++) {
                Saco s = pila.empaquetar();
                s.setLote(i);
                //listaSacos.add(s);
                System.out.println("Empaquetador-> Paquete Lote: "+i+": empaqueto Saco: "+s.getCodigo()+" "+s.getPeso()+"KG");
            }
            //listaSacos.clear();
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       }
    }
}
