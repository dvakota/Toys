package dvakota.toys.elevator;

/**
 * Date: 6/13/15
 */
public class Request implements Comparable<Request>{
    String id;
    int timePoint;
    int from;
    int to;
    int toFloor;

    public Request(String id, int tp, int start, int end) {
        this.id = id;
        this.timePoint = tp;
        this.from = start;
        this.to = end;
    }

    @Override
    public int compareTo(Request r) {
        if (timePoint < r.timePoint) return -1;
        if (timePoint > r.timePoint) return 1;
        return 0;
    }
}

