import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Test {

    // psvm
    public static void main(String[] args) {

        // Zasobnik LIFO.

        Stack<String> zasobnik = new Stack<>();
        zasobnik.push("Google");    // 1. navstivena stranka
        zasobnik.push("YouTube");   // 2. navstivena stranka
        zasobnik.push("Wikipedia"); // 3. navstivena stranka

        while(!zasobnik.isEmpty()) {
            System.out.println("Navstivene stranky: " + zasobnik.pop());
        }

        // Fronta FIFO

        Queue<String> frontaLidi = new LinkedList<>();
        frontaLidi.add("Uzivatel 1");
        frontaLidi.add("Uzivatel 2");
        frontaLidi.add("Uzivatel 3");
        frontaLidi.add("Uzivatel 4");

        while(!frontaLidi.isEmpty()) {
            System.out.println("Odbavujem: " + frontaLidi.poll());
        }


    }

}
