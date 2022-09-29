import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * SequenceAlignment
 */
public class SequenceAlignment {
    static Scanner sc;
    static Map<String, String> data;

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
                    // when the line dont start with ">", its a part of the protein
                    currentProtein += sc.nextLine(); // build the protein string
                    if (sc.hasNext())
                        nextLine = sc.nextLine();
                }
                data.put(currentName, currentProtein); // put name and protein in map
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } finally {
            sc.close();
        }

        String[] keys = (String[]) data.keySet().toArray();
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
        // TODO: Compare using algorithm from slides, and print according to output in HbB_FASTAs-out.txt
    }
}