import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PolynomialSolver {

    // Function to decode the value based on the base (use long for large values)
    public static long decodeValue(String base, String value) {
        return Long.parseLong(value, Integer.parseInt(base));
    }

    // Function to calculate Lagrange interpolation to find constant term (c)
    public static double lagrangeInterpolation(List<long[]> points) {
        double c = 0;

        for (int i = 0; i < points.size(); i++) {
            long xi = points.get(i)[0];
            long yi = points.get(i)[1];
            double li = 1;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    long xj = points.get(j)[0];
                    li *= (0 - xj) / (double)(xi - xj);
                }
            }

            c += yi * li;
        }

        return c;
    }

    public static void main(String[] args) {
        JSONParser parser = new JSONParser();

        try {
            // Read the first JSON test case file
            JSONObject json = (JSONObject) parser.parse(new FileReader("input1.json"));
            // Read the second JSON test case file
            JSONObject json2 = (JSONObject) parser.parse(new FileReader("input2.json"));

            // Process the first test case
            System.out.println("Processing first test case...");
            List<long[]> points1 = extractPoints(json);
            double secret1 = lagrangeInterpolation(points1);
            System.out.println("Secret (constant c) for test case 1: " + secret1);

            // Process the second test case and find wrong points
            System.out.println("\nProcessing second test case...");
            List<long[]> points2 = extractPoints(json2);
            double secret2 = lagrangeInterpolation(points2);

            // Find wrong points
            List<long[]> wrongPoints = findWrongPoints(points2, secret2);
            System.out.println("Secret (constant c) for test case 2: " + secret2);

            // Print wrong points
            if (!wrongPoints.isEmpty()) {
                System.out.println("Wrong points found:");
                for (long[] wrongPoint : wrongPoints) {
                    System.out.println("x = " + wrongPoint[0] + ", y = " + wrongPoint[1]);
                }
            } else {
                System.out.println("No wrong points found.");
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // Helper function to extract points from the JSON file
    public static List<long[]> extractPoints(JSONObject json) {
        JSONObject keys = (JSONObject) json.get("keys");
        long n = (Long) keys.get("n");

        List<long[]> points = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            JSONObject root = (JSONObject) json.get(String.valueOf(i));
            if (root != null) {
                String base = (String) root.get("base");
                String value = (String) root.get("value");

                // Decode y-values and add (x, y) pair to the points list
                long x = i;
                long y = decodeValue(base, value);
                points.add(new long[]{x, y});
            }
        }

        return points;
    }

    // Helper function to find wrong points in the second test case
    public static List<long[]> findWrongPoints(List<long[]> points, double correctC) {
        List<long[]> wrongPoints = new ArrayList<>();

        for (long[] point : points) {
            long x = point[0];
            long y = point[1];

            // Substitute x into the polynomial f(x) to check if the point fits the curve
            double f_x = correctC; // For a constant polynomial, f(x) = c

            if (f_x != y) {
                wrongPoints.add(point);
            }
        }

        return wrongPoints;
    }
}
