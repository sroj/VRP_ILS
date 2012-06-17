package vrp_ils;

import java.util.ArrayList;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm implements MetaheuristicOptimizationAlgorithm {

    ArrayList<ArrayList<Integer>> routes;
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        this.vrpInstance = vrpInstance;
    }

    private void constructInitialSolution() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SolutionSet execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
