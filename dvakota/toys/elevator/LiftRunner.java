package dvakota.toys.elevator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static dvakota.toys.elevator.Lift.say;
/**
 * Date: 6/13/15
 */
public class LiftRunner {

    public static void main (String[] args) throws IOException {

        Scanner sc = new Scanner(new File(System.getProperty("user.home") + "/rides.txt"));
        List<Request> list = new ArrayList<Request>();
        while (sc.hasNextLine()) {
            String[] s = sc.nextLine().split("\\s");
            if (s.length < 4) continue;
            Request ride = new Request(s[0], Integer.parseInt(s[1]),
                                                  Integer.parseInt(s[2]),
                                                  Integer.parseInt(s[3]));
            list.add(ride);
        }

        Car car1 = new Car("C1", 12, 0.1f, 1);
        Car car2 = new Car("C2", 12, 0.2f, 1);

        Lift elevator = new Lift();

        elevator.prepare(1, 12, copyList(list), car1, car2);

        Lift waitingElevator = new Lift();
        Car wcar1 = new Car("C1", 12, 0.1f, 1);
        Car wcar2 = new Car("C2", 12, 0.2f, 1);

        waitingElevator.prepare(1, 12, copyList(list), wcar1, wcar2);
        waitingElevator.setName("WaitingElevator");
        waitingElevator.setCarWaitTime("C2", 1);

        Car fastCar = new Car("C1", 12, 0.5f, 1);
        Car slowCar  = new Car("C2", 12, 0.150f, 1);
        Lift fastElevator = new Lift();
        fastElevator.prepare(1, 12, copyList(list), fastCar, slowCar);
        fastElevator.setName("FastElevator");

        Car bigCar = new Car("C1", 15, 0.1f, 1);
        Car smallCar = new Car("C2", 12, 0.1f, 1);
        Lift bigElevator = new Lift();
        bigElevator.prepare(1, 12, copyList(list), bigCar, smallCar);
        bigElevator.setName("BigElevator");

        report(elevator, waitingElevator, fastElevator, bigElevator);
    }

    private static List<Request> copyList(List<Request> src) {
        List<Request> result = new ArrayList<Request>();
        for (Request r : src) {
            Request newR = new Request(r.id, r.timePoint, r.from, r.to);
            result.add(newR);
        }
        return result;
    }

    private static void report(Lift... elevators) {
        for (Lift lift : elevators) {
            lift.go();
        }

        for (Lift lift: elevators) {
            int min = Integer.MAX_VALUE;
            Car fastest = null;

            say("--------------");
            say("Elevator " + lift.name);
            say("CALLS SERVED: " + lift.totalCalls + "\tTOTAL CAR TIMES: ");
            say("--------------");
            for (Car c : lift.cars) {
                say("CAR " + c.id);
                say("\tTIME: " + c.timeSpent + " SECONDS");
                say("\tSpeed: " + c.floorSeconds + " seconds per floor");
                say("\tCapacity: " + c.capacity);
                if (c.waitTime > 0) say("\tWait time: " + c.waitTime);
                if (c.timeSpent < min) {
                    min = c.timeSpent;
                    fastest = c;
                }
            }
            lift.fastestCar = fastest;
        }

        say("\n*******Fastest time:*********");
        int min = Integer.MAX_VALUE;
        Lift winner = null;
        for (Lift lift : elevators) {
            if (lift.fastestCar.timeSpent < min) {
                winner = lift;
                min = winner.fastestCar.timeSpent;
            }
        }

        Car fastest = winner.fastestCar;
        say("WINNER: " + winner.name + "\tTIME " + fastest.timeSpent
                        +" seconds (" + fastest.timeSpent / 60 + " min "
                        + fastest.timeSpent % 60 + " sec)");
        say("\tCar " + fastest.id);
        say("\t\tSpeed: " + fastest.floorSeconds + " second per floor");
        say("\t\tCapacity: " + fastest.capacity);
        say("\t\tWaiting time: " + fastest.waitTime);
    }
}
