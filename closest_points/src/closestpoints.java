package closest_points.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class closestpoints {
    public static class Point {
        int index;
        int[] indices;
        double x;
        double y;

        public Point(int index, double x, double y) {
            this.index = index;
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = Integer.parseInt(scanner.nextLine());

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String[] c = scanner.nextLine().split(" ");
            points.add(new Point(i, Double.parseDouble(c[0]), Double.parseDouble(c[1]))); // index, x, y
        }

        double result = closestPair(n, points);
        System.out.println(result);
        //System.out.printf("%d %d", n, closestPair(n, points));

        scanner.close();
    }

    public static double closestPair(int n, List<Point> points) {
        ArrayList<Point> p_x = new ArrayList<>(points); // points to be sorted by x
        ArrayList<Point> p_y = new ArrayList<>(points); // points to be sorted by y

        // Sort points by y and x
        Collections.sort(p_x, (o1, o2) -> Double.compare(o1.x, o2.x));
        Collections.sort(p_y, (o1, o2) -> Double.compare(o1.y, o2.y));

        return closestPairRecursive(p_x, p_y);
    }

    /**
     * Recursively find closest pairs in left and right half of all points
     */
    private static double closestPairRecursive(List<Point> px, List<Point> py) {
        // Base Case: if points < 3 - finds closest pair by measuring pairwise distances
        if (px.size() <= 3) {
            // Find closest pair in the small list of pairs
            double smallestDist = Double.MAX_VALUE;

            for (int i = 0; i < px.size(); i++) {
                for (int j = i + 1; j < px.size(); j++) {
                    // Compare each point and find minimum distance
                    double currentDist = euclideanDist(px.get(i), px.get(j));
                    if (currentDist < smallestDist) {
                        smallestDist = currentDist;
                    }
                }
            }
            
            // Stop recursion and return
            // We have found the smallest dist between points in the base case
            return smallestDist;
        }

        // Construct Qx, Rx in O(n) time
        int mid = px.size() / 2; // midpoint splitting the points at the x-line (line L)
        List<Point> q_x = px.subList(0, mid); // Left half (x-axis) of points sorted by x 
        List<Point> r_x = px.subList(mid, px.size()); // Right half (x-axis) of points sorted by x 

        Point midPoint = px.get(mid); // get the middle point (line L)
        ArrayList<Point> q_y = new ArrayList<>(); 
        ArrayList<Point> r_y = new ArrayList<>(); 

        // Construct Qy and Ry in O(n) time
        for (int i = 0; i < py.size(); i++) {
            // do the y point belong to the left or right part of the midpoint (line L)
            if (py.get(i).x <= midPoint.x)
                q_y.add(py.get(i));
            else
                r_y.add(py.get(i));
        }

        double distQ = closestPairRecursive(q_x, q_y); // smallest dist in left half
        double distR = closestPairRecursive(r_x, r_y); // smallest dist in right half
        double minDist = Math.min(distQ, distR); // smallest dist in total

        // Find all points on midpoint
        List<Point> l = new ArrayList<>();
        for (Point p : px) {
            if (p.x == midPoint.x)
                l.add(p);
        }

        List<Point> s = new ArrayList<>(); // TODO: s is not used
        for (Point p : px) {
            for (Point pl : l) {
                if (euclideanDist(p, pl) < minDist) s.add(p);
            }
            // if the distance between the point and the midpoint is less than minDist
            // could potentially be closer to point in other half
        }

        // Find and save points that is within minDist of the line L 
        List<Point> sy = new ArrayList<>(); 
        for (Point p : py) {
            for (Point pl : l) {
                if (euclideanDist(p, pl) < minDist) sy.add(p);
            }
        }

        // Find distance between all point within the minDist of the mid line
        double mergeMinDist = Double.MAX_VALUE;
        for (int i = 0; i < sy.size(); i++) {
            for (int j = i + 1; j < sy.size(); j++) {
                if (j == 15) // at most 15
                    break;

                double dist = euclideanDist(sy.get(i), sy.get(j));

                // we save the smallest distance we find
                if (dist < mergeMinDist) {
                    mergeMinDist = dist;
                }
            }
        }

        // Returns shortest distance
        return mergeMinDist < minDist ? mergeMinDist : minDist;
    }

    /**
     * Find euclidean distance between two points (A and B)
     */
    private static double euclideanDist(Point A, Point B) {
        return Math.sqrt((B.y - A.y) * (B.y - A.y) + (B.x - A.x) * (B.x - A.x));
    }
}
