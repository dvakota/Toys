package dvakota.toys.Polyominoes;

import java.util.ArrayList;
import java.util.List;

public class Shape {

    static class Square {
        int x; int y;
        String chr;

        public Square(int x, int y, String s) {
            this.x = x; this.y = y;
            this.chr = s;
        }

        public String toString() {
            return chr == null? " " : chr;
        }
    }

    Square[][] grid;
    String chr;

    /** Generates a list of unique polyominoes of a given order
     *
     * @param size order (number of squares)
     * @param chr  ascii representation of a single square
     * @return
     */
    public static List<Shape> build(int size, String chr) {
        Shape init = new Shape(size, chr);
        return generate(init);
    }

    /**
     * Construct an initial Shape witn only one Square occupying the
     * leftmost top position
     * @param size number of single squares
     * @param chr  character representation of the square
     */
    private Shape(int size, String chr) {
        grid = new Square[size][size];
        grid[0][0] = new Square(0, 0, chr);
        this.chr = chr;
    }


    /**
     * Copy constructor - makes a clone of the existing Shape
     * (does not clone the Squares, references are copied)
     * @param src  the Shape to clone
     */
    private Shape(Shape src) {
        int size = src.grid.length;
        grid = new Square[size][size];
        for (int row = 0; row < src.grid.length; row++) {
            for (int col = 0; col < src.grid.length; col++) {
                this.grid[col][row] = src.grid[col][row];
            }
        }
    }

    private  Shape addSquare(Square s) {
        grid[s.x][s.y] = s;
        return this;
    }

    private static List<Shape> generate(Shape init) {
        return generateShapes(new ArrayList<Shape>(), init.grid[0][0], init, 1);
    }

    /**
     * Checks for Shape equality by comparing all possible transformations
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (!(o instanceof Shape)) return false;
        Shape sh = (Shape) o;
        final int r = 4;
        Shape rotated = sh;
        for (int i = 0; i < r ; i++) {
            if (equalGrids(this, rotated)) return true;
            rotated = rotated.rotate();
        }

        Shape flipped = sh.flip().translate();
        for (int i = 0; i < r ; i++) {
            if (equalGrids(this, flipped)) return true;
            flipped = flipped.rotate();
        }
        return false;
    }

    /*** Shape transformation methods ***/

    //Transposition - rotate the grid 90 degrees clockwise
    private Shape rotate() {
        int size = grid.length - 1;
        Shape result = new Shape(grid.length, chr);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                result.grid[x][y] = grid[y][size - x];
            }
        }

        result = result.translate();
        return result;
    }

    //Flip along x-axis (construct horizontal mirror image)
    private Shape flip() {
        int size = grid.length - 1;
        Shape result = new Shape(grid.length, chr);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                result.grid[size-x][y] = grid[x][y];
            }
        }
        return result;
    }

    //Translate the shape to origin (0,0)
    private Shape translate() {
        int xoffset = this.leftmost();
        int yoffset = this.topmost();
        Shape trans = new Shape(grid.length, chr);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                int newX = (x + grid.length - xoffset) % grid.length;
                int newY = (y + grid.length - yoffset) % grid.length;
                trans.grid[newX][newY] = grid[x][y];
            }
        }
        return trans;
    }

    //Find the topmost Y position
    private int topmost() {
        int minY = grid.length;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (grid[x][y] != null && y < minY) {
                    return minY;
                }
            }
        }
        return minY;
    }

    //Find the leftmost X position
    private int leftmost() {
        int minX = grid.length;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                if (grid[x][y] != null && x < minX) {
                    minX = x;
                }
            }
        }
        return minX;
    }

    //Compares the grids of two shapes by converting them to a bitstring
    //(0 - empty, 1 - takem)
    private boolean equalGrids(Shape g1, Shape g2) {
        String g1s = "";
        String g2s = "";
        for (int y = 0; y < g1.grid.length; y++) {
            for (int x = 0; x < g1.grid.length; x++) {
                if (g1.grid[x][y] != null) g1s += "1";
                else g1s +="0";
                if (g2.grid[x][y] != null) g2s += "1";
                else g2s += "0";
            }
        }
        return g1s.equals(g2s);
    }


    @Override
    public String toString() {
        String result = "";
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                result += (grid[x][y] == null ? ' ' : grid[x][y].chr);
            }
            result += "\n";
        }
        return result;
    }

    //Recursively add squares to a shape, starting with a single square
    private static List<Shape> generateShapes(List<Shape> result, Square current,
                                              Shape shape, int filled) {
        if (filled == shape.grid.length) {
            if (!result.contains(shape)) result.add(shape);
        } else {
            List<Square> adjacent = findAdjacent(current, shape);
            for (Square s : adjacent) {
                Shape newShape = new Shape(shape).addSquare(s);
                generateShapes(result, s, newShape, filled + 1);
            }
        }
        return result;
    }

    private static List<Square> findAdjacent(Square s, Shape src) {
        int size = src.grid.length;
        List<Square> result = new ArrayList<Square>();
        for (int y = s.y - 1; y < s.y + 2; y++) {
            for (int x = s.x - 1; x < s.x + 2; x++) {
                if (x == s.x && y == s.y) continue;
                if ((x >= 0 && x < size) && (y >= 0 && y < size)) {
                    if (isContiguous(x, y, src) && src.grid[x][y] == null) {
                        result.add(new Square(x, y, s.chr));
                    }
                }
            }
        }
        return result;
    }

    //Only allowed to add a square diagonally if its position is contiguous
    //with another filled square on the grid
    private static boolean isContiguous(int x, int y, Shape s) {
        int size = s.grid.length;
        for (int xx = x - 1; xx < x + 2; xx++) {
            if ((xx >= 0 && xx < size) && (s.grid[xx][y] != null))
                return true;
        }
        for (int yy = y - 1; yy < y + 2; yy++) {
            if ((yy >= 0 && yy < size) && (s.grid[x][yy] != null))
                return true;
        }
        return false;
    }


    private static void say(Object o) {
        System.out.println(o);
    }

    public static void main(String[] args) {
        int test = 5; String character = "#";
        if (args.length != 0)
            test = Integer.parseInt(args[0]);
        if (args.length == 2)
            character = args[1];

        List<Shape> listOfShapes = Shape.build(test, character);
        say("Generating polyominoes of the order of " + test);
        for (Shape s : listOfShapes) {
            say(s);
        }
        say("Total generated unique shapes: " + listOfShapes.size());
    }
}
