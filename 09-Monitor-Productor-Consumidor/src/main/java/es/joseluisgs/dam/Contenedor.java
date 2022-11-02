/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;


public class Contenedor {
    private int contenido; // Tamaño
    private boolean contenedorlleno = Boolean.FALSE; // Por defececto vacío
 
    /**
     * Obtiene de forma concurrente o síncrona el elemento que hay en el contenedor
     * @return Contenido el contenedor
     */
    public synchronized int get(int index){
        // Espera activa para fitrar varias hebras, mas seguro que IF
        while (!contenedorlleno){
            // En el caso que a nivel atómico pase una hebra, 
            // espera hasta que le den paso en la línea 
            // Pon un if a ver que pasa
            try{
                System.out.println("Consumidor: " + index + " No puedo tomar nada y me duermo");
                wait();
            } 
            catch (InterruptedException e){
                System.err.println("Contenedor: Error en get -> " + e.getMessage());
            }
        }
        // Antes de consumir indicamos que ya esta a falso, 
        // esto es lo que hace que pueda pasar otra hebra en el while 
        // del productor antes cinsluso de despertarla
        contenedorlleno = Boolean.FALSE;
        notify(); // avisamos que está vacío para despertar al productor
        return contenido; // devolvemos
        // Prueba a cambiar el boleano con la señal
    }
 
    /**
     * Introduce de forma concurrente o síncrona un elemento en el contenedor
     * @param value Elemento a introducir en el contenedor
     */
    public synchronized void put(int value){
        // Mientras el buffer esté lleno, esperamos.
        // Pon un if a ver que pasa
        while (contenedorlleno){
            // Si pasamos esperamos a que nos den la señal, 
            //si pasamos es por el bolean de la línea 36
            try{
                System.out.println("No puedo producir nada y me duermo");
                wait();
            } 
            catch (InterruptedException e){
                System.err.println("Contenedor: Error en put -> " + e.getMessage());
            }
        }
        contenido = value;
        contenedorlleno = Boolean.TRUE;
        notify(); // avisamos con true que hay contenido, despertar´ía luego a los consumidores
        // Prueba a cambiar los notify por notifyAll
    }
}