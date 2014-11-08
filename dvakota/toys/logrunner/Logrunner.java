package dvakota.toys.logrunner;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Date: 11/7/14
 * DailyProgrammer Challenge #187 - Hard
 * Shortest path single-pair optimal network flow problem
 * Test runner (supports input from file as well as hardcoded values)
 */
public class Logrunner {
    static int[] vertices = new int[26];

    public static Edge[] input(String file) throws Exception {
        Arrays.fill(vertices, -1);
        Scanner sc = new Scanner(Logrunner.class.getResource(file).openStream());
        List<Edge> result = new LinkedList<Edge>();
        while (sc.hasNext()) {
            String line = sc.nextLine();
            if (line.length() == 0) continue;
            String[] tokens = line.split("\\s*-[\\s*\\>]");
            int node1, node2, capacity;
            node1 = node2 = capacity = -1;
            for (String token : tokens) {
                String t = token.trim();
                if (token.trim().length() == 1) {
                    if (node1 == -1) node1 = t.charAt(0) - 'A';
                    else node2 = t.charAt(0) - 'A';
                } else {
                    String[] words = token.split("\\s+");
                    capacity = Integer.parseInt(words[1].trim());
                }
                if (node1 >=0 && node2 >= 0 && capacity > 0 && node1 != node2) {
                    Edge edge = new Edge(node1, node2, capacity);
                    result.add(edge);
                    vertices[node1]++; vertices[node2]++;
                }
            }
        }
        Edge[] edgeArray = new Edge[result.size()];
        result.toArray(edgeArray);
        return edgeArray;
    }

    public static void main(String[] args) throws Exception {

        Edge[] test = new Edge[13];
        test[0] = new Edge(0, 1, 6);
        test[1] = new Edge(0, 2, 2);
        test[2] = new Edge(1, 4, 3);
        test[3] = new Edge(1, 3, 3);
        test[4] = new Edge(3, 2, 2);
        test[5] = new Edge(3, 5, 1);
        test[6] = new Edge(2, 6, 5);
        test[7] = new Edge(4, 7, 1);
        test[8] = new Edge(4, 8, 2);
        test[9] = new Edge(5, 7, 1);
        test[10] = new Edge(6, 7, 2);
        test[11] = new Edge(6, 8, 2);
        test[12] = new Edge(7, 8, 4);

        int logs = new Random().nextInt(30);
        int vertices = 9;

        System.out.println("***********************************");
        System.out.printf("Sending %d logs down the river\n", logs);
        System.out.println("***********************************\n");

        Graph river = new Graph(vertices, test);
        river.getOptimalFlow(logs);

        System.out.println("Test input from file\n\n");
        Edge[] fromFile = Logrunner.input("logrunner.txt");
        int numberOfVertices = 0;
        for (int v : Logrunner.vertices) {
            if (v >=0) numberOfVertices++;
        }

        logs = new Random().nextInt(9);
        System.out.println("***********************************");
        System.out.printf("Sending %d logs down the river\n", logs);
        System.out.println("***********************************\n");

        Graph river1 = new Graph(numberOfVertices, fromFile);
        river1.getOptimalFlow(logs);
    }
}
