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
    private int customerNumber;
    private int maximumRouteTime;

    public int getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
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

    public VehicleRoutingProblem() {
        this.routesData = new Graph();
    }
}
