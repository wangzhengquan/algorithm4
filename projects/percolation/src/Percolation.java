import edu.princeton.cs.algs4.UF;

public class Percolation {

    // sites states, 0  blocked, 1 opened
    private Integer[][] sites;

    private UF uf;
    // n-by-n grid of sites
    private int n;

    private int UFN = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        this.n = n;

        sites = new Integer[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // 0 means blocked, 1 means opened
                sites[i][j] = 0;
            }
        }
        //add two additional virtual sites,  n-2  as virtual top sites, n-1 as virtual bottom site
        UFN = n * n + 2;
        uf = new UF(UFN);

    }

    private int virtualTopSt() {
        return UFN - 2;
    }

    private int virtualBottomSt() {
        return UFN - 1;
    }

    private boolean connected(int p, int q) {
        return uf.find(p) == uf.find(q);
    }

    private void checkRowCol(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException("invalid row or col index");
        }

    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkRowCol(row, col);
        row--;
        col--;
        // 0 means blocked
        if (sites[row][col] == 0) {
            // set site opened
            sites[row][col] = 1;
            if (row == 0) {
                // union virtual top with one in the first row
                uf.union(col, virtualTopSt());
            }
            else if (sites[row - 1][col] == 1) {
                // up
                uf.union(row * n + col, (row - 1) * n + col);
            }

            if (row == n - 1) {
                // union virtual bottom with one in the last row
                uf.union(row * n + col, virtualBottomSt());
            }
            else if (sites[row + 1][col] == 1) {
                // down
                uf.union(row * n + col, (row + 1) * n + col);
            }

            if (col - 1 >= 0 && sites[row][col - 1] == 1) {
                // left
                uf.union(row * n + col, (row) * n + col - 1);
            }
            if (col + 1 < n && sites[row][col + 1] == 1) {
                // right
                uf.union(row * n + col, (row) * n + col + 1);
            }
        }

    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkRowCol(row, col);
        return sites[row - 1][col - 1] == 1;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkRowCol(row, col);
        return connected((row - 1) * n + col - 1, virtualTopSt());

    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        int count = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                count += sites[row][col];
            }
        }

        return count;
    }

    // does the system percolate?
    public boolean percolates() {

        return connected(virtualBottomSt(), virtualTopSt());
    }


}
