import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Ejemplo para el productor consumidor
public class ReentrantLockWithCondition {

    private static final int CAPACITY = 5; // Memoria limitada
    private final Stack<String> stack = new Stack<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition stackEmptyCondition = lock.newCondition();
    private final Condition stackFullCondition = lock.newCondition();

    void pushToStack(String item) throws InterruptedException {
        try {
            // Obtenemos el candado
            lock.lock();
            if (stack.size() == CAPACITY) {
                System.out.println(Thread.currentThread().getName() + " esperando. Pila llena");
                stackFullCondition.await(); // Esperamos pues está llena, no puedo meter
            }
            System.out.println(Thread.currentThread().getName() + " Apilando Item " + item);
            stack.push(item);
            stackEmptyCondition.signalAll(); // Avisamos a todos los que están esperando
        } finally {
            // Liberamos el candado
            lock.unlock();
        }

    }

    String popFromStack() throws InterruptedException {
        try {
            // Obtenemos el candado
            lock.lock();
            if (stack.size() == 0) {
                System.out.println(Thread.currentThread().getName() + " esperando. Pila vacía");
                stackEmptyCondition.await(); // Esperamos pues está vacía
            }
            var item = stack.pop();
            System.out.println(Thread.currentThread().getName() + " Desapilando Item " + item);
            return item;
        } finally {
            // Liberamos el candado y avisamos!!!!
            stackFullCondition.signalAll(); // Avisamos a todos los que están esperando
            lock.unlock();
        }
    }
}