import java.io.File;
import java.util.*;

/**
 * Date: 1/9/15
 */
public class LazerMaze {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;

    int height;
    int width;

    Cell[][] board;
    Maze currentState;
    Set<Maze> moves;
    List<Stack<Cell>> allPaths;
    Cell start;
    Cell end;
    int min = Integer.MAX_VALUE;
    boolean possible;

    public LazerMaze(int height, int width, List<String> input) {
        init(height, width, input);
        currentState = new Maze(start, 0);
    }


    private boolean isValidMove(int x, int y) {
        return ((x < width && x >= 0) &&
                            (y < height && y >= 0));

    }
    private boolean isValidState(Maze newState) {
        if (!newState.cell.isEmpty) return false;
        if (moves.contains(newState)) return false;
        return !isVulnerable(newState);
    }

    private Maze[] validMoves(Maze currentState) {
        Cell current = currentState.cell;
        int newSteps = currentState.steps + 1;
        Maze[] result = new Maze[4];
        for (int y = -1; y < 2; y++) {
            if (y != 0) {
                int cy = current.y + y;
                if (isValidMove(current.x, cy))  {
                    Maze newState = new Maze(board[cy][current.x], newSteps);
                    if (isValidState(newState)) {
                        result[y + 1] = newState ;
                    }
                }
            }
        }
        for (int x = -1; x < 2; x++) {
            if (x != 0) {
                int cx = current.x + x;
                if (isValidMove(cx, current.y)) {
                    Maze newState = new Maze(board[current.y][cx], newSteps);
                    if (isValidState(newState)) {
                        result[x + 2] = newState;
                    }
                }
            }
        }
        return result;
    }

    public Maze makeMove(Maze to) {
        currentState = to;
        moves.add(currentState);

        //todo: remove
        print(to);

        return to;
    }


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

    private Cell[] nearestObstacle(Cell c) {
        Cell[] result = new Cell[4];
        for (int y = c.y; y >= 0; y--) {
            if (!board[y][c.x].isEmpty) {
                result[NORTH] = board[y][c.x];
                break;
            }
        }
        for (int x = c.x; x < width; x++) {
            if (!board[c.y][x].isEmpty) {
                result[EAST] = board[c.y][x];
                break;
            }
        }
        for (int y = c.y; y < height; y++) {
            if (!board[y][c.x].isEmpty) {
                result[SOUTH] = board[y][c.x];
                break;
            }
        }
        for (int x = c.x; x >= 0; x--) {
            if (!board[c.y][x].isEmpty)  {
                result[WEST] = board[c.y][x];
                break;
            }
        }
        return result;
    }

    private boolean isVulnerable(Maze state) {
        Cell cell = state.cell;
        Cell[] neighbors = nearestObstacle(cell);
        for (Cell c : neighbors) {
            if (c == null) continue;
            if (c instanceof Lazer) {
                Lazer l = (Lazer) c;
                int direction = (l.direction + state.lazerState()) % 4;

                //todo:remove
                System.out.println("Lazer direction: " + direction);
                System.out.println("Relative direction: " + relativePosition(cell, l));

                if (direction == relativePosition(cell, l)) return true;
            }
        }
        return false;
    }

    private void solve(Maze start) {
        Stack<Maze> path = new Stack<Maze>();
        //Stack<Cell> fullPath = new Stack<Cell>();
        path.push(start);
        while(!path.isEmpty()) {
            Maze state = path.pop();
            makeMove(state);
            if (state.cell.equals(end)) {
                System.out.println("Total steps made: " + state.steps);
                if (state.steps < min) {
                    min = state.steps;
                    possible = true;
                }
                continue;
            }
            Maze[] validMoves = validMoves(state);
            for (Maze move : validMoves) {
                if (move != null)
                path.push(move);
            }
        }
    }

    private void init(int h, int w, List<String> input) {
        moves = new HashSet<Maze>();
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

    public void print(Maze m) {
        Cell current = m.cell;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (board[y][x] == current) {
                    System.out.print("[" + board[y][x] + "]");
                } else

                    //todo:remove
                    if (board[y][x] instanceof Lazer)
                        System.out.print(" " + dirToChar((((Lazer) board[y][x]).direction + m.steps) % 4));
                    else System.out.print(board[y][x] +"");
            }
            System.out.println();
        }
    }

    //todo:remove
    private char dirToChar(int dir) {
        switch (dir) {
            case 0 : return '^';
            case 1 : return '>';
            case 2 : return 'v';
            case 3 : return '<';
            default: return '^';
        }
    }

    public int solveMe() {
        if (start == null || end == null) return -1;
        allPaths = new ArrayList<Stack<Cell>>();
        Stack<Maze> path = new Stack<Maze>();
        solve(currentState);

        return min;
    }

    static class Cell {
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

        public String coordinates() {
            return String.format("%d,%d", y, x);
        }

        @Override
        public String toString() {
            return String.format(" %s ", c+"");
        }
    }

    static class Lazer extends Cell{
        int direction;
        public Lazer(char c, int x, int y,  int p) {
            super(c, x, y, false);
            direction = p;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Lazer)) return false;
            Lazer l = (Lazer) o;
            return (x == l.x && y == l.y && direction == l.direction);
        }

        public int hashCode() {
            return String.valueOf("" + x + y + direction).hashCode();
        }

        @Override
        public String toString() {
            int direction = this.direction;
            if (direction == LazerMaze.NORTH) return "^";
            if (direction == LazerMaze.EAST) return ">";
            if (direction == LazerMaze.SOUTH) return "v";
            if (direction == LazerMaze.WEST) return "<";
            return "?";
        }
    }

    static class Maze {
        Cell cell;
        int steps;

        public Maze(Cell c, int s) {
            cell = c;
            steps = s;
        }

        public int lazerState() {
            return steps % 4;
        }

        public boolean equals (Object o) {
            if (!(o instanceof Maze)) return false;
            Maze m = (Maze) o;
            return (hashCode() == m.hashCode());
        }

        public int hashCode() {
            return (""+ cell.x + cell.y + lazerState()).hashCode();
        }

        public String toString() {
            return String.format("{%d,%d, %d}", cell.y, cell.x, lazerState());
        }
    }

    public static void main(String[] args) throws Exception {
       /* if (args.length == 0) {
            System.out.println("Please provide input file name as first argument");
            return;
        }*/
        String filename = "lmz.txt";
        Scanner sc = null;
        try {
            sc = new Scanner(new File(System.getProperty("user.dir") + "/src/" + filename));
            int cases = Integer.parseInt(sc.nextLine().trim());
            List<List<String>> inputs = new ArrayList<List<String>>();
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
            List<String> tcase = inputs.get(4);
            int h = tcase.size();
            int w = tcase.get(0).length();
            doTest(tcase, h, w);

        } finally {
            sc.close();
        }


    }

    private static void doTest(List<String> input, int h, int w) {
        LazerMaze maze = new LazerMaze(h, w, input);
        for (String s : input) {
            System.out.println(s);
        }
        maze.print(maze.currentState);
        System.out.println("Minimum steps: " + maze.solveMe());
    }

}
