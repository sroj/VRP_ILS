package VRP_ILS;

import VRP.MetaheuristicOptimizationAlgorithm;
import VRP.SolutionSet;
import VRP.VehicleRoutingProblem;

/**
 * @author Andrea Aranguren
 * @author Simon Rojas
 */
public class IteratedLocalSearchAlgorithm
        implements MetaheuristicOptimizationAlgorithm {

    //INICIO de estructuras para representar una solucion al problema
    int[] customers;
    int[] partitionIndexes;
    int numberOfPartitionIndexes;
    int totalCost;
    //FIN de estructuras para representar una solución al problema
    VehicleRoutingProblem vrpInstance;

    public IteratedLocalSearchAlgorithm(VehicleRoutingProblem vrpInstance) {
        this.customers = new int[vrpInstance.getNumberOfCustomers()];
        this.partitionIndexes = new int[vrpInstance.getNumberOfCustomers() - 1];
        this.vrpInstance = vrpInstance;
        this.totalCost = 0;
        //constructInitialSolution();
    }

    private void constructInitialSolution() {
        initializePartition();
        int n=vrpInstance.getNumberOfCustomers();
        int s[][]= new int[n][n];
        calculateSavings(s);
        Index index = getBestSaving(s);
        while(index.getSaving()>=0){
            mergeRoutes(index);
            index = getBestSaving(s);
        }
    }
    
    private void mergeRoutes(Index index){

    }

    @Override
    public SolutionSet execute() {
        //TODO aqui coloqué un return chimbo, para probar la escritura enel archivo
        return (new ILSSolutionSet(numberOfPartitionIndexes, numberOfPartitionIndexes,
                numberOfPartitionIndexes, numberOfPartitionIndexes,
                numberOfPartitionIndexes, "hola"));
    }

    private void initializePartition() {
        for(int i=0;i<vrpInstance.getNumberOfCustomers(); i++){
            this.customers[i]=i+1;
            this.totalCost += 2*(this.vrpInstance.getCost(0, customers[i]));
            this.partitionIndexes[i]=i+1;
            this.numberOfPartitionIndexes = vrpInstance.getNumberOfCustomers()-1;            
        }
    }

    private void calculateSavings(int[][] s) {
        for(int i=0; i<s.length; i++){
            for(int j=i+1; i<s.length;j++){
                int a = vrpInstance.getCost(0,i+1);
                int b = vrpInstance.getCost(j+1, 0);
                int c = vrpInstance.getCost(i+1, j+1);
                s[i][j]=(a+b)-c;
                //Esto sólo se hace por completitud ya que no se utilizará
                s[j][i]=(a+b)-c; 
            }
        }
    }

    private Index getBestSaving(int[][] s) {
        int x=-1;
        int y=-1;
        int max = -1;
        for(int i=0; i<s.length; i++){
            for(int j=i+1; i<s.length;j++){
                if(s[i][j] > max){
                    max= s[i][j];
                    x=i;
                    y=j;
                }
            }
        }
        s[x][y]=-1;
        s[y][x]=-1;
        return(new Index(x+1, y+1, max));
    }
    
    private static class Index{
        int i;
        int j;
        int saving;
        
        public Index(int i, int j, int saving){
            this.i=i;
            this.j=j;
            this.saving= saving;
        }
        
        public int getI() {
            return i;
        }
        
        public int getJ() {
            return j;
        }

        public int getSaving() {
            return saving;
        }
        
    }
}
