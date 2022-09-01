package matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class matching {
    
    static private class Person {
        int id; // odd for man, even for woman
        String name;
        int[] prefs;
        
        public Person(int id, String name) {
            this.id = id;
            this.name = name;        
        }
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("-1");
        String input;

        // skip lines until "n= "
        while(true) {
            input = sc.nextLine();
            if (input.contains("=")) {
                break;
            }
        }
        
        int n = Integer.parseInt(input.split("=")[1]);
        System.out.println("1");
        List<Person> men = new ArrayList<Person>();
        List<Person> women = new ArrayList<Person>();
        
        for (int i = 1; i <= 2*n; i++) {

            String[] p = sc.nextLine().split(" ");
            Person person = new Person(Integer.parseInt(p[0]), p[1]);
            
            // move into seperate list depending on gender
            // even for women and odd for men
            if (i % 2 == 0) { 
                women.add(person);
            } else  {
                men.add(person);
            }
        }

        sc.nextLine();
        System.out.println("2");

        for (int i = 1; i <= 2*n; i++) {
            String[] personPrefs = sc.nextLine().split(":");
            String[] prefList = personPrefs[1].trim().split(" ");
            
            // map each string number from prefs to an integer
            int[] prefInts = Stream.of(prefList).mapToInt(Integer::parseInt).toArray(); 

            // for each person, fetch the person from the id, and add the prefs
            if (i % 2 == 0) {
                women.get((i-1)/2).prefs = prefInts;
            } else {
                men.get((i-1)/2).prefs = prefInts;
            }
        }
        System.out.println("3");

        match(n, men, women);

        sc.close();
    }



    public static void match(int n, List<Person> men, List<Person> women) {
        
    }
}
