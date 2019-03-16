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
    // Начало структур для представления решения проблемы
    Integer[] customers;
    List<List<Integer>> currentRoutes;
    List<Double> costOfRoutes;
    List<Integer> routeDemands;
    double totalCost;
    double totalDistance;
    List<List<Integer>> bestRoutes;
    double bestTotalCost;
    double bestTotalDistance;
    //FIN de estructuras para representar una solución al problema
    //INICIO de parametros configurables por el usuario
    //Конец структур для представления решения проблемы
    // Начало настраиваемых пользователем параметров
    int maxIter = 10000;
    int localSearchMaxIter = 100000;
    //FIN de parametros configurables por el usuario
    // Конец настраиваемых пользователем параметров
    VehicleRoutingProblem vrpInstance;
    int numberOfCustomers;
    private static final double mili = 1000000000.0;

    private double calculateRouteDistance(List<List<Integer>> routes) {
        double distance = 0;
        for (int j = 0; j < routes.size(); j++) {
            List<Integer> route = routes.get(j);
            distance += vrpInstance.getCost(0, route.get(0));
            for (int i = 1; i < route.size(); i++) {
                distance += vrpInstance.getCost(route.get(i - 1), route.get(i));
            }
            distance += vrpInstance.getCost(route.get(route.size() - 1), 0);
        }
        return distance;
    }

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
        costOfRoutes = new ArrayList<Double>(numberOfCustomers);
        routeDemands = new ArrayList<Integer>(numberOfCustomers);
        this.vrpInstance = vrpInstance;
        this.totalCost = this.vrpInstance.getDropTime() * numberOfCustomers;
        this.totalDistance = 0;
        constructInitialSolution();
        System.out.println("Начальное расстояние: " + bestTotalDistance);
        // Distancia inicial:
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
        bestTotalCost = totalCost;
        bestTotalDistance = totalDistance;
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
            boolean p = perturbate();
            while (!p) {
                p = perturbate();
            }
            iteration += 1;
        }
        tFin = System.nanoTime();
        double tBest = (tFinBest - tIni) / mili;
        double tTotal = (tFin - tIni) / mili;
        String finalRoutes = routesToString();
        double bestDistance = calculateRouteDistance(bestRoutes);
        this.bestTotalCost = bestDistance
                + vrpInstance.getNumberOfCustomers() * vrpInstance.getDropTime();
        return (new ILSSolutionSet(bestDistance,
                bestIteration, tBest, tTotal, bestRoutes.size(), iteration,
                finalRoutes, this.bestTotalCost));
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

    private void printResult() {
        int i = 0;
        for (List<Integer> route : bestRoutes) {
            System.out.println("0" + route.toString() + "0");
            System.out.println("Стоимость маршрута: " + costOfRoutes.get(i));
            // Costo ruta
            i++;
        }
        System.out.println("Общая стоимость: " + this.totalDistance);
        // Costo total
    }

    private boolean validateResult() {
        int n = 0;
        for (List<Integer> route : bestRoutes) {
            n += route.size();
        }
        if (n != numberOfCustomers) {
            System.out.println("неправильное количество клиентов");
            // Invalida por numero de customers
            return false;
        }

        int i;
        double costoRuta;
        i = 0;
        int demandaRuta;
        for (List<Integer> route : currentRoutes) {
            costoRuta = vrpInstance.getCost(0, route.get(0));
            demandaRuta = 0;
            for (int j = 0; j < route.size() - 1; j++) {
                costoRuta += vrpInstance.getCost(route.get(j), route.get(j + 1));
                demandaRuta += vrpInstance.getCustomerDemand(route.get(j));
            }
            demandaRuta += vrpInstance.getCustomerDemand(route.get(route.size() - 1));
            costoRuta += vrpInstance.getCost(route.get(route.size() - 1), 0);
            if (demandaRuta > vrpInstance.getVehicleCapacity()
                    || costoRuta >= vrpInstance.getMaximumRouteTime()) {
                System.out.println("Неправильный спрос или максимальное время маршрута");
                //Invalida por demanda o por maximum route time
                return false;
            }
            i++;
        }

        int ocurrences[] = new int[numberOfCustomers];
        for (int j = 0; j < numberOfCustomers; j++) {
            ocurrences[j] = 0;
        }

        for (List<Integer> route : bestRoutes) {
            for (Integer element : route) {
                ocurrences[element - 1] += 1;
            }
        }

        for (int j = 0; j < numberOfCustomers; j++) {
            if (ocurrences[j] != 1) {
                System.out.println("Неправильное повторение клиента");
                // Invalida por repeticion de customer
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
        // Сосед будет сгенерирован через метод 2-Opt, и если лучше,
        // останется новым решением.
        int numberOfRoutes = currentRoutes.size();
        int routeIndex =
                Math.round((float) Math.random() * (numberOfRoutes - 1));
        List<Integer> route = currentRoutes.get(routeIndex);
        int routeSize = route.size();

        if (routeSize >= 3) {
            //Hacer 2-Opt
            // Делаем 2-Opt
            int index0 =
                    Math.round((float) Math.random() * (routeSize - 1));
            int delta = Math.round((float) Math.random() * (routeSize - 2)) + 1;
            int index1 = (index0 + delta) % routeSize;

            if (index0 > index1) {
                int swap = index1;
                index1 = index0;
                index0 = swap;
            }

            Integer old0 = route.get(index0);
            Integer old1 = route.get(index1);
            double oldRouteDistance = calculateRouteCost(route);

            route.set(index0, old1);
            route.set(index1, old0);

            double calculatedRouteDistance = calculateRouteCost(route);
            if (calculatedRouteDistance - oldRouteDistance < 0) {
                this.costOfRoutes.set(routeIndex, calculatedRouteDistance);
                setFinalCost();
            } else {
                //Revertir el swap
                route.set(index0, old0);
                route.set(index1, old1);
            }
        }
    }

    private double calculateRouteCost(List<Integer> route) {
        double routeDistance = 0;
        routeDistance += vrpInstance.getCost(0, route.get(0));
        for (int i = 1; i < route.size(); i++) {
            routeDistance += vrpInstance.getCost(route.get(i - 1), route.get(i));
        }
        routeDistance += vrpInstance.getCost(route.get(route.size() - 1), 0);
        return routeDistance;
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

    private int calculateDemand(int index) {
        int dem = 0;
        for (Integer elem : currentRoutes.get(index)) {
            dem += vrpInstance.getCustomerDemand(elem);
        }
        return dem;
    }

    private boolean perturbate() {
        int numberOfRoutes = currentRoutes.size();
        int routeIndex1 = Math.round((float) Math.random() * (numberOfRoutes - 1));
        int delta = Math.round((float) Math.random() * (numberOfRoutes - 2)) + 1;
        int routeIndex2 = (routeIndex1 + delta) % numberOfRoutes;
        if (routeIndex1 != routeIndex2) {
            resetRoutes();
            List<Integer> route1 = currentRoutes.get(routeIndex1);
            List<Integer> route2 = currentRoutes.get(routeIndex2);
            int routeSize1 = route1.size();
            int routeSize2 = route2.size();
            int index1 = Math.round((float) Math.random() * (routeSize1 - 1));
            int index2 = Math.round((float) Math.random() * (routeSize2 - 1));
            Integer old1 = route1.get(index1);
            Integer old2 = route2.get(index2);
            route1.set(index1, old2);
            route2.set(index2, old1);
            double newCost1 = getRouteCost(routeIndex1);
            double newCost2 = getRouteCost(routeIndex2);
            int newDem1 = calculateDemand(routeIndex1);
            int newDem2 = calculateDemand(routeIndex2);
            if (newCost1 + (route1.size() * vrpInstance.getDropTime())
                    < vrpInstance.getMaximumRouteTime() && newDem1
                    <= vrpInstance.getVehicleCapacity() && newCost2
                    + (route2.size() * vrpInstance.getDropTime())
                    < vrpInstance.getMaximumRouteTime() && newDem2
                    <= vrpInstance.getVehicleCapacity()) {
                double newDistance = calculateRouteDistance(currentRoutes);
                this.costOfRoutes.set(routeIndex1, newCost1);
                this.costOfRoutes.set(routeIndex2, newCost2);
                this.routeDemands.set(routeIndex1, newDem1);
                this.routeDemands.set(routeIndex2, newDem2);
                this.totalDistance = newDistance;
                this.totalCost = newDistance
                        + (vrpInstance.getNumberOfCustomers()
                        * vrpInstance.getDropTime());
                return true;
            } else {
                route1.set(index1, old1);
                route2.set(index2, old2);
            }
        }
        return false;
    }

    private double costVariation(int customerIndex1, int customerIndex2,
            int routeSize, int routeIndex) {

        double variation = 0;
        List<Integer> route = currentRoutes.get(routeIndex);

        int customer0 = (customerIndex1 - 1) < 0
                ? 0 : route.get(customerIndex1 - 1);
        int customer1 = route.get(customerIndex1);
        int customer2 = route.get(customerIndex2);
        int customer3 = (customerIndex2 + 1) == routeSize
                ? 0 : route.get(customerIndex2 + 1);

        variation -= vrpInstance.getCost(customer0, customer2);
        variation -= vrpInstance.getCost(customer1, customer3);
        variation += vrpInstance.getCost(customer0, customer1);
        variation += vrpInstance.getCost(customer2, customer3);
        return variation;
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
