package dvakota.toys.logrunner;

import java.util.LinkedList;
import java.util.List;

/* Graph representation of the river */
public class Graph {
    //adjacency matrix
    Edge[][] am;
    //keep Edge references in a separate array to aovid traversing the matrix
    //every time a capacity is updated
    Edge[] allEdges;
    public Graph(int nodes, Edge[] capacities) {
        am = new Edge[nodes][nodes];
        allEdges = capacities;
        init(capacities);
    }

    /* Initialize by populating the adjacency matrix */
    public void init(Edge[] capacities) {
        for (Edge e : capacities) {
            am[e.node1][e.node2] = e;
        }
    }

    public void getOptimalFlow(int logsToTransport) {
        int logs = logsToTransport;
        List<Path> paths = new LinkedList<Path>();
        List<Edge> singlePath = new LinkedList<Edge>();

        enumerateAll(0, 1, paths, singlePath);
        System.out.printf("Total single-pair paths from %s to %s for %d edges: %d\n\n",
                                     (char)('A' + 0),
                                     (char) ('A' + am.length - 1),
                                     allEdges.length, paths.size());

        //now that all paths are enumerated, sort them by cost and
        //minimum capacity, such that shortest path with max. minimum
        //capacity is at the top of the queue
        java.util.Collections.sort(paths);

        //let's float!
        int logNo = 1;

        while (logs > 0 && !paths.isEmpty()) {
            Path p = paths.remove(0);
            int floatin = p.minCapacity > logs ? logs : p.minCapacity;
            List<Edge> edgesToUpdate = p.edges;
            logs -= floatin;

            /* Some of the edges will have zero capacity after an update.
            The paths containing them (including the edge we just used) will be skipped */
            if (floatin > 0) System.out.printf("Updated path capacities: %s\n", p.toString());
            for (int i = logNo; i < logNo + floatin; i++) {
                System.out.printf("Log No. %d a-floatin' down path %s --> path of %d \n",
                                             i, p.toOutString(), p.edges.size()+1);
            }
            if (floatin > 0) System.out.printf("%d logs remaining\n\n", logs);

            updateCapacities(edgesToUpdate, floatin);
            for (Path pathToUpdate : paths) {
                pathToUpdate.setMinCapacity();
            }
            logNo += floatin;
        }

        if (logs > 0) {
            System.out.printf("River is over capacity!\n");
        }
        System.out.printf("%d logs transported successfully\n", logNo - 1);
    }

    private void updateCapacities(List<Edge> toUpdate, int toSubtract) {
        for (Edge e : allEdges) {
            for (Edge ue : toUpdate) {
                if (ue.equals(e)) {
                    e.capacity = e.capacity - toSubtract;
                    break;
                }
            }
        }
    }

    private List<Edge> cloneList(List<Edge> original) {
        List<Edge> result = new LinkedList<Edge>();
        for (Edge e: original) {
            result.add(e);
        }
        return result;
    }

    private void enumerateAll(int startRow, int startCol, List<Path> paths, List<Edge> single) {
        int row = startRow;
        if (startRow == am.length-1 ) { //we're at the last edge, add complete path
            Path p = new Path(single);
            paths.add(p);
            return;
        }

        for (int col = startCol; col < am.length; col++) {
            Edge e = am[row][col];
            if (e != null) {
                List<Edge> newList = cloneList(single);
                newList.add(e);
                enumerateAll(col, startRow + 1, paths, newList);
            }
        }
    }
}
