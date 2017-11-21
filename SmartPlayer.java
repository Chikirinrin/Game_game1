import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;

/**
 * A smart AI player which uses a depth-first search to determine the optimal PlayerPath.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 */
public class SmartPlayer extends Player 
{

	/**
	 * Instantiates a new SmartPlayer with the specified position.
	 * @param pos The position of this player.
	 */
	public SmartPlayer(Position pos) {
		super(pos);
	}
	
	@Override
	public void step() {
		super.step();
		if(getPosition().hasArrived()) {
			City city = getPosition().getTo();
			setPosition(getCountry().readyToTravel(city, maximizeValue(city, getCountry().getGame().getStepsLeft())));
		} 
	}
	
	/**
     * Finds the city which in the long run generates most value.
     * Performs a DFS of the space of all cities (depth limited by n).
     * @param c The city to travel fro
     * @param n The number of steps remaining
     * @return The city to travel to now.
	 */
	private City maximizeValue(City c, int n) {
		HashMap<City, Integer> visited = new HashMap<City, Integer>();
		visited.put(c, 1);
		PlayerPath best = maximizeValue(visited, c, n);
		if(best.isEmpty()) {
			return c;
		}
		return best.getRoad().getTo();
	}
	
	/** The maximum depth to search. May become slow if >= 30 */
	private static final int MAX_DEPTH = 26;

	/**
     * Finds the city which in the long run generates most value.
     * Performs a DFS of the space of all cities (depth limited by n).
     * Takes into account that cities lose value when visited.
     * @param visits How many times each city is visited.
     * @param c The city to travel from
     * @param n The number of steps remaining
     * @return The path which provides maximum value.
	 */
	private PlayerPath maximizeValue(HashMap<City, Integer> visits, City c, int i) {
		int n = i;
		if(i>MAX_DEPTH) {
			n=MAX_DEPTH;
		}
		PlayerPath p = new PlayerPath(this);
		double bestValue = 0;
		for(Road r : c.getCountry().getRoads(c)) {
			if(r.getLength() <= n){
				HashMap<City, Integer> newVisits = new HashMap<City, Integer>(visits);
				City to = r.getTo();
				int v = 0;
				if(newVisits.containsKey(to))
					v=newVisits.get(to);
				newVisits.put(to, ++v);
				
				PlayerPath subPlayerPath = maximizeValue(newVisits, to, n - r.getLength());
				subPlayerPath.addRoad(r, v);
				double newValue = subPlayerPath.getValue();
				if(newValue > bestValue || (newValue==bestValue && subPlayerPath.getLength() < p.getLength())){
					p = subPlayerPath;
					bestValue = newValue;
				}
			}
		}
		
		return p;
	}

	@Override
	public String getName(){
		return "Smart Player";
	}
	
	@Override
	public Color getColor(){
		return Color.CYAN;
	}
}

/**
 * Represents a possible execution path for the SmartPlayer object.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 */
class PlayerPath {

    /** The roads to visit in this path */
	private Stack<Road> edges;

	/** The length (in steps) of the path */
	private int length;

	/** The estimated value of the path */
	private double value;

	/** How large the penalty is for choosing same city twice */
	private double impulsiveness = 1.10;
	
	/** The SmartPlayer instance which invoked maximizeValue */
	private SmartPlayer source;
	
	public PlayerPath(SmartPlayer source) {
		this.source = source;
		edges = new Stack<Road>();
		length = 0;
		value = 0;
	}

	/**
     * Returns the uppermost Road in this path.
     * @return the uppermost Road in this path.
	 */
	public Road getRoad() {
		return edges.peek();
	}
	
	/**
	 * Returns the length (in units) of this path.
	 * @return the length (in units) of this path.
	 */
	public int getLength() {
		return length;
	}
	
	/**
     * Returns the value (in €) of this path.
     * @return the value (in €) of this path.
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Determines whether or not this PlayerPath is empty.
	 * @return True, if this instance is empty; and false otherwise.
	 */
	public boolean isEmpty() {
		return edges.isEmpty();
	}
	
	@Override
	public String toString() {
		if(edges.isEmpty()) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for(Road r : edges) {
			sb.append(r + ", ");
		}
		return sb.toString().substring(0,sb.length()-2)+"]";
	}
	
	/**
	 * Adds a new road to this path.
	 * @param r The road to add
	 * @param penalty The penalty of the bonus.
	 */
	public void addRoad(Road r, int penalty) {
		if(!edges.isEmpty()) {
			Road top = getRoad();
			if(!top.getFrom().equals(r.getTo())) {
				throw new RuntimeException("Invalid road. You tried to add road to "+r.getTo()+", but the next city is "+top.getFrom());
			}
		} else {
			value += valueFrom(r, penalty);
		}
		edges.add(r);
		length += r.getLength();
		value+=valueTo(r, penalty);
	}
	
	/**
	 * Estimates the value of arriving to the 'from' of the road.
	 * @param r The road
	 * @param penalty The penalty
	 */
	public double valueFrom(Road r, int penalty) {
		return r.getFrom().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));
	}
	
	/**
	 * Estimates the value of arriving to the 'to' of the road.
	 * @param r The road
	 * @param penalty The penalty
	 */
	public double valueTo(Road r, int penalty) {
		return r.getTo().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));
	}
}