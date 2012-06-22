package vrp_ils;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class VehicleRoutingProblem {

    private int numberOfCustomers;
    private int vehicleCapacity;
    private int maximumRouteTime;
    private int dropTime;
    private int[] customerDemands;
    private int[][] costs;

    /**
     * Constructor.
     */
    public VehicleRoutingProblem(int numberOfCustomers, int vehicleCapacity,
            int maximumRouteTime, int dropTime) {

        this.numberOfCustomers = numberOfCustomers;
        this.vehicleCapacity = vehicleCapacity;
        this.maximumRouteTime = maximumRouteTime;
        this.dropTime = dropTime;
        this.customerDemands = new int[numberOfCustomers+1];

        // El customer 0 es en realidad el deposito de camiones, que no tiene
        // demanda alguna de ningun producto.
        this.customerDemands[0] = 0;

        this.costs = new int[numberOfCustomers+1][numberOfCustomers+1];

        //Se inicializa en costo "infinito" el arreglo de costos, para
        //el hecho de que algunas rutas son invalidas.
        initializeCostsArray();
    }

    private void initializeCostsArray() {
        for (int i = 0; i <= this.numberOfCustomers; i++) {
            for (int j = 0; j <= this.numberOfCustomers; j++) {
                this.costs[i][j] = Integer.MAX_VALUE;
            }
        }
    }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public int getDropTime() {
        return dropTime;
    }

    public void setDropTime(int dropTime) {
        this.dropTime = dropTime;
    }

    public int getMaximumRouteTime() {
        return maximumRouteTime;
    }

    public void setMaximumRouteTime(int maximumRouteTime) {
        this.maximumRouteTime = maximumRouteTime;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public void setVehicleCapacity(int vehicleCapacity) {
        this.vehicleCapacity = vehicleCapacity;
    }

    public void addCost(int origin, int destiny, int cost) {
        this.costs[origin][destiny] = cost;
        this.costs[origin][destiny] = cost;
    }

    public int getCost(int origin, int destiny) {
        return this.costs[origin][destiny];
    }

    public void addCustomerDemand(int customerID, int demand) {
        this.customerDemands[customerID] = demand;
    }

    public int getCustomerDemand(int customerID) {
        return this.customerDemands[customerID];
    }
}
