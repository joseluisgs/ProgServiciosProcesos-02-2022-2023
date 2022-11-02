package es.joseluisgs.dam;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrintArrayThread implements Runnable {

    private int[] array;
    private int start;
    private int end;

    public void run() {
        for (int i = start; i < end; i++) {
            System.out.println(
                    String.format("Hebra actual: %s imprimiendo valor: %s", Thread.currentThread().getName(), array[i]));
        }
    }

}