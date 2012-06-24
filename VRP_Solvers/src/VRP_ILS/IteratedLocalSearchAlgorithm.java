package VRP_ILS;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.SolutionSet;
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
    List<List<Integer>> routes;
    List<Integer> costOfRoutes;
    List<Integer> routeDemands;
//    int[] customers;
//    int[] partitionIndexes;
//    int[] quantityBeforePartition;
//    int numberOfPartitionIndexes;
    int totalCost;
    private static final double mili = 1000000000;
    //FIN de estructuras para representar una solución al problema
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        int numberOfCustomers = vrpInstance.getNumberOfCustomers();
        routes = new ArrayList<List<Integer>>(numberOfCustomers);
        for (int i = 0; i < numberOfCustomers; i++) {
            routes.add(new ArrayList<Integer>(numberOfCustomers));
        }
        costOfRoutes = new ArrayList<Integer>(numberOfCustomers);
        routeDemands = new ArrayList<Integer>(numberOfCustomers);
        this.vrpInstance = vrpInstance;
        this.totalCost = this.vrpInstance.getDropTime() * numberOfCustomers;
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
        boolean valid = validateResult();
        printResult();
        System.out.println("Es valida: " + valid);
    }

    private void mergeRoutes(Index index) {
        int i = getIndexOfRouteI(index.i);
        int j = getIndexOfRouteJ(index.j);
        if (i != -1 && j != -1) {
            int costIJ = costOfRoutes.get(i) + costOfRoutes.get(j)
                    + ((routes.get(i).size() + routes.get(j).size())
                    * vrpInstance.getDropTime()) - index.saving;
            int demandIJ = this.routeDemands.get(i) + this.routeDemands.get(j);
            if (costIJ < vrpInstance.getMaximumRouteTime()
                    && demandIJ <= vrpInstance.getVehicleCapacity() && i != j) {

                doMerge(i, j, index.saving);
            }
        }
    }

    @Override
    public SolutionSet execute() {
        int iteration=0;
        int bestIteration=0;
        long tIni = System.nanoTime();
        //TODO Aca va el algoritmo que simon no quiere hacer
        
        long tFinBest = System.nanoTime();
        
        long tFin = System.nanoTime();
        double tBest = (tFinBest - tIni)/mili;
        double tTotal = (tFin - tIni)/mili;
        String finalRoutes = routesToString();
        return (new ILSSolutionSet(1, bestIteration,tBest,tTotal,routes.size(), 
                iteration, finalRoutes));
    }

    private void initializePartition() {
        int i = 0;
        int cost;
        for (List<Integer> route : this.routes) {
            route.add(new Integer(i + 1));
            cost = vrpInstance.getCost(0, i + 1) * 2;
            this.costOfRoutes.add(i, new Integer(cost));
            this.routeDemands.add(new Integer(vrpInstance.getCustomerDemand(i + 1)));
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
        for (List<Integer> route : routes) {
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
        for (List<Integer> route : routes) {
            if (route.indexOf(element) == (route.size() - 1)) {
                return (i);
            }
            i++;
        }
        return (-1);
    }

    private void doMerge(int i, int j, int saving) {
        this.routes.get(i).addAll(this.routes.get(j));
        int newCost = this.costOfRoutes.get(i) + this.costOfRoutes.get(j) - saving;
        this.costOfRoutes.set(i, newCost);
        int newDemand = this.routeDemands.get(i) + this.routeDemands.get(j);
        this.routeDemands.set(i, newDemand);
        this.routes.remove(j);
        this.routeDemands.remove(j);
        this.costOfRoutes.remove(j);
        this.totalCost = this.totalCost - saving;
    }

    private void printResult() {
        int i = 0;
        for (List<Integer> route : routes) {
            System.out.println("0" + route.toString() + "0");
            System.out.println("Costo ruta: " + costOfRoutes.get(i));
            i++;
        }
        System.out.println("Costo total: " + totalCost);
    }
//TODO Borrar metodo

    private boolean validateResult() {
        int n = 0;
        for (List<Integer> route : routes) {
            n += route.size();
        }
        if (n != vrpInstance.getNumberOfCustomers()) {
            return (false);
        }
        int i = 0;
        for (Integer route : this.costOfRoutes) {
            if (route + (routes.get(i).size() * vrpInstance.getDropTime()) >= vrpInstance.getMaximumRouteTime()) {
                return (false);
            }
            i++;
        }

        for (Integer route : this.routeDemands) {
            if (route > vrpInstance.getVehicleCapacity()) {
                return (false);
            }
        }

        int ocurrences[] = new int[vrpInstance.getNumberOfCustomers()];
        for (int j = 0; j < vrpInstance.getNumberOfCustomers(); j++) {
            ocurrences[j] = 0;
        }

        for (List<Integer> route : routes) {
            for (Integer element : route) {
                ocurrences[element - 1] = ocurrences[element - 1] + 1;
            }
        }

        for (int j = 0; j < vrpInstance.getNumberOfCustomers(); j++) {
            if (ocurrences[j] != 1) {
                return (false);
            }
        }
        return (true);
    }

    private String routesToString() {
        String s= "";
        for (List<Integer> route : routes) {
            s = s+ "0 ";
            for (Integer element : route) {
                s = s + element + " " ; 
            }
            s = s + "0\n\n";           
        }
        return s;
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
