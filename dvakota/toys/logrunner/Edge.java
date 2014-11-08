package dvakota.toys.logrunner;

/**
 * Date: 11/8/14
 */
public class Edge {
    int node1, node2, capacity;
    public Edge(int n1, int n2, int c) {
        node1 = n1;
        node2 = n2;
        capacity = c;
    }

    @Override
    public boolean equals(Object e) {
        if (!(e instanceof  Edge)) return false;
        Edge edge = (Edge) e;
        return (node1 == edge.node1 && node2 == edge.node2);
    }

    @Override
    public String toString() {
        return String.format("{%s->%s, %d}", (char) ('A' + node1), (char) ('A' + node2), capacity);
    }
}
