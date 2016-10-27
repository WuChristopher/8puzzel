
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class Board {
    // fields
    private final char[][] blocks;
    private final int dim;
    private final int hammingVal;
    private final int manhattanVal;
    private final int blankRow;
    private final int blankCol;

    // constructor
    public Board(int[][] blocks) {
        if (null == blocks) throw new NullPointerException("Board Constructor");
        int m = blocks.length;
        if (0 == m) throw new IllegalArgumentException("0 row of blocks");
        int n = blocks[0].length;
        if (n != m) throw new IllegalArgumentException("Not A Square Board");
        dim = n;
        this.blocks = new char[n][n];
        int hammingVal = 0;
        int manhattanVal = 0;
        int blankRow = 0;
        int blankCol = 0;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                this.blocks[i][j] = (char) blocks[i][j];

                /**
                 * If blocks[i][i] equals 0, it's not a block, which shouldn't be
                 * taken into account when calculating Hamming value.
                 * For hamming value, when all the blocks are in the correct position,
                 * it should be 0. For instance, 3x3 blocks, a[0][0] should be 1,...,
                 * a[2][1] should be 8, that is, it equals (dim * i + j + 1). So if
                 * the block is not 0,which is not a real block, and if the block does
                 * not equal (dim * i + j + 1) as well, this block is out of position.
                 * At this time, Hamming value should be incremented.
                 */
                if (blocks[i][j] != dim * i + j + 1 && blocks[i][j] != 0)
                    hammingVal++;
                // calc manhattan val for each block and record black pos
                int row;
                int col;
                // don't add up to manhattan if it's the blank, just record
                // the position of the blank
                if (0 == blocks[i][j]) {
                    blankRow = i;
                    blankCol = j;
                    continue;
                } else {
                    row = (blocks[i][j] - 1) / dim;
                    col = (blocks[i][j] - 1) % dim;
                }
                manhattanVal += (Math.abs(i - row) + Math.abs(j - col));
            }
        }
        this.hammingVal = hammingVal;
        this.manhattanVal = manhattanVal;
        this.blankRow = blankRow;
        this.blankCol = blankCol;
    }

    // methods
    public int dimension() { return dim; }

    public int hamming() { return hammingVal; }

    public int manhattan() { return  manhattanVal; }

    public boolean isGoal() { return hammingVal == 0; }

    public Board twin() {
        int[][] twinBlocks = new int[dim][dim];
        /**
         * Do Not use System.arraycopy() to clone the 2d array, cause' the
         * 2d array is just a 1d array holding references to each sub 1d array
         * and System.arraycopy() uses shallow copy, thus the fields of the outer
         * 1d array of two arrays will point to the same objects each. Later on,
         * we change the values of one 2d array, then the change will be reflected
         * in the other one. Consequently, we manually do the deep copy as follows.
         */
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                twinBlocks[i][j] = (int) blocks[i][j];
            }
        }
        if (twinBlocks[0][0] != 0) {
            if (twinBlocks[dim - 1][dim - 2] != 0)
                exch(twinBlocks, 0, 0, dim - 1, dim - 2);
            else exch(twinBlocks, 0, 0, dim - 1, dim - 1);
        } else {
            if (twinBlocks[dim - 1][dim - 2] != 0)
                exch(twinBlocks, 0, 1, dim - 1, dim - 2);
            else exch(twinBlocks, 0, 1, dim - 1, dim - 1);
        }
        return new Board(twinBlocks);
    }

    private void exch(int[][] arr, int i, int j, int m, int n) {
        int temp = arr[i][j];
        arr[i][j] = arr[m][n];
        arr[m][n] = temp;
    }

    @Override
    public boolean equals(Object y) {
        if (this == y) return true;
        if (null == y) return false;
        if (getClass() != y.getClass()) return false;
        final Board board = (Board) y;
        if (dim != board.dim) return false;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (blocks[i][j] != ((Board) y).blocks[i][j])
                    return false;
            }
        }
        return true;
    }

    /**
     * Every time we override the equals method, we shall override the hashCode
     * method in order to stick to Java's Equality contract
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + dim;
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                hash = 7 * hash + blocks[i][j];
            }
        }
        return hash;
    }

    public Iterable<Board> neighbors() {
        int[][] neighBlocks = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                neighBlocks[i][j] = (int) blocks[i][j];
            }
        }
        ArrayList<Board> arrList = new ArrayList<>(4);
        if (blankRow - 1 >= 0) {
            exch(neighBlocks, blankRow, blankCol, blankRow - 1, blankCol);
            Board board = new Board(neighBlocks);
            arrList.add(board);
            exch(neighBlocks, blankRow, blankCol, blankRow - 1, blankCol);
        }
        if (blankRow + 1 <= dim - 1) {
            exch(neighBlocks, blankRow, blankCol, blankRow + 1, blankCol);
            Board board = new Board(neighBlocks);
            arrList.add(board);
            exch(neighBlocks, blankRow, blankCol, blankRow + 1, blankCol);
        }
        if (blankCol - 1 >= 0) {
            exch(neighBlocks, blankRow, blankCol, blankRow, blankCol - 1);
            Board board = new Board(neighBlocks);
            arrList.add(board);
            exch(neighBlocks, blankRow, blankCol, blankRow, blankCol - 1);
        }
        if (blankCol + 1 <= dim - 1) {
            exch(neighBlocks, blankRow, blankCol, blankRow, blankCol + 1);
            Board board = new Board(neighBlocks);
            arrList.add(board);
            exch(neighBlocks, blankRow, blankCol, blankRow, blankCol + 1);
        }
        return arrList;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dim + "\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s.append(String.format("%2d ", (int) blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
	// write your code here
        int[][] a = new int[3][3];
        a[0][0] = 8;
        a[0][1] = 1;
        a[0][2] = 3;
        a[1][0] = 4;
        a[1][1] = 0;
        a[1][2] = 2;
        a[2][0] = 7;
        a[2][1] = 6;
        a[2][2] = 5;
        Board bd1 = new Board(a);
        StdOut.print(bd1.toString());
        StdOut.println("Dim: " + bd1.dimension());
        StdOut.println("Hamming: " + bd1.hamming());
        StdOut.println("Manhattan: " + bd1.manhattan());
        StdOut.println("Is Goal? " + bd1.isGoal());
        Board bd1Twin = bd1.twin();
        StdOut.print(bd1Twin.toString());
        Iterable<Board> iter = bd1.neighbors();
        for (Board bd : iter)
            StdOut.print(bd.toString());
    }
}
