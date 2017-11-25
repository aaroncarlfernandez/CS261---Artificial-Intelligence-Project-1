import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStarAlgorithm extends AlgoSolver {
    private Heuristic heuristic;
    private AStarAlgorithm(MapState initialMap) {
        super(initialMap);
        queue = new PriorityQueue<MapState>();
    }
    public AStarAlgorithm(MapState initialMap, Heuristic heuristic) {
        this(initialMap);
        this.heuristic = heuristic;
    }
    @Override
    protected void searchFunction(ArrayList<MapState> validMoves) {
        
        for (MapState move : validMoves) {
            backtrack.put(move, currentState);
            heuristic.score(move);
            queue.add(move);
        }
    }
}