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

        Car car = new Car("Control", 12, 0.1f, 1);
        Car fastCar = new Car("FastCar", 12, 0.2f, 1);
        Car bigCar = new Car("BigCar", 20,0.1f, 1);
        Car waitingCar = new Car("WaitingCar", 12, 0.1f, 1);
        waitingCar.waitTime = 2;

        Lift elevator = new Lift("Control");

        elevator.prepare(1, 12, copyList(list), car);

        Lift fastElevator = new Lift("FAST");
        fastElevator.prepare(1, 12, copyList(list), fastCar);

        Lift bigElevator = new Lift("BIG");
        bigElevator.prepare(1, 12, copyList(list), bigCar);

        Lift waitingElevator = new Lift("WAITING");
        waitingElevator.prepare(1, 12, copyList(list), waitingCar);

        report(elevator, fastElevator, bigElevator, waitingElevator);
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
        Car fastest = null;
        Car efficient = null;
        for (Lift lift: elevators) {
            int minF, minE; minF= minE = Integer.MAX_VALUE;

            say("--------------");
            say("Elevator " + lift.name);
            say("CALLS SERVED: " + lift.totalCalls + "\tTOTAL CAR TIMES: ");
            say("--------------");
            for (Car c : lift.cars) {
                say("CAR " + c.id);
                say("\tTIME: " + c.timeSpent + " SECONDS");
                say("\tSpeed: " + c.floorSeconds + " seconds per floor");
                say("\tCapacity: " + c.capacity);
                say("\tTotal travel distance " + c.travelDistance + " floors");
                if (c.waitTime > 0) say("\tWait time: " + c.waitTime);
                say("\tAverage speed: " + String.format("%.2f", (double) (c.timeSpent) / c.travelDistance)
                                + " seconds per floor");
                if (c.timeSpent < minF) {
                    minF = c.timeSpent;
                    fastest = c;
                }
                if (c.travelDistance < minE) {
                    minE = c.travelDistance;
                    efficient = c;
                }
            }
            lift.fastestCar = fastest;
        }

        say("\n******* Best time *********\n");
        int min = Integer.MAX_VALUE;
        Lift winner = null;
        for (Lift lift : elevators) {
            if (lift.fastestCar.timeSpent < min) {
                winner = lift;
                min = winner.fastestCar.timeSpent;
            }
        }

        fastest = winner.fastestCar;
        reportCar(fastest);

        say("\n****** Most efficient (least floor travelled)*****\n");
        reportCar(efficient);
    }

    private static void reportCar(Car car) {
        say("\tCar \t" + car.id);
        say("\t\tTime spent: \t" + car.timeSpent + " seconds (" + car.timeSpent / 60 + " min "
                    + car.timeSpent % 60 + " sec)");;
        say("\t\tSpeed: \t" + car.floorSeconds + " second per floor");
        say("\t\tAverage speed: \t" +
                        String.format("%.2f", (double) (car.timeSpent) / car.travelDistance)
                        + " seconds per floor");
        say("\t\tCapacity: \t" + car.capacity);
        say("\t\tWaiting time: \t" + car.waitTime);
        say("\t\tFloors travelled: \t" + car.travelDistance);

    }
}
