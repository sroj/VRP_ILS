package VRP_ANT;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.VehicleRoutingProblem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final double p;
    private final ArrayList<ArrayList<Integer>> bestSolution;
    private double bestDistance;
    private double bestRouteTime;
    private final int elitistAnts;

    public ANTAlgorithm(VehicleRoutingProblem vrpInstance, int maxIter,
            double p, int alfa, int beta, double f, double g, int elitistAnts) {
        this.p = p;
        int customersPlusDepot = vrpInstance.getNumberOfCustomers() + 1;
        this.vrpInstance = vrpInstance;
        this.elitistAnts = elitistAnts;
        customersByDistance = new int[customersPlusDepot][];
        ants = new Ant[vrpInstance.getNumberOfCustomers()];
        pheromones = new double[customersPlusDepot][customersPlusDepot];
        this.maxIterations = maxIter;
        initializeCustomersByDistance();
        initializePheromones(1.0);
        for (int i = 0; i < ants.length; i++) {
            ants[i] = new Ant(pheromones, vrpInstance, alfa, beta, f, g);
        }
        bestSolution = new ArrayList<>(customersPlusDepot);
        bestDistance = Double.MAX_VALUE;
        bestRouteTime = Double.MAX_VALUE;
    }

    private void initializePheromones(double initialValue) {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] = initialValue;
            }
        }
    }

    private void evaporatePheromones() {
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] *= p;
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
        int initialCustomer;

        tIni = System.nanoTime();
        tFinBest = System.nanoTime();
        while (iteration < maxIterations) {
            initialCustomer = 1;
            for (Ant a : ants) {
                try {
                    a.explore(initialCustomer);
                } catch (Exception ex) {
                    System.out.println("Excepcion atrapada en ANTAlgorithm");
                    ex.printStackTrace(System.out);
                    System.exit(10);
                }
                initialCustomer++;
            }
            Arrays.sort(ants);
            if (ants[0].getTotalDistance() < bestDistance) {
                bestDistance = ants[0].getTotalDistance();
                bestRouteTime = ants[0].getTotalRouteTime();
                updateBestSolution(ants[0].getSolution());
                tFinBest = System.nanoTime();
                bestIteration = iteration;
            }
            evaporatePheromones();
            layElitistAntsPheromones();
            for (int i = 1; i <= elitistAnts - 1; i++) {
                ants[i].layDownPheromones(i, elitistAnts);
            }
            iteration++;
        }
        tFin = System.nanoTime();
        double tBest = (tFinBest - tIni) / 1000000000.0d;
        double tTotal = (tFin - tIni) / 1000000000.0d;
        String finalRoutes = routesToString();
        if (!validateResult()) {
            System.out.println("El resultado no pudo ser validado");
        } else {
            System.out.println("Resultado OK");
            double distance = calculateRouteDistance(bestSolution);
            System.out.println("Distancia a pie: " + distance);
            System.out.println("Distancia incremental: " + bestDistance);
        }
        return new ANTSolutionSet(bestDistance, bestIteration, iteration + 1,
                tBest, bestSolution.size(), finalRoutes, tTotal);
    }

    private String routesToString() {
        String s = "";
        for (ArrayList<Integer> route : bestSolution) {
            s = s + "0 ";
            for (Integer element : route) {
                s = s + element + " ";
            }
            s = s + "0\n\n";
        }
        return s;
    }

    private void layElitistAntsPheromones() {
        for (ArrayList<Integer> route : bestSolution) {
            pheromones[0][route.get(0)] += elitistAnts * (1 / bestDistance);
            for (int i = 0; i < route.size() - 1; i++) {
                pheromones[i][i + 1] += elitistAnts * (1 / bestDistance);
            }
            pheromones[route.get(route.size() - 1)][0] +=
                    elitistAnts * (1 / bestDistance);
        }
    }

    private void updateBestSolution(
            ArrayList<ArrayList<Integer>> newBestSolution) {
        bestSolution.clear();
        for (ArrayList<Integer> route : newBestSolution) {
            ArrayList<Integer> newRoute = new ArrayList<>(route.size());
            for (Integer customer : route) {
                newRoute.add(new Integer(customer));
            }
            bestSolution.add(newRoute);
        }
    }

    private double calculateRouteDistance(ArrayList<ArrayList<Integer>> routes) {
        double distance = 0;
        for (int j = 0; j < routes.size(); j++) {
            ArrayList<Integer> route = routes.get(j);
            distance += vrpInstance.getCost(0, route.get(0));
            for (int i = 1; i < route.size(); i++) {
                distance += vrpInstance.getCost(route.get(i - 1), route.get(i));
            }
            distance += vrpInstance.getCost(route.get(route.size() - 1), 0);
        }
        return distance;
    }

    private boolean validateResult() {
        int n = 0;
        for (List<Integer> route : bestSolution) {
            n += route.size();
        }
        if (n != vrpInstance.getNumberOfCustomers()) {
            System.out.println("Invalida por numero de customers");
            return false;
        }

        int i = 0;
        double costoRuta;
        int demandaRuta;
        for (ArrayList<Integer> route : bestSolution) {
            costoRuta = vrpInstance.getCost(0, route.get(0));
            demandaRuta = 0;
            for (int j = 0; j < route.size() - 1; j++) {
                costoRuta += vrpInstance.getCost(route.get(j), route.get(j + 1));
                demandaRuta += vrpInstance.getCustomerDemand(route.get(j));
            }
            demandaRuta += vrpInstance.getCustomerDemand(route.get(route.size() - 1));
            costoRuta += vrpInstance.getCost(route.get(route.size() - 1), 0);
            if (demandaRuta > vrpInstance.getVehicleCapacity()
                    || costoRuta > vrpInstance.getMaximumRouteTime()) {
                System.out.println("Invalida por demanda o por maximum route time");
                return false;
            }
            i++;
        }

        int ocurrences[] = new int[vrpInstance.getNumberOfCustomers()];
        for (int j = 0; j < vrpInstance.getNumberOfCustomers(); j++) {
            ocurrences[j] = 0;
        }

        for (List<Integer> route : bestSolution) {
            for (Integer element : route) {
                ocurrences[element - 1] += 1;
            }
        }

        for (int j = 0; j < vrpInstance.getNumberOfCustomers(); j++) {
            if (ocurrences[j] != 1) {
                System.out.println("Invalida por repeticion de customer");
                return false;
            }
        }
        return true;
    }
}
