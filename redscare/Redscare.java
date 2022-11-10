package redscare;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Redscare {
    public static void main(String[] args) {

        File[] files = new File("./redscare/data").listFiles();
        
        Parser parser = new Parser();

        for (File file : files) {
            System.out.println(file.getName());
            // ignore element if its not a file, or not a .txt
            if (!file.isFile() || !file.getName().endsWith(".txt")) continue;
            // for each problem type, parse and create a graph
            IProblem[] problems = {
                    new Alternate(parser.parse(file)),
                    new Some(parser.parse(file)),
                    new Many(parser.parse(file)),
                    new Few(parser.parse(file)),
                    new None(parser.parse(file)) };

            Arrays.stream(problems).forEach(i -> {
                i.solve();
                i.print();
            });
        }
    }
}
