/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Coche extends Thread{
    
    int matricula;
    Edificio e=new Edificio();
    public Coche(int matricula,Edificio e) {
        this.matricula=matricula;
        this.e=e;
    }

    
    
    @Override
    public void run(){
        try {
            e.intentarEntrar(matricula);
        } catch (InterruptedException ex) {
            Logger.getLogger(Coche.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
