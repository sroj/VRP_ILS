package VRP_ANT;

import java.util.ArrayList;

/**
 * La clase Ant representa a una hormiga individual.
 *
 * @author Marlin Aranguren
 * @author Simon Rojas
 */
public class Ant implements Comparable<Ant> {

    private int[][] customersByDistance;
    private double[][] pheromones;
    private int initialCustomer;
    private double totalDistance;
    private ArrayList<ArrayList<Integer>> candidateLists;
    private ArrayList<ArrayList<Integer>> otherCustomersLists;
    private ArrayList<ArrayList<Integer>> solution;

    public Ant(int[][] customersByDistance, double[][] pheromones,
            int initialCustomer) {
        this.customersByDistance = customersByDistance;
        this.pheromones = pheromones;
        this.initialCustomer = initialCustomer;
        this.solution =
                new ArrayList<ArrayList<Integer>>(customersByDistance.length);
        this.candidateLists =
                new ArrayList<ArrayList<Integer>>(customersByDistance.length);
        this.otherCustomersLists =
                new ArrayList<ArrayList<Integer>>(customersByDistance.length);
    }

    void explore() {
    }

    @Override
    public int compareTo(Ant t) {
        if (this.totalDistance < t.totalDistance) {
            return -1;
        } else if (this.totalDistance > t.totalDistance) {
            return 1;
        } else {
            return 0;
        }
    }
}
