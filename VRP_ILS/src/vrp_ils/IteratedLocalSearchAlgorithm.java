package vrp_ils;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm
        implements MetaheuristicOptimizationAlgorithm {

    //INICIO de estructuras para representar una solucion al problema
    int[] customers;
    int[] partitionIndexes;
    //FIN de estructuras para representar una solución al problema
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        this.customers = new int[vrpInstance.getNumberOfCustomers()];
        this.partitionIndexes = new int[vrpInstance.getNumberOfCustomers()];
        this.vrpInstance = vrpInstance;
        constructInitialSolution();
    }

    private void constructInitialSolution() {

        //TODO aqui va el codigo para implementar el algoritmo de construccion
        //de una solucion inicial
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SolutionSet execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
