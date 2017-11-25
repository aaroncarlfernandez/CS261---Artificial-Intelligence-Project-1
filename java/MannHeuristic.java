import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class MannHeuristic implements Heuristic {
    @Override
    public void score(MapState state) {
        Set<Point> goals = state.getGoals();
        Set<Point> boxes = state.getBoxes();
        
        // This will avoid the AI to check box already on goal!
        Set<Point> intersection = new HashSet<Point>(goals);
        intersection.retainAll(boxes);
        goals.removeAll(intersection);
        boxes.removeAll(intersection);
        
        int cost = 0;
        
        /*
         * This loop evaluates the available boxes that can be pushed to goal points, calculates cost towards each available goals, decides which target goal would cost less
         * per available box and return the aggregated cost for all available boxes
         */
        for (Point box : boxes) {
            int minMarginalCost = Integer.MAX_VALUE;
            /*
             * This inside loop determines which goal would have the least cost using Manhattan method per available box
             */
            for (Point goal : goals) {
                int dist = computeManhattanDistance (box, goal);
                if (dist < minMarginalCost)
                    minMarginalCost = dist;
            }
            cost += minMarginalCost;
        }
        state.setCost(cost);
    }
    /*
     * p1 - closest box 
     * p2 - closest goal
     */
    private static int computeManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}