import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/*
 Facebook Hacker Cup 2015 Qualification Round
 Problem 3: Lazer Maze
 author: Galina Khayut
 Date: 01/09/2015
*/

public class LazerMaze {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;

    int height;
    int width;

    Cell[][] board;
    MazeState currentState;
    Map<MazeState, MazeState> visited;
    Cell start;
    Cell end;
    int min = Integer.MAX_VALUE;
    boolean possible;

    public LazerMaze(int height, int width, List<String> input) {
        init(height, width, input);
    }

    public int solveMe() {
        if (start == null || end == null) throw new RuntimeException("No start or end");
        currentState = new MazeState(start, 0);
        solve(currentState);
        if (possible) return min;
        return -1;
    }

    @Override
    public String toString() {
        String result = "";
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] instanceof Lazer) {
                    int dir = (((Lazer) board[y][x]).direction + currentState.lazerState()) % 4;
                    switch(dir) {
                        case NORTH : result += " ^"; break;
                        case EAST  : result += " >"; break;
                        case SOUTH : result += " v"; break;
                        case WEST  : result += " <";
                    }
                    continue;
                }
                String space = " ";
                if (currentState.cell.equals(board[y][x])) {
                    result += "[";
                    space = "";
                }
                result += space + board[y][x].c;
                if (currentState.cell.equals(board[y][x])) result += "]";

            }
            result += "\n";
        }
        return result;
    }



    //Initialize new maze from the input
    private void init(int h, int w, List<String> input) {
        visited = new HashMap<MazeState, MazeState>();
        height = h; width = w;
        board = new Cell[height][width];
        int y = 0;
        for (String s : input) {
            for (int x = 0; x < width; x++) {
                char c = s.charAt(x);
                board[y][x] = makeCell(x, y, c);
                if (c == 'S') start = board[y][x];
                if (c == 'G') end = board[y][x];
            }
            y++;
        }
    }

    private Cell makeCell(int x, int y, char c) {
        switch (c) {
            case '.' : return new Cell(c, x, y, true);
            case '#' : return new Cell(c, x, y, false);
            case '^' : return new Lazer(c, x, y, NORTH);
            case '>' : return new Lazer(c, x, y, EAST);
            case 'v' : return new Lazer(c, x, y, SOUTH);
            case '<' : return new Lazer(c, x, y, WEST);
            default  : return new Cell(c, x, y, true);
        }
    }

    //Exhaustive DFS seatch using a stack. Remembers minimal number of steps from all solutions
    private void solve(MazeState start) {
        Stack<MazeState> path = new Stack<MazeState>();
        path.push(start);

        while(!path.isEmpty()) {
            MazeState state = path.pop();
            currentState = state;
            makeMove(state);

            System.out.println("\nExploring cell " + state.cell.x + "," + state.cell.y);
            System.out.println("Steps made in this state: " + state.steps);
            System.out.println("" + this);

            if (state.cell.equals(end)) {
                System.out.println("\tFinished in " + state.steps + " steps");
                if (state.steps < min) {
                    min = state.steps;
                    possible = true;
                }
            }
            MazeState[] validMoves = validMoves(state);
            if (validMoves.length == 0 && !state.cell.equals(end)) {
                System.out.println("Dead end at " + state.cell + "\n");
            }
            for (MazeState move : validMoves) {
                if (move != null) {
                    path.push(move);
                }
            }
        }
    }

    //Enumerate all possible moves from the current position/state
    private MazeState[] validMoves(MazeState forState) {
        int newSteps = forState.steps + 1;
        Cell c = forState.cell;

        List<MazeState> r = new ArrayList<MazeState>();

        for (int y = -1; y < 2; y++) {
            if (y != 0) {
                int cy = c.y + y;
                System.out.println("Checking " + board[cy][c.x]);
                if (isValid(c.x, cy, newSteps)){
                    MazeState newState = new MazeState(board[cy][c.x], newSteps);
                    r.add(newState);
                    System.out.println(newState + " is available");
                }
            }
        }

        for (int x = -1; x < 2; x++) {
            if (x != 0) {
                int cx = c.x + x;
                System.out.println("Checking " + board[c.y][cx]);
                if (isValid(cx, c.y, newSteps)) {
                    MazeState newState = new MazeState(board[c.y][cx], newSteps);
                    r.add(newState);
                    System.out.println(newState + " is available");
                }
            }
        }
        return r.toArray(new MazeState[r.size()]);
    }

    //Check if the chosen cell is vulnerable to any of the adjacent lazers
    private boolean isVulnerable(MazeState state) {
        Cell cell = state.cell;
        Cell[] neighbors = nearestObstacle(cell);
        for (Cell c : neighbors) {
            if (c == null) continue;
            if (c instanceof Lazer) {
                Lazer l = (Lazer) c;
                //check if the new cell position will be located in the
                //direction of the lazer if we are to make a step
                int direction = (l.direction + state.lazerState()) % 4;
                if (direction == relativePosition(cell, l)) {
                    Cell ce = state.cell;
                    System.out.println("Cell " + ce + " is vulnerable to " + l);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValid(int x, int y, int steps) {
        if ((x >= width || x < 0) ||
                        (y >= height || y < 0)) return false;
        MazeState state = new MazeState(board[y][x], steps);
        if (state.cell.equals(end)) return true;
        String xy = state.cell.x + "," + state.cell.y;

        //Check if this position has already been explored in a current state.
        //To avoid getting stuck in an infinie loop moving back and forth, if
        //the position has been explored but the previous path took more steps then
        //the current one, replace it with the one with less steps and mark position available.
        //Otherwise, mark position as unavailable.
        MazeState previous = visited.get(state);
        if (previous != null) {
            System.out.println("Move " + xy + " has already been made");
            if (previous.steps > state.steps) visited.put(state, state);
            else return false;

        }
        return  (state.cell.isEmpty && !isVulnerable(state));
    }


    //Remember position and state as visited
    private void makeMove(MazeState to) {
        visited.put(to, to);
    }

    //returns the position of the current cell (me) relative to the other.
    // Used when calculating vulnerability : if  lazer.direction
    //(adjusted to the number of steps in current state) == relativePosition(cell, lazer)
    //cell is considered vulnerable and is excluded from valid moves
    private int relativePosition(Cell me, Cell c) {
        if (me.x == c.x) {
            if (me.y < c.y) return NORTH;
            if (me.y > c.y) return SOUTH;
        } else if (me.y == c.y) {
            if (me.x > c.x) return EAST;
            if (me.x < c.x) return WEST;
        }
        return -1;
    }

    //Find the nearest non-empty cell surrounding the current one
    private Cell[] nearestObstacle(Cell c) {
        Cell[] result = new Cell[4];
        for (int y = c.y - 1; y >= 0; y--) {
            if (!board[y][c.x].isEmpty) {
                result[NORTH] = board[y][c.x];
                break;
            }
        }
        for (int x = c.x + 1; x < width; x++) {
            if (!board[c.y][x].isEmpty) {
                result[EAST] = board[c.y][x];
                break;
            }
        }
        for (int y = c.y + 1; y < height; y++) {
            if (!board[y][c.x].isEmpty) {
                result[SOUTH] = board[y][c.x];
                break;
            }
        }
        for (int x = c.x - 1; x >= 0; x--) {
            if (!board[c.y][x].isEmpty)  {
                result[WEST] = board[c.y][x];
                break;
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Please provide input file name as first argument");
            return;
        }

        String filename = args[0];
        Scanner sc = null;
        List<Integer> solutions = new ArrayList<Integer>();
        List<List<String>> inputs = new ArrayList<List<String>>();

        try {
            sc = new Scanner(new File(System.getProperty("user.dir") +"/" + filename));
            int cases = Integer.parseInt(sc.nextLine().trim());
            List<String> input = null;
            while (cases > 0) {
                String[] s = sc.nextLine().split("\\s+");
                if (s.length == 2) {
                    int height = Integer.parseInt(s[0]);
                    input = new ArrayList<String>();
                    for (int i = 0; i < height; i++) {
                        String str = sc.nextLine();
                        input.add(str.trim());
                    }
                    if (input != null) inputs.add(input);
                } else {
                    inputs.add(new ArrayList<String>());
                }
                cases--;
            }
        } finally {
            sc.close();
        }

        for (int x = 0; x < inputs.size(); x++) {
            if (x != Integer.parseInt(args[1])) continue;
            List<String> maze = inputs.get(x);
            System.out.println("Solving case " + (x+1));
            LazerMaze lazerMaze = new LazerMaze(maze.size(), maze.get(0).length(), maze);
            int min = lazerMaze.solveMe();
            System.out.println("Minimum steps : " + min);
            solutions.add(min);
        }

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter
                                             (new File(System.getProperty("user.dir")
                                                                   + "/" + filename + ".out")));
            for (int i = 0; i < solutions.size(); i++) {
                int s = solutions.get(i);
                pw.printf("Case #%d: %s\n", i+1, s == -1 ? "impossible" : s+"");
            }
        } finally {
            pw.close();
        }
    }
}

class Cell {
    int x;
    int y;
    char c;
    boolean isEmpty;

    public Cell(char c, int x, int y, boolean isEmpty) {
        this.c = c;
        this.x = x;
        this.y = y;
        this.isEmpty = isEmpty;
    }

    @Override
    public String toString() {
        return String.format("%d, %d ", x, y);
    }

    @Override
    public boolean equals(Object o) {
        Cell cell = (Cell) o;
        return this.x == cell.x && this.y == cell.y;
    }
}

class Lazer extends Cell{
    int direction;
    public Lazer(char c, int x, int y,  int p) {
        super(c, x, y, false);
        direction = p;
    }
}

class MazeState {
    Cell cell;
    int steps;

    public MazeState(Cell c, int s) {
        cell = c;
        steps = s;
    }

    public int lazerState() {
        return steps % 4;
    }

    public boolean equals (Object o) {
        if (!(o instanceof MazeState)) return false;
        MazeState m = (MazeState) o;
        //return hashCode() == m.hashCode();
        return (cell.x == m.cell.x && cell.y == m.cell.y && lazerState() == m.lazerState());
    }

    public int hashCode() {
        //return cell.x * 100 + cell.y * 10 + lazerState();
        return ("#"+ cell.x + "#" + cell.y +  "#" + lazerState()).hashCode();
    }

    public String toString() {
        return String.format("{%d,%d}", cell.y, cell.x);
    }
}
