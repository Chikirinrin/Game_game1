import java.awt.Color;
import java.util.*;

/**
 * An AI player which randomly chooses its path.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class RandomPlayer extends Player {

	/**
	 * Instantiates a new RandomPlayer with the specified position.
	 * @param pos The position of this player.
	 */
	public RandomPlayer(Position pos) {
		super(pos);
	}
	
	@Override
	public void step() {
		super.step();

		//Determine new city when this player arrives
		if(getPosition().hasArrived()) {
			City city = getPosition().getTo();
			List<Road> roads = getCountry().getRoads(city);
			int random = getCountry().getGame().getRandom().nextInt(roads.size());
			int i=0;
			for(Road road : roads) {
				if(i++==random) {
					setPosition(getCountry().readyToTravel(city, road.getTo()));
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Random Player";
	}
	
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}
}