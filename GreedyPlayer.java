import java.awt.Color;
import java.util.List;
import java.util.Set;

/**
 * An AI player which greedily chooses its path at each city (looks only one city ahead).
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class GreedyPlayer extends Player {

	/**
	 * Instantiates a new GreedyPlayer with the specified position.
	 * @param pos The position of this player.
	 */
	public GreedyPlayer(Position pos) {
		super(pos);
	}
	
	@Override
	public void step(){
		super.step();
		if(getPosition().hasArrived()){
			City city = getPosition().getTo();
			List<Road> roads = getCountry().getRoads(city);
			double best = 0;
			City bestCity = null;
			for(Road road : roads){
				double value = road.getTo().getValue() / (double) road.getLength();
				if(value > best){
					bestCity = road.getTo();
					best = value;
				}
				
			}
			if(bestCity!=null)
				setPosition(getCountry().readyToTravel(city, bestCity));
		}
	}

	@Override
	public String getName(){
		return "Greedy Player";
	}
	
	@Override
	public Color getColor(){
		return Color.ORANGE;
	}
}