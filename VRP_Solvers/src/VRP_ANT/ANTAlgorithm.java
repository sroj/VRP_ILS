package VRP_ANT;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.VehicleRoutingProblem;
import java.util.Arrays;

/**
 *
 * @author Marlin Aranguren
 * @author Simon Rojas
 */
public class ANTAlgorithm implements MetaheuristicOptimizationAlgorithm {

    private final VehicleRoutingProblem vrpInstance;
    private final int[][] customersByDistance;
    private Ant[] ants;
    private double[][] pheromones;
    private final int maxIterations;

    public ANTAlgorithm(VehicleRoutingProblem vrpInstance, int maxIter) {
        int customersPlusDepot = vrpInstance.getNumberOfCustomers() + 1;
        this.vrpInstance = vrpInstance;
        this.customersByDistance = new int[customersPlusDepot][];
        this.ants = new Ant[vrpInstance.getNumberOfCustomers()];
        this.pheromones = new double[customersPlusDepot][customersPlusDepot];
        this.maxIterations = maxIter;
        initializeCustomersByDistance();
        initializePheromones(1.0);
    }

    private void initializePheromones(double initialValue) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] = initialValue;
            }
        }
    }

    private class Customer implements Comparable<Customer> {

        private int customerNumber;
        private double distance;

        public Customer(int customerNumber, double distance) {
            this.customerNumber = customerNumber;
            this.distance = distance;
        }

        public int getCustomerNumber() {
            return customerNumber;
        }

        @Override
        public int compareTo(Customer t) {
            if (this.distance > t.distance) {
                return 1;
            } else if (this.distance < t.distance) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "Customer: " + customerNumber + " Distancia: "
                    + distance;
        }
    }

    private void initializeCustomersByDistance() {
        int customers = this.vrpInstance.getNumberOfCustomers();
        for (int k = 0; k < this.customersByDistance.length; k++) {
            this.customersByDistance[k] = new int[customers];
            Customer[] sortedByDistance = new Customer[customers + 1];
            for (int i = 0; i < sortedByDistance.length; i++) {
                sortedByDistance[i] =
                        new Customer(i, this.vrpInstance.getCost(k, i));
            }
            Arrays.sort(sortedByDistance);
            for (int i = 0; i < customersByDistance[k].length; i++) {
                this.customersByDistance[k][i] =
                        sortedByDistance[i].getCustomerNumber();
            }
        }
    }

    @Override
    public ANTSolutionSet execute() {
        int iteration = 0;
        int bestIteration = 0;
        long tIni;
        long tFin;
        long tFinBest;

        tIni = System.nanoTime();
        tFinBest = System.nanoTime();
        while (iteration < maxIterations) {

            iteration++;
        }
        tFin = System.nanoTime();
        double tBest = (tFinBest - tIni) / 1000000000.0d;
        double tTotal = (tFin - tIni) / 1000000000.0d;

        return new ANTSolutionSet(bestIteration, bestIteration, bestIteration, tFin, iteration, null, iteration);
    }
}
