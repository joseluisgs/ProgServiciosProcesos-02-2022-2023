/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.joseluisgs.dam;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class CajeraHebra extends Thread {

    private String nombre;
	private Cliente cliente;
	private long initialTime;


        // El metodo concurrente a ejecutar por una hebra siempre se llama run
        // si te fijas es el procesar compra de CajeraHebra
	@Override
	public void run() {

		System.out.println("La cajera " + this.getNombre() + " COMIENZA A PROCESAR LA COMPRA DEL CLIENTE "
					+ this.cliente.getNombre() + " EN EL TIEMPO: "
					+ (System.currentTimeMillis() - this.getInitialTime()) / 1000
					+ "seg");

		for (int i = 0; i< cliente.getCarroCompra().length; i++) {
			this.esperarXsegundos(getCliente().getCarroCompra()[i]);
			System.out.println(this.getNombre() + " procesado el producto " + (i + 1)
			+ " del cliente " + this.getCliente().getNombre() + "->Tiempo: "
			+ (System.currentTimeMillis() - this.getInitialTime()) / 1000
			+ "seg");
		}

		System.out.println("La cajera " + this.getNombre() + " HA TERMINADO DE PROCESAR "
						+ this.getCliente().getNombre() + " EN EL TIEMPO: "
						+ (System.currentTimeMillis() - this.getInitialTime()) / 1000
						+ "seg");
	}

	private void esperarXsegundos(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * @param cliente the cliente to set
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * @return the initialTime
     */
    public long getInitialTime() {
        return initialTime;
    }

    /**
     * @param initialTime the initialTime to set
     */
    public void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
    }
}
