/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VRP_ILS;

import VRP.SolutionSet;

/**
 *
 * @author administrador
 */
public class ILSSolutionSet extends SolutionSet {

    private int bestDistance;
    private int bestIteration;
    private int numberOfIterations;
    private double bestTime;
    private int numberOfRoutes;
    private int totaldistance;
    private String routes;

    public ILSSolutionSet(int bestDistance, int bestIteration, double bestTime,
           double totalTime, int numRoutes, int numberOfIt, String routes, int totalDistance) {

        super(totalTime);
        this.bestDistance = bestDistance;
        this.bestIteration = bestIteration;
        this.bestTime = bestTime;
        this.numberOfRoutes = numRoutes;
        this.numberOfIterations = numberOfIt;
        this.routes = routes;
        this.totaldistance=totalDistance;
    }

    public int getBestDistance() {
        return bestDistance;
    }

    public void setBestDistance(int bestDistance) {
        this.bestDistance = bestDistance;
    }

    public int getBestIteration() {
        return bestIteration;
    }

    public void setBestIteration(int bestIteration) {
        this.bestIteration = bestIteration;
    }

    public double getBestTime() {
        return bestTime;
    }

    public void setBestTime(double bestTime) {
        this.bestTime = bestTime;
    }

    public int getNumberOfIterations() {
        return numberOfIterations;
    }

    public void setNumberOfIterations(int numberOfIterations) {
        this.numberOfIterations = numberOfIterations;
    }

    public int getNumberOfRoutes() {
        return numberOfRoutes;
    }

    public void setNumberOfRoutes(int numberOfRoutes) {
        this.numberOfRoutes = numberOfRoutes;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public int getTotaldistance() {
        return totaldistance;
    }

    public void setTotaldistance(int totaldistance) {
        this.totaldistance = totaldistance;
    }

    
}
