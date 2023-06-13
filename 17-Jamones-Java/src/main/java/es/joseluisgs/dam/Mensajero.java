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
public class Mensajero extends Thread {
    private Secadero secadero;
    private int cant,ms, tam;
    
    public Mensajero (Secadero secadero, int cant, int ms, int tam){
        this.secadero = secadero;
        this.cant = cant;
        this.ms = ms;
        this.tam = tam;
    }

    public void run (){
        ArrayList<Jamon> misJamones = new ArrayList<Jamon>();
        
        for (int i = 1 ; i < cant + 1; i++){
            // Mensajero saca 3 jamones
            for(int k = 0; k<tam; k++) {
                Jamon j = secadero.sacar();
                j.setLote(i); // LE asigno el lote
                misJamones.add(j);
                System.out.println("Mensajero-> Paquete Lote: "+i+": empaqueto Jamon: "+j.getId()+" "+j.getPeso()+"KG de: " +j.getIdGranja());
            }
            imprimirLote(misJamones);
            misJamones.clear();
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       }
    }
    
    private void imprimirLote(ArrayList<Jamon> misJamones){
        System.out.println("\t->Imprimiendo Lote");
        for(Jamon j: misJamones){
            System.out.println("\t->"+j.toString());
        }
    }
    
}
