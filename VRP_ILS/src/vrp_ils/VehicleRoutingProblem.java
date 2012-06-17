package vrp_ils;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class VehicleRoutingProblem {

    private Graph routesData;
    private int vehicleCapacity;
    private int dropTime;
    private int numberOfCustomers;
    private int maximumRouteTime;

    /**
     * Constructor vacio.
     */
    public VehicleRoutingProblem() {
        this.routesData = new Graph();
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

    public void addNode(int origin, int destiny, int cost) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
