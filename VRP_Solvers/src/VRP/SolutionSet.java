package VRP;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public abstract class SolutionSet {

    //DERIVAR ESTA CLASE CON LOS ATRIBUTOS PARTICULARES QUE RESULTEN
    //INTERESANTES PARA CADA ALGORITMO, Y SUS GETTERS Y SETTERS.
    private double executionTime;

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }
}
