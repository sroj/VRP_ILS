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
    //INICIO DE ESTRUCTURAS PARA CALCULO DE LA MEJOR SOLUCION INICIAL
    List<List<Integer>> currentRoutes;
    List<Double> costOfRoutes;
    List<Integer> routeDemands;
    double totalCost;
    double totalDistance;
    Integer[] customers;
    //FIN DE ESTRUCTURAS PARA CALCULO DE LA MEJOR SOLUCION INICIAL
    private final int localSearchMaxIter;

    public ANTAlgorithm(VehicleRoutingProblem vrpInstance, int maxIter,
            double p, int alfa, int beta, double f, double g, int elitistAnts,
            int localSearchMaxIter) {
        this.p = p;
        int customersPlusDepot = vrpInstance.getNumberOfCustomers() + 1;
        this.vrpInstance = vrpInstance;
        this.elitistAnts = elitistAnts;
        customersByDistance = new int[customersPlusDepot][];
        ants = new Ant[vrpInstance.getNumberOfCustomers()];
        pheromones = new double[customersPlusDepot][customersPlusDepot];
        this.maxIterations = maxIter;
        initializeCustomersByDistance();
        initializePheromones(2);
        for (int i = 0; i < ants.length; i++) {
            ants[i] = new Ant(pheromones, vrpInstance, alfa, beta, f, g,
                    localSearchMaxIter);
        }
        bestSolution = new ArrayList<>(customersPlusDepot);
        bestDistance = Double.MAX_VALUE;
        bestRouteTime = Double.MAX_VALUE;

        currentRoutes = new ArrayList<>(vrpInstance.getNumberOfCustomers());
        for (int i = 0; i < vrpInstance.getNumberOfCustomers(); i++) {
            currentRoutes.add(new ArrayList<Integer>(vrpInstance.getNumberOfCustomers()));
        }
        costOfRoutes = new ArrayList<>(vrpInstance.getNumberOfCustomers());
        routeDemands = new ArrayList<>(vrpInstance.getNumberOfCustomers());
        customers = new Integer[customersPlusDepot];
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Integer(i);
        }
        constructInitialSolution();
        this.localSearchMaxIter = localSearchMaxIter;
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

    private static class Customer implements Comparable<Customer> {

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
        int customersSize = this.vrpInstance.getNumberOfCustomers();
        for (int k = 0; k < this.customersByDistance.length; k++) {
            this.customersByDistance[k] = new int[customersSize];
            Customer[] sortedByDistance = new Customer[customersSize + 1];
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

    private void constructInitialSolution() {
        initializePartition();
        int n = vrpInstance.getNumberOfCustomers();
        double s[][] = new double[n][n];
        calculateSavings(s);
        Index index = getBestSaving(s);
        while (index.getSaving() >= 0) {
            mergeRoutes(index);
            index = getBestSaving(s);
        }
        setFinalCost();
        updateBestRoutes();
        bestRouteTime = totalCost;
        bestDistance = totalDistance;
    }

    private void updateBestRoutes() {
        bestSolution.clear();
        for (int i = 0; i < currentRoutes.size(); i++) {
            bestSolution.add(new ArrayList<Integer>(
                    currentRoutes.get(i).size()));
            for (int j = 0; j < currentRoutes.get(i).size(); j++) {
                bestSolution.get(i).add(currentRoutes.get(i).get(j));
            }
        }
    }

    private Index getBestSaving(double[][] s) {
        int x = -1;
        int y = -1;
        double max = -1;
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s.length && i != j; j++) {
                if (s[i][j] > max) {
                    max = s[i][j];
                    x = i;
                    y = j;
                }
            }
        }
        if (x != -1 && y != -1) {
            s[x][y] = -1;
            s[y][x] = -1;
        }
        return (new Index(x + 1, y + 1, max));
    }

    private double getRouteCost(int i) {
        double cost;

        cost = vrpInstance.getCost(0, currentRoutes.get(i).get(0));
        for (int j = 0; j < currentRoutes.get(i).size() - 1; j++) {
            cost += vrpInstance.getCost(currentRoutes.get(i).get(j), currentRoutes.get(i).get(j + 1));
        }
        cost += vrpInstance.getCost(currentRoutes.get(i).get(currentRoutes.get(i).size() - 1), 0);
        return cost;
    }

    private int getIndexOfRouteI(int elem) {
        Integer element = new Integer(elem);
        int i = 0;
        for (List<Integer> route : currentRoutes) {
            if (route.indexOf(element) == 0) {
                return (i);
            }
            i++;
        }
        return (-1);
    }

    private int getIndexOfRouteJ(int elem) {
        Integer element = new Integer(elem);
        int i = 0;
        for (List<Integer> route : currentRoutes) {
            if (route.indexOf(element) == (route.size() - 1)) {
                return (i);
            }
            i++;
        }
        return (-1);
    }

    private void doMerge(int i, int j, double saving) {
        this.currentRoutes.get(i).addAll(this.currentRoutes.get(j));
        double newCost = getRouteCost(i);
        this.costOfRoutes.set(i, newCost);
        int newDemand = this.routeDemands.get(i) + this.routeDemands.get(j);
        this.routeDemands.set(i, newDemand);
        this.currentRoutes.remove(j);
        this.routeDemands.remove(j);
        this.costOfRoutes.remove(j);
        this.totalCost = this.totalCost - saving;
        this.totalDistance = this.totalDistance - saving;
    }

    private void initializePartition() {
        int i = 0;
        double cost;
        for (List<Integer> route : this.currentRoutes) {
            route.add(this.customers[i + 1]);
            cost = vrpInstance.getCost(0, i + 1) * 2;
            this.costOfRoutes.add(i, new Double(cost));
            this.routeDemands.add(new Integer(vrpInstance.getCustomerDemand(i + 1)));
            this.totalDistance += cost;
            this.totalCost += cost;
            i++;
        }
    }

    private void calculateSavings(double[][] s) {
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                double a = vrpInstance.getCost(i + 1, 0);
                double b = vrpInstance.getCost(0, j + 1);
                double c = vrpInstance.getCost(i + 1, j + 1);
                s[i][j] = (a + b) - c;
                s[j][i] = (a + b) - c;
            }
        }
    }

    private void mergeRoutes(Index index) {
        int i = getIndexOfRouteI(index.i);
        int j = getIndexOfRouteJ(index.j);
        if (i != -1 && j != -1) {
            double costIJ = getRouteCost(i) + getRouteCost(j)
                    + ((currentRoutes.get(i).size() + currentRoutes.get(j).size())
                    * vrpInstance.getDropTime()) - index.saving;
            int demandIJ = this.routeDemands.get(i) + this.routeDemands.get(j);
            if (costIJ < vrpInstance.getMaximumRouteTime()
                    && demandIJ <= vrpInstance.getVehicleCapacity() && i != j) {
                doMerge(i, j, index.saving);
            }
        }
    }

    private void setFinalCost() {
        this.totalCost = vrpInstance.getNumberOfCustomers() * vrpInstance.getDropTime();
        this.totalDistance = 0;
        for (Double route : this.costOfRoutes) {
            totalCost += route;
            totalDistance += route;
        }
    }

    private static class Index {

        int i;
        int j;
        double saving;

        public Index(int i, int j, double saving) {
            this.i = i;
            this.j = j;
            this.saving = saving;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public double getSaving() {
            return saving;
        }
    }
}
