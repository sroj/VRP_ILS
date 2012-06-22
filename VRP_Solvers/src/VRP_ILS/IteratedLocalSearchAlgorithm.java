package VRP_ILS;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.SolutionSet;
import VRP.VehicleRoutingProblem;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm
        implements MetaheuristicOptimizationAlgorithm {

    //INICIO de estructuras para representar una solucion al problema
    int[] customers;
    int[] partitionIndexes;
    int numberOfPartitionIndexes;
    //FIN de estructuras para representar una solución al problema
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        this.customers = new int[vrpInstance.getNumberOfCustomers()];
        this.partitionIndexes = new int[vrpInstance.getNumberOfCustomers() - 1];
        this.vrpInstance = vrpInstance;
        //constructInitialSolution();
    }

    private void constructInitialSolution() {

        //TODO aqui va el codigo para implementar el algoritmo de construccion
        //de una solucion inicial
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SolutionSet execute() {
        //TODO aqui coloqué un return chimbo, para probar la escritura enel archivo
        return (new ILSSolutionSet(numberOfPartitionIndexes, numberOfPartitionIndexes,
                numberOfPartitionIndexes, numberOfPartitionIndexes,
                numberOfPartitionIndexes, "hola"));
    }
}
