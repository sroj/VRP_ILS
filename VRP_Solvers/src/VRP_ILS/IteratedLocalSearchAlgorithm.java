package VRP_ILS;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.VehicleRoutingProblem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm
        implements MetaheuristicOptimizationAlgorithm {

    //INICIO de estructuras para representar una solucion al problema
    Integer[] customers;
    List<List<Integer>> currentRoutes;
    List<Integer> costOfRoutes;
    List<Integer> routeDemands;
    int totalCost;
    int totalDistance;
    List<List<Integer>> bestRoutes;
    int bestTotalCost;
    int bestTotalDistance;
    //FIN de estructuras para representar una solución al problema
    //INICIO de parametros configurables por el usuario
    int maxIter = 1000;
    int localSearchMaxIter = 500;
    //FIN de parametros configurables por el usuario
    VehicleRoutingProblem vrpInstance;
    int numberOfCustomers;
    private static final double mili = 1000000000.0;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        numberOfCustomers = vrpInstance.getNumberOfCustomers();
        customers = new Integer[numberOfCustomers + 1];
        currentRoutes = new ArrayList<List<Integer>>(numberOfCustomers);
        bestRoutes = new ArrayList<List<Integer>>(numberOfCustomers);
        for (int i = 0; i < numberOfCustomers; i++) {
            currentRoutes.add(new ArrayList<Integer>(numberOfCustomers));
        }
        for (int i = 0; i < numberOfCustomers + 1; i++) {
            customers[i] = new Integer(i);
        }
        costOfRoutes = new ArrayList<Integer>(numberOfCustomers);
        routeDemands = new ArrayList<Integer>(numberOfCustomers);
        this.vrpInstance = vrpInstance;
        this.totalCost = this.vrpInstance.getDropTime() * numberOfCustomers;
        this.totalDistance = 0;
        constructInitialSolution();
    }

    private void constructInitialSolution() {
        initializePartition();
        int n = vrpInstance.getNumberOfCustomers();
        int s[][] = new int[n][n];
        calculateSavings(s);
        Index index = getBestSaving(s);
        while (index.getSaving() >= 0) {
            mergeRoutes(index);
            index = getBestSaving(s);
        }
        updateBestRoutes();
        bestTotalCost = totalCost;
        bestTotalDistance = totalDistance;
        //printResult();
        boolean valid = validateResult();
        System.out.println("Es valida la solucion inicial: " + valid);
        System.out.println("Distancia de la solucion inicial: " + totalDistance);
    }

    private void mergeRoutes(Index index) {
        int i = getIndexOfRouteI(index.i);
        int j = getIndexOfRouteJ(index.j);
        if (i != -1 && j != -1) {
            int costIJ = costOfRoutes.get(i) + costOfRoutes.get(j)
                    + ((currentRoutes.get(i).size() + currentRoutes.get(j).size())
                    * vrpInstance.getDropTime()) - index.saving;
            int demandIJ = this.routeDemands.get(i) + this.routeDemands.get(j);
            if (costIJ < vrpInstance.getMaximumRouteTime()
                    && demandIJ <= vrpInstance.getVehicleCapacity() && i != j) {

                doMerge(i, j, index.saving);
            }
        }
    }

    @Override
    public ILSSolutionSet execute() {
        int iteration = 0;
        int bestIteration = 0;
        long tIni;
        long tFin;
        long tFinBest;
        boolean accepted;

        tIni = System.nanoTime();
        tFinBest = System.nanoTime();
        while (iteration < this.maxIter) {
            localSearch();
            accepted = acceptanceCriterion();
            if (accepted) {
                tFinBest = System.nanoTime();
                bestIteration = iteration;
            }
            perturbate();
            iteration += 1;
        }
        tFin = System.nanoTime();
        double tBest = (tFinBest - tIni) / mili;
        double tTotal = (tFin - tIni) / mili;
        String finalRoutes = routesToString();
        return (new ILSSolutionSet(this.totalCost, bestIteration, tBest, tTotal,
                bestRoutes.size(), iteration, finalRoutes, this.totalDistance));
    }

    private void updateBestRoutes() {
        bestRoutes.clear();
        for (int i = 0; i < currentRoutes.size(); i++) {
            bestRoutes.add(new ArrayList<Integer>(
                    currentRoutes.get(i).size()));
            for (int j = 0; j < currentRoutes.get(i).size(); j++) {
                bestRoutes.get(i).add(currentRoutes.get(i).get(j));
            }
        }
    }

    private void resetRoutes() {
        currentRoutes.clear();
        for (int i = 0; i < bestRoutes.size(); i++) {
            currentRoutes.add(new ArrayList<Integer>(bestRoutes.get(i).size()));
            for (int j = 0; j < bestRoutes.get(i).size(); j++) {
                currentRoutes.get(i).add(bestRoutes.get(i).get(j));
            }
        }
    }

    private void initializePartition() {
        int i = 0;
        int cost;
        for (List<Integer> route : this.currentRoutes) {
            route.add(this.customers[i + 1]);
            cost = vrpInstance.getCost(0, i + 1) * 2;
            this.costOfRoutes.add(i, new Integer(cost));
            this.routeDemands.add(new Integer(vrpInstance.getCustomerDemand(i + 1)));
            this.totalDistance += cost;
            this.totalCost += cost;
            i++;
        }
    }

    private void calculateSavings(int[][] s) {
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                int a = vrpInstance.getCost(i + 1, 0);
                int b = vrpInstance.getCost(0, j + 1);
                int c = vrpInstance.getCost(i + 1, j + 1);
                s[i][j] = (a + b) - c;
                //Esto sólo se hace por completitud ya que no se utilizará
                s[j][i] = (a + b) - c;
            }
        }
    }

    private Index getBestSaving(int[][] s) {
        int x = -1;
        int y = -1;
        int max = -1;
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

    private void doMerge(int i, int j, int saving) {
        this.currentRoutes.get(i).addAll(this.currentRoutes.get(j));
        int newCost = this.costOfRoutes.get(i) + this.costOfRoutes.get(j) - saving;
        this.costOfRoutes.set(i, newCost);
        int newDemand = this.routeDemands.get(i) + this.routeDemands.get(j);
        this.routeDemands.set(i, newDemand);
        this.currentRoutes.remove(j);
        this.routeDemands.remove(j);
        this.costOfRoutes.remove(j);
        this.totalCost = this.totalCost - saving;
        this.totalDistance = this.totalDistance - saving;
    }

    private void printResult() {
        int i = 0;
        for (List<Integer> route : bestRoutes) {
            System.out.println("0" + route.toString() + "0");
            System.out.println("Costo ruta: " + costOfRoutes.get(i));
            i++;
        }
        System.out.println("Costo total: " + totalCost);
    }

    //TODO Borrar metodo
    private boolean validateResult() {
        int n = 0;
        for (List<Integer> route : bestRoutes) {
            n += route.size();
        }
        if (n != numberOfCustomers) {
            return false;
        }
        int i = 0;
        for (Integer route : this.costOfRoutes) {
            if (route + (bestRoutes.get(i).size() * vrpInstance.getDropTime()) >= vrpInstance.getMaximumRouteTime()) {
                return false;
            }
            i++;
        }

        for (Integer route : this.routeDemands) {
            if (route > vrpInstance.getVehicleCapacity()) {
                return false;
            }
        }

        int ocurrences[] = new int[numberOfCustomers];
        for (int j = 0; j < numberOfCustomers; j++) {
            ocurrences[j] = 0;
        }

        for (List<Integer> route : bestRoutes) {
            for (Integer element : route) {
                ocurrences[element - 1] = ocurrences[element - 1] + 1;
            }
        }

        for (int j = 0; j < numberOfCustomers; j++) {
            if (ocurrences[j] != 1) {
                return false;
            }
        }
        return true;
    }

    private String routesToString() {
        String s = "";
        for (List<Integer> route : bestRoutes) {
            s = s + "0 ";
            for (Integer element : route) {
                s = s + element + " ";
            }
            s = s + "0\n\n";
        }
        return s;
    }

    private void localSearch() {
        int i = 0;
        while (i < localSearchMaxIter) {
            generateNeighbor();
            i += 1;
        }
    }

    private void generateNeighbor() {
        //Se generará un vecino a traves del metodo 2-Opt y si es mejor,
        //quedara como nueva solucion.
        int numberOfRoutes = currentRoutes.size();
        int routeIndex =
                Math.round((float) Math.random() * (numberOfRoutes - 1));
        List<Integer> route = currentRoutes.get(routeIndex);
        int routeSize = route.size();

        if (routeSize == 3) {
            //Hacer swap-city ya que no se puede hacer 2-Opt
            int random = Math.round((float) Math.random());
            int index0 = (random == 0) ? 0 : 2;
            int index1 = 1;
            if (index0 > index1) {
                int swap = index1;
                index1 = index0;
                index0 = swap;
            }
            //Si vale la pena, hacer el swap
            int deltaCost =
                    costVariation(index0, index1, routeSize, routeIndex);
            if (deltaCost < 0) {
                Integer old0 = route.get(index0);
                Integer old1 = route.get(index1);
                route.set(index0, old1);
                route.set(index1, old0);
                this.totalCost += deltaCost;
                this.totalDistance += deltaCost;
                this.costOfRoutes.set(routeIndex, totalCost);
            }
        } else if (routeSize >= 3) {
            //Hacer 2-Opt
            int index0 =
                    Math.round((float) Math.random() * (routeSize - 1));
            int delta = Math.round((float) Math.random() * (routeSize - 2)) + 1;
            int index1 = (index0 + delta) % routeSize;

            if (index0 > index1) {
                int swap = index1;
                index1 = index0;
                index0 = swap;
            }
            int deltaCost =
                    costVariation(index0, index1, routeSize, routeIndex);
            if (deltaCost < 0) {
                Integer old0 = route.get(index0);
                Integer old1 = route.get(index1);
                route.set(index0, old1);
                route.set(index1, old0);
                this.totalCost += deltaCost;
                this.totalDistance += deltaCost;
                this.costOfRoutes.set(routeIndex, totalCost);
            }
        }
    }

    private boolean acceptanceCriterion() {
        if (totalDistance < bestTotalDistance) {
            updateBestRoutes();
            bestTotalCost = totalCost;
            bestTotalDistance = totalDistance;
            return true;
        }
        return false;
    }

    private void perturbate() {
        //TODO implement this
    }

    private void updateCurrentSolution(List<List<Integer>> candidateSolution,
            int candidateSolutionDistance) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int costVariation(int customerIndex1, int customerIndex2,
            int routeSize, int routeIndex) {

        int variation = 0;
        List<Integer> route = currentRoutes.get(routeIndex);

        int customer0 = (customerIndex1 - 1) < 0
                ? 0 : route.get(customerIndex1 - 1);
        int customer1 = route.get(customerIndex1);
        int customer2 = route.get(customerIndex2);
        int customer3 = (customerIndex2 + 1) == routeSize
                ? 0 : route.get(customerIndex2 + 1);

        variation += vrpInstance.getCost(customer0, customer2);
        variation += vrpInstance.getCost(customer1, customer3);
        variation -= vrpInstance.getCost(customer0, customer1);
        variation -= vrpInstance.getCost(customer2, customer3);

        return variation;
    }

    private static class Index {

        int i;
        int j;
        int saving;

        public Index(int i, int j, int saving) {
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

        public int getSaving() {
            return saving;
        }
    }
}
