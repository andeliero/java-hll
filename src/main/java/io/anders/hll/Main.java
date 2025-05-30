package io.anders.hll;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream("output.csv"));
        System.setOut(out);
        System.out.println("expected,actual,error");
        for (long i = 1; i <= 100000; i++) {
            HyperLogLog hll = new HyperLogLog();
            for (long z = 1; z <= i; z++) {
                String value = z + "_" + i;
                hll.add(value);
            }
            double error = ((double)(hll.count() - i)) / i * 100;
            System.out.println(i + "," + hll.count() + "," + error);
            System.out.flush();
        }
    }

}
