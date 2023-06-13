/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joseluisgs
 */
public class FabricaYeso {
    private static FabricaYeso fabrica;
    
    private FabricaYeso() {
        
    }
    
    public static FabricaYeso nuevaInstancia() {
        if (fabrica== null){
            fabrica = new FabricaYeso();
        }
        else{
            //System.out.println("No se puede crear el objeto "+ nombre + " porque ya existe un objeto de la clase SoyUnico");
        }       
        return fabrica;
    }
    
    public void fabricarSacos() {
        borrarInformacion();
        //lanzamos las hebras
        lanzarProductorEmpaquetador(500,50);
        // Ahora escribimos el archivo de resumen
        
    }

    public static void borrarInformacion() {
        // borramos lo que haya
        //Saco.borrarSacos();
    }

    public static void lanzarProductorEmpaquetador(int productor, int empaquetador) {
        Pila pila = new Pila();
		
        Productor pr = new Productor(pila,productor, 500);
        Empaquetador em = new Empaquetador (pila,empaquetador,250);

        pr.start();
        em.start();
              
        try {
            pr.join();
            em.join();
   
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    
    
}
