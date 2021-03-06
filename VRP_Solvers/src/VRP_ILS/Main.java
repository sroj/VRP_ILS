package VRP_ILS;

import VRP.VehicleRoutingProblem;
import java.io.*;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class Main {

    private static VehicleRoutingProblem vrp;
    private static IteratedLocalSearchAlgorithm algorithm;

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
        double coord[][] = new double[vrp.getNumberOfCustomers() + 1][2];
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

    private static void writeFile(ILSSolutionSet solution, String instanceName)
            throws IOException {
        String outFileName = ("stat.").concat(instanceName);
        BufferedWriter out = new BufferedWriter(new FileWriter(outFileName));
        out.write("Distancia de la mejor solucion (CON DROP TIME): " + solution.getTotaldistance());
        out.newLine();
        out.write("Costo de la mejor solucion (SIN DROP TIME): " + solution.getBestDistance());
        out.newLine();
        out.write("Iteracion de la mejor solucion: " + solution.getBestIteration());
        out.newLine();
        out.write("Iteraciones hechas por el programa: " + solution.getNumberOfIterations());
        out.newLine();
        out.write("Tiempo en el que se encontro la mejor solucion (s): "
                + solution.getBestTime());
        out.newLine();
        out.write("Tiempo total de la corrida del algoritmo (s): " + solution.getExecutionTime());
        out.newLine();
        out.write("Numero de rutas de la solucion: " + solution.getNumberOfRoutes());
        out.newLine();
        out.newLine();
        out.write("Rutas conseguidas:");
        out.newLine();
        out.newLine();
        out.write(solution.getRoutes());
        out.close();
    }

    private static void fillMatrix(double[][] coord) throws Exception {
        double xd;
        double yd;
        for (int i = 0; i < coord.length; i++) {
            for (int j = i + 1; j < coord.length; j++) {
                xd = coord[i][0] - coord[j][0];
                yd = coord[i][1] - coord[j][1];
                double distance = Math.sqrt((xd * xd) + (yd * yd));
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
            ILSSolutionSet solution = algorithm.execute();
            writeFile(solution, InstanceName);
            System.out.println("Ejecución finalizada");
            System.out.println("Mejor distancia (CON DROPTIME): "
                    + solution.getBestDistance());
            System.out.println("Mejor distancia (SIN DROPTIME): "
                    + solution.getTotaldistance());
        } catch (Exception e) {
            System.out.println("Excepcion atrapada en Main");
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
