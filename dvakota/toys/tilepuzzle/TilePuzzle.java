package dvakota.toys.tilepuzzle;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Date: 10/14/14
 */
public class TilePuzzle extends Backtracker<TilePuzzle.Tile> {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    public static final int ASCII_DIFF = 32; //between uppercase and lowercase char values
    public static final String[] directions = {"NORTH", "EAST", "SOUTH", "WEST"};

    static class Tile {
        int label;
        char[] sides;
        int rotation;

        public Tile(int lbl, String s) {
            label = lbl;
            sides = s.substring(0, 4).toCharArray();
        }

        public Tile (int lbl, char[] s) {
            label = lbl;
            sides = s;
        }

        public Tile(Tile from) {
            label = from.label;
            sides = from.sides.clone();
            rotation = from.rotation;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Tile)) return false;
            return label == ((Tile) o).label;
        }

        @Override
        public String toString() {
            final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String letterLabel = "[" + (label >= 0 ? letters.charAt(label) : "?")
                                             + "]" + Arrays.toString(sides)
                                             + directions[rotation];
            return letterLabel;
        }

        /* Enumerate all possible rotations matching the given constraint */
        List<Tile> matches(Tile constraint, int fixedSides) {
            List<Tile> result = new ArrayList<Tile>();

            /* First, create a clone of the tile we're trying ot match */
            Tile tmp = new Tile(this);

            for (int rotations = 0; rotations < 4; rotations++) {
                int count = fixedSides;
                for (int i = 0; i < 4; i++) {
                    //only counts if complementary "color"(char) is found at the required
                    //position
                    if (Math.abs(tmp.sides[i] - constraint.sides[i]) == ASCII_DIFF) {
                        count--;
                    }
                }

                /* If all the required sides of a rotated copy match, add it
                to the solution candidate set
                 */
                if (count <= 0) result.add(tmp);

                tmp = tmp.rotateForward();
            }
            return result;
        }

        /* Make a new tile from this instance, rotated clockwise once */
        Tile rotateForward() {
            Tile result = new Tile(this);
            for (int i = 0; i < 4; i++) {
                result.sides[(i+1) % 4] = sides[i];
            }
            result.rotation = (rotation + 1) % 4;
            return result;
        }
    }


    int width;
    Tile[][] board;
    List<Tile> input;

    public TilePuzzle(int w) {
        if (w < 3 || w > 5)
            throw new IllegalArgumentException("Puzzle only accepts width between 3 and 5");
        width = w;
        input = new ArrayList<Tile>();
        board = new Tile[width][width];
    }

    public TilePuzzle(int w, String fromFile) {
        this(w);
        fromFile(fromFile);
    }

    public void fromFile(String fileName) {
        Scanner s = null;
        String line = ""; int i = 0;
        try {
            s = new Scanner(new File(System.getProperty("user.home") + "/" + fileName));
            while (s.hasNextLine()) {
                line = s.nextLine();
                Tile t = new Tile(i, line.trim());
                input.add(t);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Point to the tile on board */
    public Tile tileAt(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < width)
            return board[x][y];
        return null;
    }

    @Override
    public List<Tile> getAllPieces() {
        List<Tile> result = new ArrayList<Tile>();
        for (Tile t : input) {
            result.add(t);
        }
        return result;
    }

    @Override
    public void makeMove(Tile t, int position) {
        board[position % width][position / width] = t;
    }

    @Override
    public List<Tile> legalMoves(Tile tile, int position) {
        int x = position % width; int y = position / width;

        /* Create a "dummy" tile that will represent a constraint for the
        current candidate. Moving left to right, downward, our possible
        neighbors can only be located north or west of the current position.
        The constraint tile will mirror the "colors" of its adjacent neighbors,
         */
        char[] matches = new char[4];
        int sideCount = 0; //how many sides should match at the same time

        //Mark which sides should match the neighbors
        Tile neighbor = tileAt(x, y - 1); //upper
        if (neighbor != null) {
            matches[NORTH] = neighbor.sides[SOUTH];
            sideCount += 1;
        }

        neighbor = tileAt(x - 1, y); //left
        if (neighbor != null) {
            matches[WEST] = neighbor.sides[EAST];
            sideCount += 1;
        }

        //Enumerate possible matches for a given tile and all its rotations
        Tile constraint = new Tile(-1, matches);
        return tile.matches(constraint, sideCount);
    }

    @Override
    public int stopValue() {
        return width * width;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                sb.append("\t" + board[j][i]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void prettyPrint() {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < width; i++) {
            String upperLine = ""; String midLine = ""; String lowerLine = "";
            for (int j = 0; j < width; j++) {
                Tile tile = board[j][i];
                upperLine += "\t---" +tile.sides[NORTH] + "---";
                midLine += "\t" + "|" + tile.sides[WEST] + " "
                                       + letters.charAt(tile.label) + " " + tile.sides[EAST] + "|";
                lowerLine += "\t---" + tile.sides[SOUTH] + "---";
            }
            System.out.println(upperLine);
            System.out.println(midLine);
            System.out.println(lowerLine);
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
       // TilePuzzle tp = new TilePuzzle(3, "puzzletest.txt");
       // tp.solveMe();
        long time0 = System.currentTimeMillis();
        TilePuzzle tp = new TilePuzzle(5, "puzzletest2.txt");
        tp.solveMe();
        System.out.println("Time: " + (System.currentTimeMillis() - time0));

       // tp = new TilePuzzle(5, "puzzletest2.txt");
       // tp.solveMe();
    }
}



