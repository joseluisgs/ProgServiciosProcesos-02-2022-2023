/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author link
 */
public class Productor extends Thread {
    private Buffer bb = null;

    public Productor (Buffer bb) {
        this.bb = bb;
    }

 public void run() {
    double item = 0.0;
    while (true) {
        bb.insertar (++item);
        System.out.println("Produciendo " + item);



    }
 }//run
}
