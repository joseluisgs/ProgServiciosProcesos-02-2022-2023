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
public class FabricaJamones {
    private static FabricaJamones fabrica;
    
    private FabricaJamones() {
        
    }
    
    public static FabricaJamones nuevaInstancia() {
        if (fabrica== null){
            fabrica = new FabricaJamones();
        }
        else{
            //System.out.println("No se puede crear el objeto "+ nombre + " porque ya existe un objeto de la clase SoyUnico");
        }       
        return fabrica;
    }
    
    public void fabricarJamones() {
        //lanzamos las hebras
        lanzarGranjaMensajero();
        // Ahora escribimos el archivo de resumen
        
    }

    public static void lanzarGranjaMensajero() {
        
        int MAX_JAMONES = 30;
        int INTER_GRANJA = 1000;
        int TAMANO = 10;
        int TAM_LOTE = 3;
        int INTER_MENSA = 3000;
        
        int PRIO1 = 4;
        int PRIO2 = 8;
        
        Secadero secadero = new Secadero(TAMANO);
		
        Granja gr1 = new Granja("Granja1", secadero,MAX_JAMONES,INTER_GRANJA, PRIO1);
        Granja gr2 = new Granja("Granja2", secadero,MAX_JAMONES,INTER_GRANJA, PRIO2);
        
        Mensajero em = new Mensajero (secadero,((2*MAX_JAMONES)/TAM_LOTE),INTER_MENSA, TAM_LOTE);

        gr1.start();
        gr2.start();
        em.start();
              
        try {
            gr1.join();
            gr2.join();
            em.join();
   
        } catch (InterruptedException ex) {
            System.err.println(ex.getMessage());
        }
        System.exit(0);
    }
    
    
    
}
