import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * SequenceAlignment
 */
public class SequenceAlignment {
    static Scanner sc;
    static Map<String, String> data;
    static int[][] penalty;
    static Map<String, Integer> proteinToIndex;
    static int[][] M; // solutions for subproblems

    public static void main(String[] args) {
        data = new HashMap<>();
        try {
            File file = new File("HbB_FASTAs-in.txt");
            sc = new Scanner(file);
            String currentProtein; // string to be build with the current protein
            String currentName;
            String nextLine = sc.nextLine();
            while (sc.hasNext()) {
                currentProtein = "";
                currentName = nextLine.split(" ")[0].substring(1); // cut await <
                nextLine = sc.nextLine();
                while (!nextLine.startsWith(">")) {
                    // when the line doesn't start with ">", it's a part of the protein
                    currentProtein += nextLine; // build the protein string
                    if (sc.hasNext()) {
                        nextLine = sc.nextLine();
                    } else {
                        break;
                    }
                }
                data.put(currentName, currentProtein); // put name and protein in map
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } finally {
            sc.close();
        }

        penalty = getPenalties();
        proteinToIndex = getProteinToIndex();

        Set<String> keySet = data.keySet();
        String[] keys = new String[keySet.size()];
        keySet.toArray(keys);
        for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                // run from the current element (i) and compare with the following elements
                stringSimilarity(keys[i], keys[j]);
            }
        }
    }

    public static void stringSimilarity(String key1, String key2) {
        String value1 = data.get(key1);
        String value2 = data.get(key2);
        final int DELTA = getPenalty("A", "*"); // penalty for gap
        int m = value1.length();
        int n = value2.length();

        // instantiate the M array
        M = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            M[i][0] = i * DELTA;
        }
        for (int j = 0; j <= n; j++) {
            M[0][j] = j * DELTA;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                String xi = value1.charAt(i - 1) + ""; // get i'th char in first string
                String yj = value2.charAt(j - 1) + ""; // get j'th char in second string

                // Find best option. Should we accept difference in both? Introduce gab in
                // first, or second string, and add penalty for gab?
                M[i][j] = Math.max(getPenalty(yj, xi) + M[i - 1][j - 1],
                        Math.max(DELTA + M[i - 1][j], DELTA + M[i][j - 1]));
            }
        }

        int score = M[m][n];
        System.out.println(key1 + "--" + key2 + ": " + score);
        reconstructAnswers(value1, value2);
    }

    public static void reconstructAnswers(String a, String b) {
        final int DELTA = getPenalty("A", "*"); // penalty for gap
        StringBuilder aa = new StringBuilder(), bb = new StringBuilder();

        // go through each letter in the two input strings
        for (int i = a.length(), j = b.length(); i > 0 && j > 0; ) {
            if (M[i - 1][j] + DELTA == M[i][j]) {
                // if there has been introduced a gab in the second string
                aa.append(a.charAt(--i)); // use letter from first string
                bb.append("-"); // add - in the answer string
            } else if (M[i][j - 1] + DELTA == M[i][j]) {
                // if there has been introduced a gab in the first string
                bb.append(b.charAt(--j)); // use letter of second string
                aa.append("-"); // add - in the answer string
            } else {
                // use the two present letters
                aa.append(a.charAt(--i));
                bb.append(b.charAt(--j));
            }
        }
        System.out.println(aa.reverse().toString());
        System.out.println(bb.reverse().toString());
    }

    public static int getPenalty(String a, String b) {
        return penalty[proteinToIndex.get(a)][proteinToIndex.get(b)];
    }

    public static int[][] getPenalties() {
        return new int[][] {
            { 4, -1, -2, -2, 0, -1, -1, 0, -2, -1, -1, -1, -1, -2, -1, 1, 0, -3, -2, 0, -2, -1, 0, -4 },
            { -1, 5, 0, -2, -3, 1, 0, -2, 0, -3, -2, 2, -1, -3, -2, -1, -1, -3, -2, -3, -1, 0, -1, -4 },
            { -2, 0, 6, 1, -3, 0, 0, 0, 1, -3, -3, 0, -2, -3, -2, 1, 0, -4, -2, -3, 3, 0, -1, -4 },
            { -2, -2, 1, 6, -3, 0, 2, -1, -1, -3, -4, -1, -3, -3, -1, 0, -1, -4, -3, -3, 4, 1, -1, -4 },
            { 0, -3, -3, -3, 9, -3, -4, -3, -3, -1, -1, -3, -1, -2, -3, -1, -1, -2, -2, -1, -3, -3, -2, -4 },
            { -1, 1, 0, 0, -3, 5, 2, -2, 0, -3, -2, 1, 0, -3, -1, 0, -1, -2, -1, -2, 0, 3, -1, -4 },
            { -1, 0, 0, 2, -4, 2, 5, -2, 0, -3, -3, 1, -2, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4 },
            { 0, -2, 0, -1, -3, -2, -2, 6, -2, -4, -4, -2, -3, -3, -2, 0, -2, -2, -3, -3, -1, -2, -1, -4 },
            { -2, 0, 1, -1, -3, 0, 0, -2, 8, -3, -3, -1, -2, -1, -2, -1, -2, -2, 2, -3, 0, 0, -1, -4 },
            { -1, -3, -3, -3, -1, -3, -3, -4, -3, 4, 2, -3, 1, 0, -3, -2, -1, -3, -1, 3, -3, -3, -1, -4 },
            { -1, -2, -3, -4, -1, -2, -3, -4, -3, 2, 4, -2, 2, 0, -3, -2, -1, -2, -1, 1, -4, -3, -1, -4 },
            { -1, 2, 0, -1, -3, 1, 1, -2, -1, -3, -2, 5, -1, -3, -1, 0, -1, -3, -2, -2, 0, 1, -1, -4 },
            { -1, -1, -2, -3, -1, 0, -2, -3, -2, 1, 2, -1, 5, 0, -2, -1, -1, -1, -1, 1, -3, -1, -1, -4 },
            { -2, -3, -3, -3, -2, -3, -3, -3, -1, 0, 0, -3, 0, 6, -4, -2, -2, 1, 3, -1, -3, -3, -1, -4 },
            { -1, -2, -2, -1, -3, -1, -1, -2, -2, -3, -3, -1, -2, -4, 7, -1, -1, -4, -3, -2, -2, -1, -2, -4 },
            { 1, -1, 1, 0, -1, 0, 0, 0, -1, -2, -2, 0, -1, -2, -1, 4, 1, -3, -2, -2, 0, 0, 0, -4 },
            { 0, -1, 0, -1, -1, -1, -1, -2, -2, -1, -1, -1, -1, -2, -1, 1, 5, -2, -2, 0, -1, -1, 0, -4 },
            { -3, -3, -4, -4, -2, -2, -3, -2, -2, -3, -2, -3, -1, 1, -4, -3, -2, 11, 2, -3, -4, -3, -2, -4 },
            { -2, -2, -2, -3, -2, -1, -2, -3, 2, -1, -1, -2, -1, 3, -3, -2, -2, 2, 7, -1, -3, -2, -1, -4 },
            { 0, -3, -3, -3, -1, -2, -2, -3, -3, 3, 1, -2, 1, -1, -2, -2, 0, -3, -1, 4, -3, -2, -1, -4 },
            { -2, -1, 3, 4, -3, 0, 1, -1, 0, -3, -4, 0, -3, -3, -2, 0, -1, -4, -3, -3, 4, 1, -1, -4 },
            { -1, 0, 0, 1, -3, 3, 4, -2, 0, -3, -3, 1, -1, -3, -1, 0, -1, -3, -2, -2, 1, 4, -1, -4 },
            { 0, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, -2, -1, -1, -1, -1, -1 - 4 },
            { -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, -4, 1 },
        };
    }

    public static Map<String, Integer> getProteinToIndex() {
        Map<String, Integer> m = new HashMap<>();
        m.put("A", 0);
        m.put("R", 1);
        m.put("N", 2);
        m.put("D", 3);
        m.put("C", 4);
        m.put("Q", 5);
        m.put("E", 6);
        m.put("G", 7);
        m.put("H", 8);
        m.put("I", 9);
        m.put("L", 10);
        m.put("K", 11);
        m.put("M", 12);
        m.put("F", 13);
        m.put("P", 14);
        m.put("S", 15);
        m.put("T", 16);
        m.put("W", 17);
        m.put("Y", 18);
        m.put("V", 19);
        m.put("B", 20);
        m.put("Z", 21);
        m.put("X", 22);
        m.put("*", 23);
        return m;
    }
}
