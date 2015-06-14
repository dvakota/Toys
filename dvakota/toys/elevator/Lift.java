package dvakota.toys.elevator;

import java.util.*;

/**
 * Date: 6/12/15
 */

public class Lift {
    static final int UP = 1;
    static final int DOWN = -1;

    //future - dispatch calls between 2 cars
    interface CarDispatcher {
        public String dispatchToCar(Request call);
    }

    String name = "Elevator";
    Queue<Request> calls;
    List<Car> cars;
    int totalCalls;
    Car fastestCar;

    CarDispatcher carDispatcher;

    public void prepare(int minFloor, int maxFloor, List<Request> riders, Car...crs) {
        say("Initializing Elevator...");
        calls = new PriorityQueue<Request>();
        cars = new ArrayList<Car>();
        for (Car c : crs) {
            c.minFloor = minFloor;
            c.maxFloor = maxFloor;
            cars.add(c);
        }
        for (Request rider : riders) {
            calls.offer(rider);
        }
        totalCalls = calls.size();

        say(cars.size() + " cars; " + calls.size() + " requests waiting");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCarWaitTime(String carId, int time) {
        Car c = null;
        for (Car car : cars) {
            if (car.id.equals(carId)) {
                c = car;
                break;
            }
        }
        if (c != null && time > 0) c.waitTime = time;
    }

    public void dispatchCall(Request call) {
        if (carDispatcher == null) {
            //select the car randomly for now
            for (Car car : cars) car.calls.offer(call);
        } else {
            carDispatcher.dispatchToCar(call);
        }
    }

    public void go() {
        while (!calls.isEmpty()) {
            Request call = calls.poll();
            dispatchCall(call);
        }

        for (Car car : cars) {
            car.doWork();
        }
    }

    public static void say(Object o) {
        System.out.println(o);
    }
}
