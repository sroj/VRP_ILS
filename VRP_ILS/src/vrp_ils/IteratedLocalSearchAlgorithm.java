package vrp_ils;

import java.util.ArrayList;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm
        implements MetaheuristicOptimizationAlgorithm {

    ArrayList<ArrayList<Integer>> routes;
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
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
