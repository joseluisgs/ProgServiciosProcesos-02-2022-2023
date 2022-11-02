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
public class Escritor extends Thread {
    int veces;
    int escritor;
    RecursoEL RW;
    private Random generator = new Random();

    public Escritor(int escritor, int veces, RecursoEL RW) {
        this.escritor=escritor;
        this.veces = veces;
        this.RW = RW;
    }
    public void run() {
        for (int i = 0; i<veces; i++) {
            try {
                Thread.sleep(generator.nextInt(500));
            } catch (InterruptedException e) {
                System.err.println("Error en escritor");
            }
            RW.escribir(escritor);
    }
  }
}
