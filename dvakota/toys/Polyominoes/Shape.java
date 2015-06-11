package dvakota.toys.Polyominoes;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 6/10/15
 */
public class Shape {
    static class Square {
        int x; int y;
        String chr;

        public Square(int x, int y, String s) {
            this.x = x; this.y = y;
            this.chr = s;
        }

        public int hashCode() {
            return (chr + x + y).hashCode();
        }

        public String toString() {
            return chr;
        }
    }

    Square[][] grid;
    String chr;

    public static List<Shape> build(int size, String chr) {
        Shape init = new Shape(size, chr);
        return generate(init);
    }

    /**
     * Construct an initial Shape witn only one Square occupying the leftmost top position
     * @param size number of single squares
     * @param chr  character representation of the square
     */
    private Shape(int size, String chr) {
        grid = new Square[size][size];
        grid[0][0] = new Square(0, 0, chr);
        this.chr = chr;
    }


    /**
     * Copy constructor - makes a deep clone of existing Shape
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


    public static List<Shape> generate(Shape init) {
        return generateShapes(new ArrayList<Shape>(), init.grid[0][0], init, 1);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Shape)) return false;
        Shape sh = (Shape) o;
        final int r = 4;
        say(this);
        say(sh);
        say("---");

        Shape check = sh;
        say("Equals: rotation");
        for (int i = 0; i < r ; i++) {
            say(i + "\n" + check);
            if (equalGrids(this.grid, check.grid)) return true;
            check = check.rotate();
        }

        Shape flipped = sh.flip();
        say("Equals: flip rotation");
        for (int i = 0; i < r ; i++) {
            say(i + "\n" + flipped);
            if (equalGrids(this.grid, flipped.grid)) return true;
            flipped = flipped.rotate();

        }
        say("Shape \n" + this + " and \n" + sh + " are NOT equal!");
        return false;
    }

    //todo: l-shaped equals consider the gaps
    private boolean equalGrids(Square[][] g1, Square[][] g2) {
        String g1s = "";
        String g2s = "";
        for (int y = 0; y < g1.length; y++) {
           for (int x = 0; x < g1.length; x++) {
               if (g1[x][y] != null) g1s += "1";
               else g1s +="0";
               if (g2[x][y] != null) g2s += "1";
               else g2s += "0";
           }
        }
        return g1s.equals(g2s);
    }

    private Shape rotate() {
        int size = grid.length - 1;
        Shape result = new Shape(grid.length, chr);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid.length; x++) {
                result.grid[y][size - x] = grid[x][y];
            }
        }
        //translate
        while(result.grid[0][0] == null) {
            for (int y = 0; y < result.grid.length; y++) {
                for (int x = 1; x < result.grid.length; x++) {
                    result.grid[x-1][y] = result.grid[x][y];
                    result.grid[x][y] = null;
                }
            }
        }
        say("Transrotated: \n" + result);
        return result;
    }

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

    private static List<Shape> generateShapes(List<Shape> result, Square current,
                                              Shape shape, int filled) {
        if (filled == shape.grid.length) {
     //       say("Adding \n" + shape);
            if (!result.contains(shape)) result.add(shape);
            else {
         //       say("Shape already exists: " + shape);
            }
        } else {
            List<Square> adjacent = findAdjacent(current, shape);
       //     say("Found " + adjacent.size() + " positions\n");
            for (Square s : adjacent) {
         //       say("Adding square at " + s.x + "," + s.y);
                Shape newShape = new Shape(shape).addSquare(s);
           //     say("New shape \n" + newShape);
                generateShapes(result, s, newShape, filled+1);
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

        int test = 4;

        List<Shape> listOfShapes = Shape.build(test, "#");
        say("Output:");
        for (Shape s : listOfShapes) {
            say("Shape:\n" + s);
        }
        say("Total generated shapes: " + listOfShapes.size());

    }

}
