/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.Random;

/**
 *
 * @author link
 */
public class Lector extends Thread{
    int veces;
    int lector;
    RecursoEL RW;
    private Random generator = new Random();

    public Lector(int lector, int veces, RecursoEL RW) {
        this.veces = veces;
        this.lector= lector;
        this.RW = RW;
    }
    public void run() {
        for (int i = 0; i<veces; i++) {
	try {
            Thread.sleep(generator.nextInt(500));
        }catch (InterruptedException e) {
            System.err.println("Error en lector");
        }
        RW.leer(lector);
    }
  }
    
}
