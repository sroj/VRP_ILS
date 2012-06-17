package vrp_ils;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class VehicleRoutingProblem {

    private int vehicleCapacity;
    private int dropTime;
    private int numberOfCustomers;
    private int maximumRouteTime;
    private int[] customerDemands;
    private int[][] costs;

    /**
     * Constructor vacio.
     */
    public VehicleRoutingProblem(int numberOfCustomers, int vehicleCapacity,
            int maximumRouteTime, int dropTime) {
        this.numberOfCustomers = numberOfCustomers;
        this.vehicleCapacity = vehicleCapacity;
        this.maximumRouteTime = maximumRouteTime;
        this.dropTime = dropTime;
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

    public void addArc(int origin, int destiny, int cost) throws Exception {
        if (origin < 0 || origin > this.numberOfCustomers
                || destiny < 0 || destiny > this.numberOfCustomers) {
            throw new Exception("El nodo de origen y/o destino es invalido.");
        }
        this.costs[origin][destiny] = cost;
    }

    public void addCustomerDemand(int customerID, int demand) throws Exception {

        if (customerID < 0 || customerID > this.numberOfCustomers) {
            throw new Exception("El customerID es invalido");
        }
        this.customerDemands[customerID] = demand;
    }
}
