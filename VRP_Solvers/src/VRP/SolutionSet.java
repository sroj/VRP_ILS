package VRP;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public abstract class SolutionSet {

    //DERIVAR ESTA CLASE CON LOS ATRIBUTOS PARTICULARES QUE RESULTEN
    //INTERESANTES PARA CADA ALGORITMO, Y SUS GETTERS Y SETTERS.
    // ПОЛУЧИТЬ ЭТОТ КЛАСС С КОНКРЕТНЫМИ АТРИБУТАМИ, КОТОРЫЕ ВОЗНИКАЮТ
    // ИНТЕРЕСНЫЕ ДЛЯ КАЖДОГО АЛГОРИТМА, А ТАКЖЕ ИХ ГЕТТЕРЫ И СЕТТЕРЫ.
    protected double executionTime;

    protected SolutionSet(double executionTime) {
        this.executionTime = executionTime;
    }

    public double getExecutionTime() {
        return executionTime;
    }
}
