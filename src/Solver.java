import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

/**
 * Created by zack on 14/10/2016.
 */
public class Solver {
    // fields
    private boolean solvable = false;
    private SearchNode sNode;

    /**
     * When comparing two nodes' priority, first compare the Manhattan priority, if the
     * Manhattan priority equals each other, use the Hamming value to break the equality.
     */
    private class SearchNode implements Comparable<SearchNode> {
        public final int moves;
        public final Board board;
        public final SearchNode prev;
        public SearchNode(Board bd, int moves, SearchNode prev) {
            board = bd;
            this.moves = moves;
            this.prev = prev;
        }

        @Override
        public int compareTo(SearchNode o) {
            if (moves + board.manhattan() < o.moves + o.board.manhattan())
                return -1;
            if (moves + board.manhattan() > o.moves + o.board.manhattan())
                return 1;
            if (board.hamming() < o.board.hamming())
                return -1;
            if (board.hamming() > o.board.hamming())
                return 1;
            return 0;
        }
    }
    // constructor
    public Solver(Board initial) {
        if (null == initial)
            throw new NullPointerException("Solver Constructor");

        Board twin = initial.twin();
        sNode = new SearchNode(initial, 0, null);
        SearchNode sNodeTwin = new SearchNode(twin, 0, null);
        MinPQ<SearchNode> minPQ = new MinPQ<>();
        MinPQ<SearchNode> minPQTwin = new MinPQ<>();
        minPQ.insert(sNode);
        minPQTwin.insert(sNodeTwin);
        /**
         * Per A* algorithm, the board or any twin can be solved. Thus we try solving
         * the board and its twin simultaneously. If the twin is solved, the board is
         * deemed to be unsolvable.
         */
        while (true) {
            if (!minPQ.isEmpty()) {
                sNode = minPQ.delMin();
                if (sNode.board.isGoal()) {
                    solvable = true;
                    break;
                } else {
                    for (Board neighbor : sNode.board.neighbors()) {
                        if (sNode.prev != null && neighbor.equals(sNode.prev.board))
                            continue;
                        else
                            minPQ.insert(new SearchNode(neighbor, sNode.moves + 1, sNode));
                    }
                }
            }
            if (!minPQTwin.isEmpty()) {
                sNodeTwin = minPQTwin.delMin();
                if (sNodeTwin.board.isGoal()) break;
                else {
                    for (Board neighbor : sNodeTwin.board.neighbors()) {
                        if (sNodeTwin.prev != null && neighbor.equals(sNodeTwin.prev.board))
                            continue;
                        else
                            minPQTwin.insert(new SearchNode(neighbor, sNodeTwin.moves + 1, sNodeTwin));
                    }
                }
            }
        }
    }

    // methods
    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return solvable? sNode.moves : -1;
    }

    public Iterable<Board> solution() {
        if (solvable) {
            /**
             * Use LinkedList instead of ArrayList, for all the boards are inserted into
             * the first place. ArrayList is implemented by array, which is inefficient.
             */
            LinkedList<Board> sol = new LinkedList<>();
            SearchNode sn = sNode;
            do {
                sol.addFirst(sn.board);
                sn = sn.prev;
            } while (sn != null);
            return sol;
        } else return null;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
