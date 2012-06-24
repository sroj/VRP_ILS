package VRP_ILS;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.SolutionSet;
import VRP.VehicleRoutingProblem;
import java.io.*;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class Main {

    private static VehicleRoutingProblem vrp;
    private static MetaheuristicOptimizationAlgorithm algorithm;

    private static void LoadData(String path) throws Exception {
        BufferedReader file = new BufferedReader(new FileReader(path));
        String separator[];
        String line = file.readLine();
        separator = line.split(" ");
        int i = 0;
        if (separator[i].equals("")) {
            i++;
        }
        int n = Integer.parseInt(separator[i]);
        int capacity = Integer.parseInt(separator[i + 1]);
        int maxRouteTime = Integer.parseInt(separator[i + 2]);
        int dropTime = Integer.parseInt(separator[i + 3]);
        vrp = new VehicleRoutingProblem(n, capacity, maxRouteTime, dropTime);
        int coord[][] = new int[vrp.getNumberOfCustomers() + 1][2];
        line = file.readLine();
        separator = line.split(" ");
        i = 0;
        if (separator[i].equals("")) {
            i++;
        }
        coord[0][0] = Integer.parseInt(separator[i]);
        coord[0][1] = Integer.parseInt(separator[i + 1]);
        vrp.addCustomerDemand(0, 0);
        for (int j = 1; j <= vrp.getNumberOfCustomers(); j++) {
            line = file.readLine();
            separator = line.split(" ");
            i = 0;
            if (separator[i].equals("")) {
                i++;
            }
            coord[j][0] = Integer.parseInt(separator[i]);
            coord[j][1] = Integer.parseInt(separator[i + 1]);
            vrp.addCustomerDemand(j, Integer.parseInt(separator[i + 2]));
        }
        fillMatrix(coord);
    }

    private static void writeFile(SolutionSet solution, String instanceName)
            throws IOException {
        String outFileName = ("stat.").concat(instanceName);
        BufferedWriter out = new BufferedWriter(new FileWriter(outFileName));
        out.write("aString");
        out.close();
    }

    private static void fillMatrix(int[][] coord) throws Exception {
        int xd;
        int yd;
        for (int i = 0; i < coord.length; i++) {
            for (int j = i + 1; j < coord.length; j++) {
                xd = coord[i][0] - coord[j][0];
                yd = coord[i][1] - coord[j][1];
                int distance = (int) Math.ceil(Math.sqrt((xd * xd) + (yd * yd)));
                vrp.addCost(i, j, distance);
                vrp.addCost(j, i, distance);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Numero de argumentos invalidos");
            System.exit(1);
        }
        String InstanceName = args[0];
        try {
            LoadData(InstanceName);
            algorithm = new IteratedLocalSearchAlgorithm(vrp);
            SolutionSet solution = algorithm.execute();
            writeFile(solution, InstanceName);
        } catch (Exception e) {
            System.out.print("Error: ");
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
