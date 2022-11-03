package redscare;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import redscare.Alternate;
import redscare.Some;
import redscare.Many;
import redscare.Few;
import redscare.None;
import redscare.Graph;
import redscare.Parser;

public class Redscare {
    public static void main(String[] args) {

        // read relevant files in directory
        File dir = new File("data");
        FileFilter filter = new FileFilter() {
            public boolean accept(File f) {
                return f.getName().endsWith("txt");
            };
        };

        Parser parser = new Parser();
        File[] files = dir.listFiles(filter);
        for (File f : files) {
            String file = f.getName();
            System.out.println(file);
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
