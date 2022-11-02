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
public class Saco {
    private int peso;
    private int codigo;
    private int lote;
    
    public Saco(int codigo) {
        peso = (int) (Math.random() * 25 + 26);
        this.codigo = codigo;
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
    public int getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
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
}
