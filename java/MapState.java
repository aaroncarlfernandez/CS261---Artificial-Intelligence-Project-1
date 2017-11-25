import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public class MapState implements Comparable<MapState> {
	// This assign byte values for look up arrays
	public static final byte PLAYER = 1 << 0;
	public static final byte WALL = 1 << 1;
	public static final byte BOX = 1 << 2;
	public static final byte GOAL = 1 << 3;
	// Character to byte look up array
	private static HashMap<String, Byte> charToField;
	private static HashMap<Byte, String> fieldToChar;
	static {
		charToField = new HashMap<String, Byte>();
		charToField.put("-1", WALL);
		charToField.put("2", GOAL);
		charToField.put("3", PLAYER);
		charToField.put(" 3", (byte) (PLAYER | GOAL));
		charToField.put("1", BOX);
		charToField.put("l", (byte) (BOX | GOAL));
		charToField.put("0", (byte) 0);
		
		fieldToChar = new HashMap<Byte, String>();
		for (Entry<String, Byte> entry : charToField.entrySet()) {
			fieldToChar.put(entry.getValue(), entry.getKey());
		}
	}
	
	private byte[][] board;
	private Point player;
	private Set<Point> goals;
	private Set<Point> boxes;
	private Point directionTaken;	
	private int cost;
	
	public MapState(byte[][] board, Point player, Set<Point> goals,
			Set<Point> boxes) {
		this(board, player, goals, boxes, null);
	}

	public MapState(byte[][] board, Point player, Set<Point> goals, 
			Set<Point> boxes, Point direction) {
		this.board = board;
		this.player = player;
		this.goals = goals;
		this.boxes = boxes;
		this.directionTaken = direction;
		cost = 0;
	}
	
	public boolean isSolved() {
		for (Point p : goals) {
			if (!(pointHas(p.x, p.y, GOAL) && pointHas(p.x, p.y, BOX))) {	
				return false;
			}
		}
		return true;
	}
	
	/**
	 This returns TRUE if the player can move in a certain direction and FALSE if otherwise
	 */
	public boolean canMove(Point direction) {
		Point newPos = new Point(player.x + direction.x, player.y + direction.y);
		Point oneOutPos = new Point(newPos.x + direction.x, newPos.y + direction.y);
		if (pointHas(newPos, BOX)) {
			// This should catch a deadlock
			if (pointHas(oneOutPos, WALL) || pointHas(oneOutPos, BOX))
				return false;
			else
				return true;
		}
		else if (pointHas(newPos, WALL))
			return false;
		else
			return true;
	}
	
	/**
	 This returns the new MapState after moving a certain direction; This can only be called if "canMove" is true
	 */
	public MapState getMove(Point direction) {
		Point newPos = new Point(player.x + direction.x, player.y + direction.y);
		Point oneOutPos = new Point(newPos.x + direction.x, newPos.y + direction.y);
		Set<Point> newBoxes = boxes;
		
		byte[][] newBoard = new byte[board.length][];
		for (int i = 0; i < newBoard.length; i++)
			newBoard[i] = board[i].clone();
            
		
		// This will move the player from its current position
		byte playerBitField = newBoard[player.x][player.y];
		newBoard[player.x][player.y] = toggleField(playerBitField, PLAYER); 
		
		// This will put the player to its new position
		byte newPlayerBitField = newBoard[newPos.x][newPos.y];
		newBoard[newPos.x][newPos.y] = toggleField(newPlayerBitField, PLAYER); 
		
		// This will make the person push the box
		if (pointHas(newPos, BOX)) {
			byte oldBoxBitfield = newBoard[newPos.x][newPos.y];
			byte newBoxBitfield = newBoard[oneOutPos.x][oneOutPos.y];
			newBoard[newPos.x][newPos.y] = toggleField(oldBoxBitfield, BOX);
			newBoard[oneOutPos.x][oneOutPos.y] = toggleField(newBoxBitfield, BOX);

			newBoxes = new HashSet<Point>(boxes);
			newBoxes.remove(newPos);
			newBoxes.add(oneOutPos);
		}

	
		return new MapState(newBoard, newPos, goals, newBoxes, direction);
        
	}
	
	/**
	 This returns TRUE if the next move has the input bitfield and FALSE if otherwise.
	 */
	public boolean nextMoveHas(byte field, Point direction) {
		Point nextPos = new Point(player.x + direction.x, player.y + direction.y);
		return pointHas(nextPos, field);
	}
	
	/**
	 This gets and returns the byte board representation used for search hashing
	 */
	public byte[][] getBoard() {
		return board;
	}

	/**
	 This gets and returns the direction that the player made to get to the MapState
	 */
	public Point getDirectionTaken() {
		return directionTaken;
	}

	/**
	 This sets the current state's cost
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 This gets and returns the current state's cost
	 */
	public int getCost() {
		return cost;
	}
	
	public Set<Point> getGoals() {
		return new HashSet<Point>(goals);
	}
	
	public Set<Point> getBoxes() {
		return new HashSet<Point>(boxes);
	}

	@Override
	public int compareTo(MapState other) {
		if (this.getCost() < other.getCost())
			return -1;
		else if (this.getCost() > other.getCost())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[0].length; col++) {
				builder.append(fieldToChar.get(board[row][col]));
			}
			builder.append('\n');
		}
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		result = prime * result + ((goals == null) ? 0 : goals.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapState other = (MapState) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		if (goals == null) {
			if (other.goals != null)
				return false;
		} else if (!goals.equals(other.goals))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}

	/**
	 This checks if a row/col pair has a certain bitfield; This returns TRUE if the board row/col has the field and FALSE if otherwise.
	 */
	private boolean pointHas(int row, int col, byte field) {
		return (board[row][col] & field) == field;
	}
	
	/**
	 This checks if a Point coordinate has a certain field where x is row and y is column; This returns TRUE if the Point coordinate has the field and FALSE if otherwise.
	 */
	private boolean pointHas(Point pos, byte field) {
		return pointHas(pos.x, pos.y, field);
	}
	
	/**
     This toggles bit by bit fields
	 */
	private byte toggleField(byte bitfield, byte field) {
		return (byte) (bitfield ^ field);
	}

    /**
     This maps out the input text file into a virtual map which the program will use.
    */
	public static MapState parseMapInput(String boardInput) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(boardInput));
		int width = Integer.parseInt(reader.readLine());
		int height = Integer.parseInt(reader.readLine());
		byte[][] boardPoints = new byte[height][width]; 
		Point player = new Point();
		Set<Point> goals = new HashSet<Point>();
		Set<Point> boxes = new HashSet<Point>();

		String line;
        char NegSgn = '-'; 
		for (int row = 0; row < height && (line = reader.readLine()) != null; row++) {
            int index1 = 0; 
            int index2 = 0;
			for (int col = 0; col < width && col < line.length(); col++) {
                
                byte field;
                char linecharAt = line.charAt(index1);
                
                if (linecharAt == NegSgn) {
                    index2 = index1 + 2;
                    field = charToField.get(line.substring(index1, index2));
                    index1 = index1 + 2;
                }   else {
                    field = charToField.get(Character.toString(line.charAt(index1)));
                    index1++;
                    index2++;
                }

				boardPoints[row][col] = field;
				if ((field & PLAYER) == PLAYER)
					player = new Point(row, col);
				if ((field & GOAL) == GOAL)
					goals.add(new Point(row, col));
				if ((field & BOX) == BOX)
					boxes.add(new Point(row, col));
			}
		}

		reader.close();
		return new MapState(boardPoints, player, goals, boxes);
	}
}