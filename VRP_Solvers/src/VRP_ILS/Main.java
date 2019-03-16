package VRP_ILS;

import VRP.VehicleRoutingProblem;
import java.io.*;
import java.util.Arrays;

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
    private static String[] Split_line(String line)
    {
        String separator[];
        separator = line.split(" ");
        int i = 0;
        if (separator[i].equals("")) {
            i++;
        }
        return Arrays.copyOfRange(separator, i, separator.length);
    }
    // adaptation for TSPLib95
    private static void LoadData_new(String path) throws Exception {
        BufferedReader file = new BufferedReader(new FileReader(path));
        String separator[];
        file.readLine();
        file.readLine();
        file.readLine();
        String line = file.readLine();
        separator = line.split(" ");
        int n = Integer.parseInt(separator[2]);
        file.readLine();
        line = file.readLine();
        separator = line.split(" ");
        int capacity = Integer.parseInt(separator[2]);
        int maxRouteTime = 999999;
        int dropTime = 0;
        vrp = new VehicleRoutingProblem(n-1, capacity, maxRouteTime, dropTime);
        file.readLine();
        double coord[][] = new double[vrp.getNumberOfCustomers() + 1][2];
        line = file.readLine();
//        separator = line.split(" ");
//        int i = 0;
//        if (separator[i].equals("")) {
//            i++;
//        }
        separator = Split_line(line);
        coord[0][0] = Integer.parseInt(separator[1]);
        coord[0][1] = Integer.parseInt(separator[2]);
        //vrp.addCustomerDemand(0, 0);
        for (int j = 1; j <= vrp.getNumberOfCustomers(); j++) {
            line = file.readLine();
//          separator = line.split(" ");
//          int i = 0;
//          if (separator[i].equals("")) {
//              i++;
//          }
            separator = Split_line(line);
            coord[j][0] = Integer.parseInt(separator[1]);
            coord[j][1] = Integer.parseInt(separator[2]);
            // vrp.addCustomerDemand(j, Integer.parseInt(separator[i + 2]));
        }
        fillMatrix(coord);
        file.readLine();
        for (int j = 0; j <= vrp.getNumberOfCustomers(); j++) {
            line = file.readLine();
            separator = Split_line(line);
            vrp.addCustomerDemand(j, Integer.parseInt(separator[1]));
        }
    }

    private static void writeFile(ILSSolutionSet solution, String instanceName)
            throws IOException {
        //String outFileName = ("stat.").concat(instanceName);
        String outFileName = instanceName.concat("out.txt");
        BufferedWriter out = new BufferedWriter(new FileWriter(outFileName));
        out.write("Лучшее решение расстояние (с DROP TIME): " + solution.getTotaldistance());
        // Distancia de la mejor solucion (CON DROP TIME):
        out.newLine();
        out.write("Стоимость наилучшего решения (без DROP TIME): " + solution.getBestDistance());
        // Costo de la mejor solucion (SIN DROP TIME):
        out.newLine();
        out.write("Итерация лучшего решения: " + solution.getBestIteration());
        //Iteracion de la mejor solucion:
        out.newLine();
        out.write("Итерации, сделанные программой: " + solution.getNumberOfIterations());
        // Iteraciones hechas por el programa:
        out.newLine();
        out.write("Время, когда было найдено лучшее решение (s): "
                + solution.getBestTime());
        // Tiempo en el que se encontro la mejor solucion (s):
        out.newLine();
        out.write("Общее время работы алгоритма (s): " + solution.getExecutionTime());
        //Tiempo total de la corrida del algoritmo
        out.newLine();
        out.write("Количество путей решения: " + solution.getNumberOfRoutes());
        // Numero de rutas de la solucion:
        out.newLine();
        out.newLine();
        out.write("Достигнутые маршруты:");
        // Rutas conseguidas:
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
            System.err.println("Неправильное количество аргументов");
            // Numero de argumentos invalidos
            System.exit(1);
        }
        String InstanceName = args[0];
        try {
            //LoadData(InstanceName);
            LoadData_new(InstanceName);
            algorithm = new IteratedLocalSearchAlgorithm(vrp);
            ILSSolutionSet solution = algorithm.execute();
            writeFile(solution, InstanceName);
            System.out.println("Выполнение завершено");
            // Ejecución finalizada
            System.out.println("Лучшее расстояние (с DROPTIME): "
                    + solution.getBestDistance());
            // Mejor distancia (CON DROPTIME):
            System.out.println("Лучшее расстояние (без DROPTIME): "
                    + solution.getTotaldistance());
            // Mejor distancia (SIN DROPTIME):
        } catch (Exception e) {
            System.out.println("Ошибка в main");
            // Excepcion atrapada en Main
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
