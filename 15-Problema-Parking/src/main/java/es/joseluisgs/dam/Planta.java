/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;


public class Planta {

    private boolean cerrado = false;
    Semaphore hayPlazas = new Semaphore(20);
    ArrayList<Integer> plazas = new ArrayList<>();

    public boolean entrar(int matricula, String p) throws InterruptedException {
        boolean haEntrado;
        if (hayPlazas.tryAcquire() && !isCerrado()) {
            System.out.println("El vehículo con matrícula " + matricula + " ha entrado en la planta " + p);
            plazas.add(matricula);
            haEntrado = true;
        } else {
            haEntrado = false;
        }
        return haEntrado;
    }

    public void salir(String p) {
        if (plazas.size() > 0) {
            System.out.println("Ha salido de la " + p + " el vehiculo con matrícula " + plazas.remove((int) Math.random() * plazas.size()));
            hayPlazas.release();
        }
    }

    /**
     * @return the cerrado
     */
    public boolean isCerrado() {
        return cerrado;
    }

    /**
     * @param cerrado the cerrado to set
     */
    public void setCerrado(boolean cerrado) {
        this.cerrado = cerrado;
    }
    
}
