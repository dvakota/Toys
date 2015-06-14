package dvakota.toys.elevator;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import static dvakota.toys.elevator.Lift.say;


/**
 * Date: 6/13/15
 */
public class Car {

    class PriorityHash<K, V> {
        HashMap<K, Queue<V>> requestQues;
        public PriorityHash() {
            requestQues = new HashMap<K, Queue<V>>();
        }

        public void add(K floor, V req) {
            Queue<V> floorq = requestQues.get(floor);
            if (floorq == null) floorq = new PriorityQueue<V>();
            floorq.offer(req);
            requestQues.put(floor, floorq);
        }

        public Queue<V> get(int floor) {
            return requestQues.get(floor);
        }

        public boolean isEmpty() {
            boolean e = true;
            for (K key : requestQues.keySet()) {
                if (!requestQues.get(key).isEmpty())
                    e = false;
            }
            return e;
        }
    }

    final String id;
    final int capacity;
    //  final int maxFloors;
    final int floorSeconds;

    int minFloor;
    int maxFloor;

    int waitTime;
    int travelDistance;

    PriorityHash<Integer, Request> upQue;
    PriorityHash<Integer, Request> downQue;
    Queue<Request> calls;

    Map<String, Request> riders;
    int current;

    int direction;

    int timeSpent;

    public Car(String sid, int cap, float fs,
               int start) {
        id = sid;
        capacity = cap;
        floorSeconds = (int) (1 / fs);
        current = start;
        riders = new HashMap<String, Request>();

        upQue = new PriorityHash<Integer, Request>();
        downQue = new PriorityHash<Integer, Request>();
        calls = new PriorityQueue<Request>();
        direction = current > 1 ? Lift.DOWN : Lift.UP;
    }

    public void doWork() {
        say("\n" + id + " IS CURRENTLY WORKING; " + calls.size() + " requests\n");

        //set start time to the earliest call
        timeSpent = calls.peek().timePoint;
        dispatch();

        //keep going until there's no more calls in the main queue
        //Calls are dispatched by time priority
        //Elevator loops up and down, only changing direction when
        //it reaches the bottom/top floors, to ensure fairness
        while (!(upQue.isEmpty() && downQue.isEmpty())) {

            if (current < minFloor) current = minFloor;

            direction = Lift.UP;
            say("\n\tDING! TRAVELLING UP\n");
            while (!upQue.isEmpty() && current <= maxFloor) {
                say("\n|Current floor " + current + "|\tTime spent " + timeSpent);
                dispatch();
                loadUnload(current, upQue);
                move(direction);
            }

            if (current > maxFloor) current = maxFloor;

            direction = Lift.DOWN;
            say("\n\tDING! TRAVELLING DOWN\n");
            while(!downQue.isEmpty() && current >= minFloor) {
                say("\n|Current floor " + current + "|\tTime spent " + timeSpent);
                dispatch();
                loadUnload(current, downQue);
                move(direction);
            }
        }

        say("TOTAL TIME: " + timeSpent / 60 + " MINUTES " + timeSpent % 60 + " SECONDS");
    }

    //Pick up/unload passengers
    private void loadUnload(int floor, PriorityHash<Integer, Request> where) {
        Map<String, Request> off = new HashMap<String, Request>();
        Map<String, Request> on = new HashMap<String, Request>();

        Queue<Request> waiting = where.get(floor);
        if (waiting == null) {
            say("No buttons pressed for floor " + floor);
            return;
        }

        //Elevator will stop and wait only on the pressed floors
        linger();

        for (Request req = waiting.peek();
             req != null && req.timePoint <= timeSpent;
             req = waiting.peek()) {
            say("\tStopping for passenger " + req.id + "(going from " +
                            req.from + " to " + req.to +")");

            Request r  = waiting.poll();

            if ((floor == r.to) && (floor == r.from)) {
                say("\tPassenger " + req.id + ", you are an idiot. " +
                                "You are already on floor " + floor);
                continue;
            }
            if (r.to == floor) off.put(r.id, r);
            if (r.from == floor) on.put(r.id, r);
        }

        for (String s : off.keySet()) {
            Request r = riders.get(s);
            if (r != null) {
                say("\tPassenger " + r.id + " EXITING on floor " + floor);
                riders.remove(s);
            }
        }

        int leftover = 0;

        //This is the "dumb" passenger scenario. A passenger will try to board
        //the elevator even if it's going in the opposite direction of that
        //the passenger wants.
        for (String s : on.keySet()) {
            Request r = on.get(s);
            if (capacity > riders.size()) {
                r.toFloor = r.to;
                r.timePoint = timeSpent;
                riders.put(s, r);
                say("\tLoading passenger " + r.id + " going from " + floor + " to " + r.toFloor);
                if (r.toFloor > floor) upQue.add(r.toFloor, r);
                else downQue.add(r.toFloor, r);
            } else {
                //capacity exceeded, passengers will have to wait
                say("Cabin over capacity! Sorry, " + r.id + " (from " +
                                r.from + " to " + r.to +")");
                leftover++;

                //will have to re-enqueue the leftover passengers
                //accounting for "Wasted" time (new timePoint)
                r.timePoint = timeSpent;
                calls.offer(r);
            }
        }

        if (off.size() > 0) say(off.size() + " passengers got off");
        if (on.size() > 0) say((on.size()-leftover) + " passengers got on");
        if (leftover > 0)
            say(leftover + " passengers are left behind :(");
        say("Car " + id + " is going " + (direction == Lift.UP ? "UP" : "DOWN")
                        + " with " + riders.size() + " passengers");

    }

    private void linger() {
        if (waitTime > 0) say("Elevator waiting for " + waitTime + " seconds");
        timeSpent += waitTime;
    }

    private void move(int direction) {
        current += direction;
        if (current <= maxFloor && current >= minFloor) {
            timeSpent += floorSeconds;
            travelDistance++;
        }
    }

    private void dispatch() {
        if (calls.isEmpty())
            return;
        //dispatch the calls to the appropriate queue
        for (Request req = calls.peek();
             req != null && req.timePoint <= timeSpent; req = calls.peek()) {

            req = calls.poll();
            req.toFloor = req.from;
            if (riders.containsKey(req.id)) {
                //existing rider pressed another floor
                say("\tPassenger " + req.id + " is now going to " + req.to);
                riders.get(req.id).to = req.to;
                riders.get(req.id).from = req.from = current;
                req.timePoint = timeSpent;
                req.toFloor = req.to;
            }

            if (req.toFloor == current) {
                upQue.add(req.toFloor, req);
                downQue.add(req.toFloor, req);
            } else {
                if (req.toFloor < current) downQue.add(req.toFloor, req);
                if (req.toFloor > current)upQue.add(req.toFloor, req);
            }
        }
    }
}


