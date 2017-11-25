import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;
import java.util.Iterator;

public abstract class AlgoSolver {
	protected MapState currentState;
	protected HashSet<MapState> visited;
	protected HashMap<MapState, MapState> backtrack;
	protected Queue<MapState> queue;
	
	private int previouslySeen;
    
	public AlgoSolver(MapState initialState) {
		currentState = initialState;
		visited = new HashSet<MapState>();
		backtrack = new HashMap<MapState, MapState>();
		previouslySeen = 0;
	}
	
	/**
	 This searches the puzzle for solution and returns the move sequence
	 */
	public String search() throws CannotSolve {
		searchStart();

		while (!queue.isEmpty()) {
			currentState = queue.poll();
            System.out.println("+=============================================+");
            System.out.println(currentState); 
			if (visited.contains(currentState))
				previouslySeen++;
			visited.add(currentState);
            
			if (currentState.isSolved()) {
				String solution = backtrackMoves(currentState);
                System.out.println(currentState);
				return solution;
			}

            ArrayList<MapState> validMoves = getValidMoves();
			searchFunction(validMoves);
		}
		throw new CannotSolve();
	}
	
	/**
	 This initializes the search.
	 */
	protected void searchStart() {
		queue.add(currentState);
	}
	
    
	/**
	 This is a search function
	 */
	protected abstract void searchFunction(ArrayList<MapState> validMoves);
	
	/**
	 This returns the valid moves from the current state.
	 */
	protected ArrayList<MapState> getValidMoves() {
        ArrayList<MapState> validMoves = new ArrayList<MapState>(4);
        addIfValid(validMoves, Direction.UP);
        addIfValid(validMoves, Direction.RIGHT);
        addIfValid(validMoves, Direction.DOWN);
        addIfValid(validMoves, Direction.LEFT);
        return validMoves;
    }
	
	/**
	 This backtracks through the search to find the move sequence and returns the move sequence
	 */
    protected String backtrackMoves(MapState finalState) {
    	// This will trace back the path and fill LURD stack for validation
		LinkedList<Character> moveStack = new LinkedList<Character>();
        LinkedList<MapState> matrixStack = new LinkedList<MapState>();
		MapState current = finalState;
		while (current.getDirectionTaken() != null) {
			char move = Direction.directionToChar(current.getDirectionTaken());
			moveStack.push(move);
            current = backtrack.get(current);
            matrixStack.push(current);
		}
		
        System.out.println("+=============================================+" +'\n');
        System.out.println("--+ This will show all the VALID moves A* algorithm has taken to come up with the solution +---" +'\n');
        for (int matrixIdx = 0; matrixIdx < matrixStack.size(); matrixIdx++) {
            Object matrixShow = matrixStack.get(matrixIdx);
            System.out.println(matrixShow.toString());
        }
          
        
		// This is for validation of LURD output
		StringBuilder solution = new StringBuilder();
		String delim = "";
		for (Character move : moveStack) {

			solution.append(delim);
			solution.append(move);
			delim = ", ";
		}
		return solution.toString();
	}

	private void addIfValid(ArrayList<MapState> moves, Point direction) {
	    if (currentState.canMove(direction)) {
            MapState newState = currentState.getMove(direction);

	        if (!visited.contains(newState))
	            moves.add(newState);
	    }
	}


}