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
public class Granja extends Thread {
    private Secadero secadero;
    private int cant, ms, prioridad;
    private String id;


    public Granja (String id, Secadero secadero, int cant, int ms, int prioridad){
        this.id = id;
        this.secadero = secadero;
        this.cant = cant;
        this.ms = ms;
        this.prioridad = prioridad;
    }

    public Granja (Secadero p){
            this.secadero = p;
    }

    public void run (){
        // Cambiamos la prioridad
        this.setPriority(this.prioridad);
        
        for (int i = 1; i < cant+1; i++){
            Jamon j = new Jamon(i,this.id);
            System.out.println("Granja "+this.id+"-> Produzco Jamón: "+i+": "+j.getId()+" de "+j.getPeso()+"KG");
            secadero.meter(j);
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }    
        

    }
    
}
