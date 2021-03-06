package VRP;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public abstract class SolutionSet {

    //DERIVAR ESTA CLASE CON LOS ATRIBUTOS PARTICULARES QUE RESULTEN
    //INTERESANTES PARA CADA ALGORITMO, Y SUS GETTERS Y SETTERS.
    protected double executionTime;

    protected SolutionSet(double executionTime) {
        this.executionTime = executionTime;
    }

    public double getExecutionTime() {
        return executionTime;
    }
}
