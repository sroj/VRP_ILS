package VRP_ANT;

import VRP.SolutionSet;

/**
 *
 * @author Marlin Aranguren
 * @author Simon Rojas
 */
public class ANTSolutionSet extends SolutionSet {

    private double bestDistance;
    private int bestIteration;
    private int numberOfIterations;
    private double bestTime;
    private int numberOfRoutes;
    private String routes;

    /**
     * Constructor que inicializa cada uno de los elementos que una solucion
     * encontrada para el problema de VRP usando un sistema de hormigas, debe
     * tener.
     *
     * @param bestDistance Distancia de la mejor solucion
     * @param bestIteration Iteracion donde se encontro la mejor solucion
     * @param numberOfIterations Numero total de iteraciones realizadas por el
     * programa
     * @param bestTime Tiempo en que fue encontrada la mejor solucion, en
     * segundos
     * @param numberOfRoutes Numero de rutas distintas de las que se compone la
     * solucion encontrada
     * @param routes Representacion como
     * <code>String</code> de cada una de las rutas de las que se compone la
     * solucion
     * @param executionTime Tiempo total de ejecución del algoritmo, en segundos
     */
    public ANTSolutionSet(double bestDistance, int bestIteration,
            int numberOfIterations, double bestTime, int numberOfRoutes,
            String routes, double executionTime) {
        super(executionTime);
        this.bestDistance = bestDistance;
        this.bestIteration = bestIteration;
        this.numberOfIterations = numberOfIterations;
        this.bestTime = bestTime;
        this.numberOfRoutes = numberOfRoutes;
        this.routes = routes;
    }

    public double getBestDistance() {
        return bestDistance;
    }

    public int getBestIteration() {
        return bestIteration;
    }

    public double getBestTime() {
        return bestTime;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public int getNumberOfRoutes() {
        return numberOfRoutes;
    }

    public String getRoutes() {
        return routes;
    }
}
