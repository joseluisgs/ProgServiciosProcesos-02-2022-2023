import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

class Worker implements Runnable {
    String name;
    ReentrantLock re;

    public Worker(ReentrantLock rl, String n) {
        re = rl;
        name = n;
    }

    public void run() {
        boolean done = false;
        // Espera activa!!!!
        while (!done) {
            //Intentamos obtener el candado
            boolean ans = re.tryLock();

            // Si lo tenemos es true!!!!
            if (ans) {
                try {
                    Date d = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                    System.out.println("Nombre de Tarea - " + name
                            + " candado exterior adquirido a las "
                            + ft.format(d)
                            + " Haciendo trabajo externo");
                    Thread.sleep(1500);

                    // Obteniendo candado interno
                    re.lock();
                    try {
                        d = new Date();
                        ft = new SimpleDateFormat("hh:mm:ss");
                        System.out.println("Nombre de Tarea - " + name
                                + " candado interior adquirido a las "
                                + ft.format(d)
                                + " Haciendo trabajo interno");
                        System.out.println("Veces que bloquea sin liberar - " + re.getHoldCount());
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        //Levanta el candado interno
                        System.out.println("Nombre de Tarea - " + name +
                                " candado interior liberado");

                        re.unlock();
                    }
                    System.out.println("Veces que bloquea sin liberar - " + re.getHoldCount());
                    System.out.println("Nombre de la Tarea - " + name + " trabajo completo");
                    done = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Levanta el candado exterior
                    System.out.println("Nombre de Tarea - " + name +
                            " candado exterior liberado");

                    re.unlock();
                    System.out.println("Veces que bloquea sin liberar - " +
                            re.getHoldCount());
                }
            } else {
                System.out.println("Nombre Tarea - " + name +
                        " esperando candado");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}