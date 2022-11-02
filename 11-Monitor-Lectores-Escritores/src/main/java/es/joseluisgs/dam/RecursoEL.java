/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Recurso compartido de lectura y escritura
 * @author link
 */
public class  RecursoEL {
    private int lectores = 0;
    private int escritores = 0;
    private int peticionesEscritura = 0;
    
    private int dato=0; // Dato a leer y escribir
    

    public synchronized void leer(int lector) {
        // Si hay alguien escribiendo o peticiones pendientes por escribir
        while(escritores > 0 || peticionesEscritura > 0){
            try {
                wait();
            } catch (InterruptedException ex) {
                System.err.println("Leer: Error en get -> " + ex.getMessage());
            }
        }
        lectores++;
        System.out.println("Lector " + lector + " empieza a leer.");
        int DELAY = 5000;
        try{
            Thread.sleep((int) (Math.random() * DELAY));
        }
        catch (InterruptedException e) {
            System.err.println("Error en la espera del lector");
        }
        System.out.println("Lector " + lector + " ha leido el dato: " + this.dato);
        System.out.println("Lector " + lector + " termina de leer.");
        lectores--;
        notifyAll();
  }

    public synchronized void escribir(int escritor) {
        // Generamos na petición de escritura
        peticionesEscritura++;
        // Mientras haya un lector o una petición pendiente
        while(lectores > 0 || escritores > 0){
            try {
                wait(); // Esperamos
            } catch (InterruptedException ex) {
                System.err.println("Escribir: Error en get -> " + ex.getMessage());
            }
        }
        peticionesEscritura--;
        escritores++;
        System.out.println("Escritor " + escritor + " comienza a escribir.");
 
        int DELAY = 5000;
        try{
            Thread.sleep((int) (Math.random() * DELAY));
        }catch (InterruptedException e) {
            System.err.println("Error en la espera del escritor");
        }
        this.dato++;
        System.out.println("Escritor " + escritor + " ha escrito en el dato: " + this.dato);
        System.out.println("Escritor " + escritor + " termina de escribir.");
        escritores--;
        // Despertamos a todos
        notifyAll();
  }


    
}
