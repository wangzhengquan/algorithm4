import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private int T;
    private int n = 0;
    private double[] thresholds;


    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        this.T = trials;
        this.n = n;
        thresholds = new double[trials];
        for (int i = 0; i < trials; i++) {
            thresholds[i] = threshold();
        }
    }

    private double threshold() {
        int row, col, count = 0;
        Percolation percolation = new Percolation(n);
        /** the previous n times were unnecessary to execute percolates method to check if the grid was percolation,
         * since a percolation grid has at least n opened sites
         * */
        while (count < n || !percolation.percolates()) {
            row = StdRandom.uniform(n);
            col = StdRandom.uniform(n);
            percolation.open(row + 1, col + 1);
            count++;
        }
        double res = (double) (percolation.numberOfOpenSites()) / (n * n);
        // System.out.printf("threshold = %f\n", res);
        return res;
    }

    // sample mean of percolation threshold
    public double mean() {

        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {

        return StdStats.stddev(thresholds);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - stddev() / Math.sqrt(T);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + stddev() / Math.sqrt(T);
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java-algs4 PercolationStats n T");
        }

        // System.out.println("test==" + StdStats.stddev(thresholds));
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        Stopwatch watch = new Stopwatch();
        PercolationStats stats = new PercolationStats(n, trials);
        System.out.printf("mean                    = %f\n", stats.mean());
        System.out.printf("stddev                  = %f\n", stats.stddev());
        System.out.printf("95%% confidence interval = [%f, %f]\n",
                          stats.confidenceLo(), stats.confidenceHi());

        System.out.printf("time elapsed %f\n", watch.elapsedTime());
    }

}
