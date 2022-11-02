/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 *
 * @author david
 */
public class HiloImplementado implements Runnable {

    Camello ca = new Camello();
    int avance;
    Thread h = new Thread();

    public HiloImplementado(String nombre) {
        h = new Thread(this);
        h.setName(nombre);
    }

    @Override
    public void run() {
        while (ca.getPosicion() < 100) {
            avance = ca.avanzar();
            System.out.println("El " + h.getName() + " ha avanzado " + avance + " posiciones y esta en la posición " + ca.getPosicion());
            ca.setPosicion(ca.getPosicion() + avance);
            try {
                Thread.sleep((long) (Math.random()*100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("El " + h.getName() + " ha llegado a la meta");
    }
    public void start(){
        h.start();
    }
}
