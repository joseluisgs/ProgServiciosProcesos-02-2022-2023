/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author joseluisgs
 */
public class Jamon {
    private int peso;
    private int id;
    private String idGranja;
    private int lote;
    
    private static int idContador=1;
    
    public Jamon(int id, String idGranja) {
        peso = (int)Math.floor(Math.random()*(9-6+1)+6);
        
        this.idGranja = idGranja;
         

        // Si queremos que un jamon tenga un id segun la granja, se puede dar el case que Granja1 y Granja2 produzcan el jamon 3
        this.id = id;
        // Si queremos que cada jamon temga un id distinto indepedientemente de la granja
        //this.id = idContador;
        //idContador++;
    }

    /**
     * @return the peso
     */
    public int getPeso() {
        return peso;
    }

    /**
     * @param peso the peso to set
     */
    public void setPeso(int peso) {
        this.peso = peso;
    }

    /**
     * @return the codigo
     */
    public int getId() {
        return id;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the lote
     */
    public int getLote() {
        return lote;
    }

    /**
     * @param lote the lote to set
     */
    public void setLote(int lote) {
        this.lote = lote;
    }
    
      public String getIdGranja() {
        return idGranja;
    }

    /**
     * @param lote the lote to set
     */
    public void setIdGranaga(String idGranja) {
        this.idGranja = idGranja;
    }

    @Override
    public String toString() {
        return "Jamon{lote=" + lote + ", id=" + id+ ", peso=" + peso + ", idGranja=" + idGranja + "}";
    }
    
    
}
