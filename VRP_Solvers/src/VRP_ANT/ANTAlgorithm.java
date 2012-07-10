package VRP_ANT;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.VehicleRoutingProblem;
import java.util.Arrays;

/**
 *
 * @author Marlin Aranguren
 * @author Simon Rojas
 */
public class ANTAlgorithm implements MetaheuristicOptimizationAlgorithm {

    VehicleRoutingProblem vrpInstance;
    int[][] candidateLists;

    public ANTAlgorithm(VehicleRoutingProblem vrpInstance) {
        this.vrpInstance = vrpInstance;
        this.candidateLists = new int[vrpInstance.getNumberOfCustomers() + 1][];
        initializeCandidateLists(15);
    }

    private void initializeCandidateLists(int size) {

        /**
         * El unico proposito de la clase Customer es poder calcular comodamente
         * las listas de clientes candidatos, y no se usa en otros metodos, por
         * eso se define localmente
         */
        class Customer implements Comparable<Customer> {

            private int customerNumber;
            private double distance;

            public Customer(int customerNumber, double distance) {
                this.customerNumber = customerNumber;
                this.distance = distance;
            }

            public int getCustomerNumber() {
                return customerNumber;
            }

            @Override
            public int compareTo(Customer t) {
                if (this.distance > t.distance) {
                    return 1;
                } else if (this.distance < t.distance) {
                    return -1;
                } else {
                    return 0;
                }
            }

            @Override
            public String toString() {
                return "Customer: " + customerNumber + " Distancia: "
                        + distance;
            }
        }

        for (int k = 0; k < this.candidateLists.length; k++) {
            int customers = this.vrpInstance.getNumberOfCustomers() + 1;
            this.candidateLists[k] = new int[size >= customers
                    ? customers : size];
            Customer[] sortedByDistance = new Customer[customers];
            for (int i = 0; i < sortedByDistance.length; i++) {
                sortedByDistance[i] =
                        new Customer(i, this.vrpInstance.getCost(k, i));
            }
            Arrays.sort(sortedByDistance);
            for (int i = 0; i < candidateLists[k].length; i++) {
                this.candidateLists[k][i] =
                        sortedByDistance[i].getCustomerNumber();
            }
        }
    }

    @Override
    public ANTSolutionSet execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
