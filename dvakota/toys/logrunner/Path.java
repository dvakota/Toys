package dvakota.toys.logrunner;

import java.util.List;

/* Rerpresentation of a single path as a collection of connected edges */

public class Path implements Comparable<Path> {
    List<Edge> edges;
    int minCapacity;
    int cost; //in unweighted graph every edge has the cost of 1

    public Path(List<Edge> elist) {
        edges = elist;
        cost = elist.size();
        setMinCapacity();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Edge e : edges) {
            result.append(String.format("{%s->%s, %d} ", (char) ('A' + e.node1),
                                                   (char) ('A' + e.node2), e.capacity));
        }
        return result.toString();
    }

    public String toOutString() {
        StringBuilder result = new StringBuilder();
        for (Edge e : edges) {
            result.append(String.format("%s -> ", (char) ('A' + e.node1)));
        }
        result.append((char) ('A' + edges.get(edges.size() -1).node2));
        return result.toString();
    }

    /* To avoid re-calculating shortest path every time we eliminate an edge,
     we enumerate all possile paths only once and sort them in ascending order
     by the following criteria:
     1) fewer edges == shorter path (lower cost), 2) for two paths with equal cost,
     the one with the greater minimum capacity will come first
      */
    @Override
    public int compareTo(Path o) {
        if (cost < o.cost) return -1;
        if (cost > o.cost) return 1;
        else {
            if (minCapacity > o.minCapacity) return -1;
            if (minCapacity < o.minCapacity) return 1;
        }
        return 0;

    }

    /* Finds the the edge with minimal capacity per single path */
    public void setMinCapacity() {
        minCapacity = Integer.MAX_VALUE;
        for (Edge e : edges) {
            if (e.capacity < minCapacity) {
                minCapacity = e.capacity;
            }
        }
    }
}
