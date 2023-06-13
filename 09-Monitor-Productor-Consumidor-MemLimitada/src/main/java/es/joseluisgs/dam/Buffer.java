package es.joseluisgs.dam;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author link
 */
public class Buffer {
    private int numSlots = 0;
    private double[] buffer = null;
    private int putIn = 0, takeOut = 0;
    private int cont = 0;

    public Buffer(int numSlots) {
        this.numSlots = numSlots;
        buffer = new double[numSlots];
    }
    
    public synchronized void insertar (double valor){
        while (cont == numSlots){ //condición de buffer lleno
            try {
                wait();
            }
            catch (InterruptedException e) {
                System.err.println("wait interrumpido");
            }
        } 
        buffer[putIn] = valor;
        putIn = (putIn + 1) % numSlots;
        cont++;
        System.out.println("\t->Tamaño de buffer: " + cont+"\n");
        notifyAll(); //inserción en e.m. del buffer
    }
    
    public synchronized double extraer () {
        double valor;
        while (cont == 0){ //condición de buffer vacío
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("wait interrumpido");
            }
        }
        valor = buffer[takeOut];
        takeOut = (takeOut + 1) % numSlots;
        cont--;
        System.out.println("\t->Tamaño de buffer: " + cont+"\n");
        notifyAll();
        return valor; //extracción en e.m.
    }
}
