package matching;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class matching {

    static private class Person {
        int id;
        String name;
        Person match;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input;

        // skip lines until "n= "
        while (true) {
            input = sc.nextLine();
            if (input.contains("=") && !input.startsWith("#")) {
                break;
            }
        }

        int n = Integer.parseInt(input.split("=")[1]);
        List<Person> men = new ArrayList<Person>();
        List<Person> women = new ArrayList<Person>();

        for (int i = 1; i <= 2 * n; i++) {

            String[] p = sc.nextLine().split(" ");
            int id = (Integer.parseInt(p[0]) - 1) / 2; // squish id's so both men an women are equal
            Person person = new Person(id, p[1]);

            // move into seperate list depending on gender
            // even for women and odd for men
            if (i % 2 == 0) {
                women.add(person);
            } else {
                men.add(person);
            }
        }

        sc.nextLine();

        int[][] rankings = new int[n][n];
        int[][] manPref = new int[n][n];

        for (int i = 0; i < 2 * n; i++) {

            String[] personPrefs = sc.nextLine().split(":");
            String[] prefList = personPrefs[1].trim().split(" ");

            // map each string number from prefs to an integer
            int[] prefInts = Stream.of(prefList).mapToInt(Integer::parseInt).map(id -> (id - 1) / 2).toArray(); // [2,4,6] -> [0,1,2]

            // for each person, fetch the person from the id, and add the prefs
            if (i % 2 != 0) {
                int[] womanPref = new int[n];
                for (int j = 0; j < n; j++) {
                    // for each woman insert the id of a man, and their ranking
                    womanPref[prefInts[j]] = j;
                }
                rankings[i/2] = womanPref; // inserts person id at given preference level
            } else {
                manPref[i/2] = prefInts;
            }
        }

        List<Person> matches = match(n, men, women, rankings, manPref);
        List<String> result = createMatchStrings(matches);

        for (String match : result) {
            System.out.println(match);
        }

        sc.close();
    }

    public static List<Person> match(int n, List<Person> proposers, List<Person> rejectors, int[][] rankings,
            int[][] manPref) {
        // Generate strings of pairs of type: person1name -- person2name

        LinkedList<Person> freeProposers = new LinkedList<Person>(proposers);
        int[] next = new int[n]; // for each proposer, save their next rejector at their id

        while (!freeProposers.isEmpty()) {
            Person p = freeProposers.getFirst();
            Person r = rejectors.get(manPref[p.id][next[p.id]]); // find next rejector
            next[p.id] += 1; // increment next counter, so we find the next rejector in following iterations

            Person currentMatch = r.match;
            if (currentMatch == null) {
                r.match = p;
                freeProposers.removeFirst();
            } else {
                // find which proposer is best ranked, by fetching the rank from the proposers
                // id, and comparing the ranks
                // if ranking of new man is better than the current rank, we need to switch the
                // partners
                boolean exchange = rankings[r.id][p.id] < rankings[r.id][currentMatch.id];

                if (exchange) {
                    // if we need to change partner, we change, and add the proposer to the free
                    // proposers list
                    Person rejected = r.match;
                    r.match = p;
                    freeProposers.removeFirst(); // remove the new match from the free proposers list
                    freeProposers.addFirst(rejected); // add the rejected person to the free proposers list
                }
            }
        }

        return rejectors;
    }

    public static List<String> createMatchStrings(List<Person> rejectors) {
        List<String> result = new ArrayList<>();
        rejectors.sort((a,b)-> a.match.id - b.match.id);
        for (Person p : rejectors) {
            String s = String.format("%1$s -- %2$s", p.match.name, p.name);
            result.add(s);
        }
        return result;
    }
}
