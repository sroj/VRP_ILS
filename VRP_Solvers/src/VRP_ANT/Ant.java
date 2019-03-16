package VRP_ANT;

import VRP.VehicleRoutingProblem;
import java.util.ArrayList;

/**
 * La clase Ant representa a una hormiga individual.
 * Класс Ant представляет отдельного муравья.
 * @author Marlin Aranguren
 * @author Simon Rojas
 */
public class Ant implements Comparable<Ant> {

    private final int numberOfCustomers;
    //Variable compartida
    // Общие переменные
    private double[][] pheromones;
    private double totalDistance;
    private double totalRouteTime;
    private double dropTime;
    //INICIO DE PARAMETROS DE AJUSTE
    //НАЧАЛО НАСТРОЙКИ ПАРАМЕТРОВ
    private int alfa;
    private int beta;
    private double f;
    private double g;
    //FIN DE PARAMETROS DE AJUSTE
    //КОНЕЦ НАСТРОЙКИ ПАРАМЕТРОВ
    private ArrayList<ArrayList<Integer>> solution;
    //No incluye el depot
    //Не включает depot
    private ArrayList<Integer> notVisited;
    private ArrayList<Integer> feasibleCustomers;
    private ArrayList<Arc> usedArcs;
    VehicleRoutingProblem vrpInstance;
    private final int localSearchMaxIter;

    public Ant(double[][] pheromones, VehicleRoutingProblem vrpInstance,
            int alfa, int beta, double f, double g, int localSearchMaxIter) {
        this.alfa = alfa;
        this.beta = beta;
        this.f = f;
        this.g = g;
        this.vrpInstance = vrpInstance;
        numberOfCustomers = vrpInstance.getNumberOfCustomers();
        dropTime = vrpInstance.getDropTime();
        this.pheromones = pheromones;
        //CUIDADO: Solo sirve en Java 7 (ya que se usa 'diamond inference')!
        //Осторожно: он работает только в Java 7 (поскольку используется "diamond inference")!
        solution =
                new ArrayList<>(numberOfCustomers + 1);
        notVisited = new ArrayList<>(numberOfCustomers + 1);
        for (int i = 1; i <= numberOfCustomers; i++) {
            notVisited.add(i);
        }
        feasibleCustomers =
                new ArrayList<>(numberOfCustomers + 1);
        usedArcs = new ArrayList<>(numberOfCustomers * numberOfCustomers);
        this.localSearchMaxIter = localSearchMaxIter;
    }

    /**
     * Prepara a esta hormiga para iniciar una nueva exploracion
     * Подготовьте этого муравья, чтобы начать новое исследование
     * @param initialCustomer nuevo cliente inicial desde donde se iniciara la
     * busqueda
     * новый начальный клиент с момента запуска поиска
     *
     */
    public void reset() {
        totalRouteTime = 0;
        totalDistance = 0;
        notVisited.clear();
        for (int i = 1; i <= numberOfCustomers; i++) {
            notVisited.add(i);
        }
        solution.clear();
        usedArcs.clear();
    }

    private int nextCustomer(int currentCustomer, double currentRouteTime,
            double currentDemand) throws Exception {
        double dice = Math.random();
        double sum = 0;
        double lower;
        double upper = 0;
        feasibleCustomers.clear();
        for (Integer customer : notVisited) {
            if (isFeasible(currentCustomer, customer, currentRouteTime,
                    currentDemand)) {
                feasibleCustomers.add(customer);
            }
        }
        for (Integer customer : feasibleCustomers) {
            double pheromone = getPheromone(currentCustomer, customer);
            double visibility = getVisibility(currentCustomer, customer);
            if (visibility < 0 || pheromone < 0) {
                throw new Exception("Probabilidad negativa");
            }
            sum += Math.pow(pheromone, alfa)
                    * Math.pow(visibility, beta);
        }
        //Sumar el depot
        if (currentCustomer > 0) {
            sum += Math.pow(getPheromone(currentCustomer, 0), alfa)
                    * Math.pow(getVisibility(currentCustomer, 0), beta);
        }
        for (int i = 0; i <= feasibleCustomers.size(); i++) {
            if (i < feasibleCustomers.size()) {
                int customer = feasibleCustomers.get(i);
                lower = upper;
                upper += (Math.pow(getPheromone(currentCustomer, customer), alfa)
                        * Math.pow(getVisibility(currentCustomer, customer), beta))
                        / sum;
                if (lower <= dice && dice < upper) {
                    notVisited.remove(new Integer(customer));
                    return customer;
                }
            } else {
                if (currentCustomer > 0) {
                    lower = upper;
                    upper += (Math.pow(getPheromone(currentCustomer, 0), alfa)
                            * Math.pow(getVisibility(currentCustomer, 0), beta))
                            / sum;
                    if (lower <= dice && dice < upper) {
                        return 0;
                    }
                }
            }
        }
        throw new Exception("Не найден новый клиент для путешествий");
        //No se encontró un nuevo cliente al que viajar
    }

    private double getVisibility(int customer0, int customer1) {
        if (customer1 == 0) {
            return vrpInstance.getCost(customer0, 0)
                    - g * vrpInstance.getCost(customer0, customer1)
                    + f * Math.abs(vrpInstance.getCost(customer0, 0));
        }
        if (customer0 == 0) {
            return vrpInstance.getCost(0, customer1)
                    - g * vrpInstance.getCost(customer0, customer1)
                    + f * Math.abs(vrpInstance.getCost(0, customer1));
        }
        return vrpInstance.getCost(customer0, 0)
                + vrpInstance.getCost(0, customer1)
                - g * vrpInstance.getCost(customer0, customer1)
                + f * Math.abs(vrpInstance.getCost(customer0, 0)
                - vrpInstance.getCost(0, customer1));
    }

    private double getPheromone(int customer0, int customer1) {
        return pheromones[customer0][customer1];
    }

    void explore(int initialCustomer) throws Exception {
        reset();
        totalDistance = vrpInstance.getCost(0, initialCustomer);
        int routeDemand = vrpInstance.getCustomerDemand(initialCustomer);
        totalRouteTime = totalDistance + dropTime;
        double routeTime = totalRouteTime;
        notVisited.remove(new Integer(initialCustomer));
        solution.add(new ArrayList<Integer>(numberOfCustomers + 1));
        solution.get(0).add(initialCustomer);
        usedArcs.add(new Arc(0, initialCustomer));
        int currentCustomer = initialCustomer;
        int nextCustomer;
        int j = 0;
        while (!notVisited.isEmpty()) {
            nextCustomer = nextCustomer(currentCustomer, routeTime,
                    routeDemand);
            if (nextCustomer > 0) {
                solution.get(j).add(nextCustomer);
                routeDemand += vrpInstance.getCustomerDemand(nextCustomer);
                routeTime += vrpInstance.getCost(currentCustomer, nextCustomer)
                        + dropTime;
            } else {
                solution.add(
                        new ArrayList<Integer>(numberOfCustomers + 1));
                routeDemand = 0;
                routeTime = 0;
                j++;
            }
            usedArcs.add(new Arc(currentCustomer, nextCustomer));
            totalDistance += vrpInstance.getCost(currentCustomer, nextCustomer);
            totalRouteTime += vrpInstance.getCost(currentCustomer, nextCustomer)
                    + nextCustomer > 0 ? dropTime : 0;
            currentCustomer = nextCustomer;
        }
        totalDistance += vrpInstance.getCost(currentCustomer, 0);
        totalRouteTime += vrpInstance.getCost(currentCustomer, 0);
        localSearch();
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

    private boolean isFeasible(int currentCustomer, int candidateCustomer,
            double currentRouteTime, double currentDemand) {
        return ((currentRouteTime
                + vrpInstance.getCost(currentCustomer, candidateCustomer)
                + dropTime + vrpInstance.getCost(candidateCustomer, 0))
                <= vrpInstance.getMaximumRouteTime())
                && ((currentDemand
                + vrpInstance.getCustomerDemand(candidateCustomer))
                <= vrpInstance.getVehicleCapacity());
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getTotalRouteTime() {
        return totalRouteTime;
    }

    public void layDownPheromones(int rank, int elitistAnts) {
        int initialNode;
        int endNode;
        for (Arc arc : usedArcs) {
            initialNode = arc.getInitialNode();
            endNode = arc.getEndNode();
            pheromones[initialNode][endNode] +=
                    (elitistAnts - rank) / totalDistance;
        }
    }

    public ArrayList<ArrayList<Integer>> getSolution() {
        return solution;
    }

    @Override
    public String toString() {
        return "Дистанция: " + totalDistance;
    }
    //Distancia:

    private static class Arc {

        private int initialNode;
        private int endNode;

        public Arc(int initialNode, int endNode) {
            this.initialNode = initialNode;
            this.endNode = endNode;
        }

        public int getEndNode() {
            return endNode;
        }

        public int getInitialNode() {
            return initialNode;
        }
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
        //Сосед будет сгенерирован через метод 2-Opt, и если лучше,
        // останется новым решением.
        int numberOfRoutes = solution.size();
        int routeIndex =
                Math.round((float) Math.random() * (numberOfRoutes - 1));
        ArrayList<Integer> route = solution.get(routeIndex);
        int routeSize = route.size();

        if (routeSize >= 3) {
            //Hacer 2-Opt
            //Сделать 2-Opt
            int index0 =
                    Math.round((float) Math.random() * (routeSize - 1));
            int delta = Math.round((float) Math.random() * (routeSize - 2)) + 1;
            int index1 = (index0 + delta) % routeSize;

            if (index0 > index1) {
                int swap = index1;
                index1 = index0;
                index0 = swap;
            }

            int old0 = route.get(index0);
            int old1 = route.get(index1);
            double oldRouteDistance = calculateRouteCost(route);

            route.set(index0, old1);
            route.set(index1, old0);

            double calculatedRouteDistance = calculateRouteCost(route);
            if (calculatedRouteDistance - oldRouteDistance < 0) {
                totalDistance += calculatedRouteDistance - oldRouteDistance;
            } else {
                //Revertir el swap
                route.set(index0, old0);
                route.set(index1, old1);
            }
        }
    }

    private double calculateRouteCost(ArrayList<Integer> route) {
        double routeDistance = 0;
        routeDistance += vrpInstance.getCost(0, route.get(0));
        for (int i = 1; i < route.size(); i++) {
            routeDistance += vrpInstance.getCost(route.get(i - 1), route.get(i));
        }
        routeDistance += vrpInstance.getCost(route.get(route.size() - 1), 0);
        return routeDistance;
    }
}
