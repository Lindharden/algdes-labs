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
                // first, or second, string and add penalty for gab?
                M[i][j] = Math.max(getPenalty(xi, yj) + M[i - 1][j - 1],
                        Math.max(DELTA + M[i - 1][j], DELTA + M[i][j - 1]));
            }
        }

        int score = M[m - 1][n - 1];
        System.out.println(key1 + "--" + key2 + ": " + score);
        reconstructAnswers(value1, value2);
    }

    public static void reconstructAnswers(String fstInput, String sndInput) {
        final int DELTA = getPenalty("A", "*"); // penalty for gap
        int i = fstInput.length(); // keep track of position in first string (input)
        int j = sndInput.length(); // keep track of position in second string (input)
        int l = i + j;
        int xpos = l; // keep track of position in first answer string (the answer we construct)
        int ypos = l; // keep track of position in second answer string (the answer we construct)
        char[] xans = new char[l + 1]; // first answer string
        char[] yans = new char[l + 1]; // second answer string

        // go through each letter in the two input strings
        while (!(i == 0 || j == 0)) {
            if (M[i - 1][j] + DELTA == M[i][j]) {
                // if there has been introduced a gab in the second string
                xans[xpos--] = fstInput.charAt(i - 1); // use letter from first string
                yans[ypos--] = '-'; // add _ in second string
                i--;
            } else if (M[i][j - 1] + DELTA == M[i][j]) {
                // if there has been introduced a gab in the first string
                xans[xpos--] = '-'; // add _ in the answer string
                yans[ypos--] = sndInput.charAt(j - 1); // use letter of second string
                j--;
            } else {
                // use the two present letters
                xans[xpos--] = fstInput.charAt(i - 1);
                yans[ypos--] = sndInput.charAt(j - 1);
                i--;
                j--;
            }
        }

        // fill out the remainder of the first answer string
        while (xpos > 0) {
            if (i > 0)
                // we have not been through all the letters in the first string
                xans[xpos--] = fstInput.charAt(--i); // insert the corresponding letter from the first input string
            else
                // we have been through all the letters in the first input string
                // insert dashes in the front of the first answer string
                xans[xpos--] = '-';
        }

        // fill out the remainder of the first answer string
        while (ypos > 0) {
            if (j > 0)
                // we have not been through all the letters in the second string
                yans[ypos--] = sndInput.charAt(--j); // insert the corresponding letter from the second input string
            else
                // we have been through all the letters in the second input string
                // insert dashes in the front of the second answer string
                yans[ypos--] = '-';
        }

        // find the first index of the answer strings,
        // where there is not a dash in both strings
        int idx;
        for (idx = 1; yans[idx] == '-' && xans[idx] == '-'; idx++);

        // from the first index where there no dash in both strings, print the answer
        // strings
        for (int ix = idx; ix <= l; ix++) {
            System.out.print(xans[ix]);
        }
        System.out.println();
        for (int ix = idx; ix <= l; ix++) {
            System.out.print(yans[ix]);
        }
        System.out.println();
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
