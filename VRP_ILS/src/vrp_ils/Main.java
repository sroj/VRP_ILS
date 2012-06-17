package vrp_ils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Andrea Aranguren
 * @author Simon Rojas
 */


public class Main {
    
    private static VehicleRoutingProblem vrp;
    private static MetaheuristicOptimizationAlgorithm algorithm;
    
    
    private static void LoadData(String path) throws IOException{
        BufferedReader file = new BufferedReader(new FileReader(path));
        String separator[];
        vrp = new VehicleRoutingProblem();
        String line = file.readLine();
        separator = line.split(" ");
        int i=0;
        if(separator[i].equals("")){
            i++;
        }
        vrp.setNumberOfCustomers(Integer.parseInt(separator[i]));
        vrp.setVehicleCapacity(Integer.parseInt(separator[i+1]));
        vrp.setMaximumRouteTime(Integer.parseInt(separator[i+2]));
        vrp.setDropTime(Integer.parseInt(separator[i+3]));
        
        int coord[][]= new int[vrp.getNumberOfCustomers()+1][2];
        line = file.readLine();
        separator = line.split(" ");
        i=0;
        if(separator[i].equals("")){
            i++;
        }
        coord[0][0] = Integer.parseInt(separator[i]);
        coord[0][1] = Integer.parseInt(separator[i+1]);
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Numero de argumentos invalidos");
            System.exit(1);
        }
    }
}
