package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author link
 */
class Consumidor extends Thread {
 
    // La memoria compartida, alias monitor
 private Buffer bb = null;
 
 public Consumidor (Buffer bb) {
     this.bb = bb; 
 }

 public void run() {
    double item;
        while (true) {
        item = bb.extraer ();
        System.out.println("Consumiendo " + item);

    }
 }//run

}//Consumidor
