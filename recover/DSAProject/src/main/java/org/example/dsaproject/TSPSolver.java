package org.example.dsaproject;
import java.util.Arrays;

public class TSPSolver {
    private double[][] dist;
    private int N;
    private int start, end;
    private double[][] memo;
    private int[][] parent;
    public static double totalDistance;

    public TSPSolver(double[][] dist, int start, int end) {
        this.dist = dist;
        this.N = dist.length;
        this.start = start;
        this.end = end;
        this.memo = new double[N][(1 << N)];
        this.parent = new int[N][(1 << N)];
        for (double[] row : memo) {
            Arrays.fill(row, -1);
        }
    }

    private double tsp(int pos, int mask) {
        if (mask == (1 << N) - 1) {  // All cities visited
            return dist[pos][end];
        }

        if (memo[pos][mask] != -1) {
            return memo[pos][mask];
        }

        double minCost = Double.MAX_VALUE;
        for (int city = 0; city < N; city++) {
            if ((mask & (1 << city)) == 0 && city != start) {  // If city not visited and not starting point
                double newCost = dist[pos][city] + tsp(city, mask | (1 << city));
                if (newCost < minCost) {
                    minCost = newCost;
                    parent[pos][mask] = city;
                }
            }
        }

        return memo[pos][mask] = minCost;
    }

    public double findMinCost() {
        return tsp(start, 1 << start);
    }

    public int[] printPath() {
        int mask = 1 << start;
        int pos = start;
        int[] path = new int[N];
        path[0]=pos;
        int i=1;
        while (mask != (1 << N) - 1) {

            int next = parent[pos][mask];
            path[i++]=next;
            mask |= (1 << next);
            pos = next;
        }
        return path;
    }


}

