package VRP;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class VehicleRoutingProblem {

    private int numberOfCustomers;
    private int vehicleCapacity;
    private int maximumRouteTime;
    private int dropTime;
    private int[] customerDemands;
    private double[][] costs;

    /**
     * Constructor que inicializa todos los parametros que definen completamente
     * una instancia particular del VRP.
     *
     * @param numberOfCustomers Numero de clientes de la instancia
     * @param vehicleCapacity Capacidad de los vehiculos (identica para todos)
     * @param maximumRouteTime Restriccion: tiempo máximo que un vehiculo puede
     * usar para atender una ruta
     * @param dropTime Restriccion: tiempo que tarda cada vehiculo en descargar
     * sus productos en la ubicacion que corresponde a cada cliente (igual para
     * todos los clientes)
     */
    public VehicleRoutingProblem(int numberOfCustomers, int vehicleCapacity,
            int maximumRouteTime, int dropTime) {

        this.numberOfCustomers = numberOfCustomers;
        this.vehicleCapacity = vehicleCapacity;
        this.maximumRouteTime = maximumRouteTime;
        this.dropTime = dropTime;
        this.customerDemands = new int[numberOfCustomers + 1];

        // El customer 0 es en realidad el deposito de camiones, que no tiene
        // demanda alguna de ningun producto.
        this.customerDemands[0] = 0;

        this.costs = new double[numberOfCustomers + 1][numberOfCustomers + 1];

        //Se inicializa en costo "infinito" el arreglo de costos, para simular
        //el hecho de que algunas rutas son invalidas.
        initializeCostsArray();
    }

    private void initializeCostsArray() {
        for (int i = 0; i <= this.numberOfCustomers; i++) {
            for (int j = 0; j <= this.numberOfCustomers; j++) {
                this.costs[i][j] = Double.MAX_VALUE;
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

    public void addCost(int origin, int destiny, double cost) {
        this.costs[origin][destiny] = cost;
    }

    public double getCost(int origin, int destiny) {
        return this.costs[origin][destiny];
    }

    public void addCustomerDemand(int customerID, int demand) {
        this.customerDemands[customerID] = demand;
    }

    public int getCustomerDemand(int customerID) {
        return this.customerDemands[customerID];
    }
}
