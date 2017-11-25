import java.io.IOException;
/**
 * Project 1 on A*
 * Author: Aaron Carl Fernandez - CS261 - Artificial Intelligence
 * Date written: April 2017
 */
public class ProjectOne {
    public static void main(String[] args) {
        acceptArgs(args);
    }
    
    public static void acceptArgs(String[] args) {
        try {
            String puzzleMap = args[0];
            MapState initialMap = MapState.parseMapInput(puzzleMap);
            System.out.println(" *** This will show all the movements A* has undertaken to explore the possible paths including those wherein it encountered a deadlock ***" +'\n');
            AlgoSolver solver = null;
            solver = new AStarAlgorithm(initialMap, new MannHeuristic());
            
            if (solver!= null) {
                String solution = solver.search();
            }
              
        } catch (IOException e) {
            System.out.println("Invalid Input File");
        } catch (CannotSolve e) {
            System.out.println("Problem can't be solved!");
        }
        
    }
}           