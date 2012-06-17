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
        String line = file.readLine();
        separator = line.split(" ");
        
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
    }
}
