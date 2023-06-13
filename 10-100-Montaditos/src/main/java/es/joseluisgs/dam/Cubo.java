/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

/**
 *
 * @author link
 */
public class Cubo {
    private final int capacidad;
    private int contenido = 0;
    private boolean cuboLleno = Boolean.FALSE;
    
    public Cubo(int capacidad){
        this.capacidad = capacidad;
    }
    
    public int getCapacidad(){
        return this.capacidad;
    }
    
    public int getBotellines(){
        return this.contenido;
    }

    //Método para extraer contenido del cubo
    public synchronized void coger(String nombreAlumno) {
        //while (!(contenido <= capacidad)) {
        while (!(this.cuboLleno)) {
            System.out.println("\tAlumno: "+ nombreAlumno + " quiere coger un botellín");
	// Mientras el contenedor esta vacio
            try {
                //Pausamos el hilo de ejecucion ya que no se puede extraer
                System.out.println("\tAlumno: "+ nombreAlumno + " tiene que esperar");
                wait();
            } catch (InterruptedException e) {
                System.err.println("Cubo: Error en get -> " + e.getMessage());
            }
	}
	// Cuando se haya reanudado el hilo (ya hay contenido, el cubo está lleno)
	// Extraemos un botellin
        this.contenido--;
        System.out.println("\tSe saca un botellín del cubo");
        System.out.println("\tAlumno: "+ nombreAlumno + " coge su botellín");
        System.out.println("\tEl cubo tiene ahora: " + this.getBotellines() + " botellines");
        // Si está vacío
        if(this.contenido==0){
            this.cuboLleno= Boolean.FALSE;
            //Despertamos el hilo pausado
            notifyAll();
        } 
		
    }

    //Método para colocar mas contenido en el contenedor
    public synchronized void poner(int idCamarero) {
        //Si el contenedor esta lleno, esperamos
	while (this.cuboLleno) {
            System.out.println("\tCamarero: "+ idCamarero + " quiere rellenar el cubo");
            try {
            //Pausamos el hilo hasta que haya hueco para colocar
                System.out.println("\tCamarero: "+ idCamarero + " tiene que esperar");
                wait();
            } catch (InterruptedException e) {
                System.err.println("Cubo: Error en put -> " + e.getMessage());
            }
	} // While
	//Cuando se haya reanudado el hilo (Ya no está lleno el contenedor)
	// rellenamos el cubo entero
        System.out.println("\tCamarero: "+ idCamarero + " rellena el cubo");
        this.contenido = this.capacidad;
        System.out.println("\tSe ha rellenado el cubo de botellines");
        System.out.println("\tEl cubo tiene ahora: " + this.getBotellines() + " botellines");
	this.cuboLleno = Boolean.TRUE;
	//Despertamos el hilo pausado
	notifyAll();
    }
    
}
