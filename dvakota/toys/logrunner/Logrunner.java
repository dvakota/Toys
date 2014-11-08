package dvakota.toys.logrunner;

import java.util.*;

/**
 * Date: 11/7/14
 * DailyProgrammer Challenge #187 - Hard
 * Shortest path single-pair optimal network flow problem
 */
public class Logrunner {



    public static void main(String[] args) {

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
        System.out.printf("Seinding %d logs down the river\n", logs);
        System.out.println("***********************************\n");

        Graph river = new Graph(vertices, test);
        river.getOptimalFlow(logs);
    }
}
