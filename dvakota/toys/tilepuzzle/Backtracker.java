package dvakota.toys.tilepuzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 10/16/14
 */
public abstract class Backtracker<T> {

    public abstract List<T> getAllPieces();

    public abstract void makeMove(T move, int position);

    public abstract List<T> legalMoves(T item, int position);

    public abstract int stopValue();

    public abstract void prettyPrint();

    /* Creates a new list without the given item */
    List<T> remaining(T item, List<T> remaining) {
        List<T> result = new ArrayList<T>();
        for (T t : remaining) {
            if (!t.equals(item)) result.add(t);
        }
        return result;
    }

    public int solveMe() {
        List<T> remaining = getAllPieces();
        int size = stopValue();
        return solve(remaining, 0, size);
    }

    /* Recursive helper */
    private int solve(List<T> remaining, int position, final int size) {
        if (position == size) {
            prettyPrint();
            return 0; //solved
        }
        else if (remaining.size() == 0) return -1; //no solution
        makeMove(null, position);

        List<T> legalMoves = new ArrayList<T>();

        /* Enumerate all legal moves for all remaining items */
        for (int i = 0; i < remaining.size(); i++) {
            T item = remaining.get(i);
            legalMoves.addAll(legalMoves(item, position));
        }

        /* Do a DFS solution search for each move */
        for (int i = 0; i < legalMoves.size(); i++) {
            T legalMove = legalMoves.get(i);
            makeMove(legalMove, position);
            List<T> rest = remaining(legalMove, remaining);
            solve(rest, position + 1, size);
        }
       return 0;
    }
}
//https://gist.github.com/anonymous/5cf93432faef19c8685d
