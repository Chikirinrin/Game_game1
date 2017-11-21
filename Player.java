import java.awt.Color;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * A Player which is controlled by the GUI.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class Player implements Comparable<Player> {

	/** The Position objects of this Player. 'pending' is the next position to attempt after finishing 'pos'. */
	private Position pos, pending;
	
	/** The amount of money this Player has collected */
	private int money;
	
	/**
	 * Instantiates a new GUI Player with the specified position.
	 * @param pos The position of this player.
	 */
	public Player(Position pos) {
		this.pos = pos;
		money = 0;
	}
	
	/**
	 * Resets this player (sets its money back to 0).
	 */
	public void reset() {
		money = 0;
	}
	
	/**
	 * Advances this Player one step.
	 */
	public void step() {
		if(pos.move() && pos.hasArrived()) {
			money += pos.getTo().arrive();
			if(pending!=null && pos.getTo().equals(pending.getFrom())) {
				pos = pending;
			}
			pending = null;
		}
	}
	
	/**
	 * Gets how much money this Player has collected.
	 * @return An integer representing how much money this Player has collected (in €).
	 */
	public int getMoney() {
		return money;
	}
	
	/**
	 * Gets the current position of this Player instance.
	 * @return The current position of this Player instance.
	 */
	public Position getPosition() {
		return pos;
	}
	
	/**
	 * Sets the position of this Player instance.
	 * Only has effect, if the Player has arrived to its destination, or the game has just started.
	 * @param newPos The new position to attempt to move this Player to.
	 */
	public void setPosition(Position newPos) {
		if(pos.hasArrived() || getCountry().getGame().getStepsLeft()==getCountry().getGame().getTotalTimeLeft()) {
			pos = newPos;
		}
	}
	
	/**
	 * Sets the pending position of this GUI Player.
	 * That is, the player will attempt to move to the pending position after finishing its current travel.
	 * This gives a more responsive design.
	 * @param pending The pending position.
	 */
	public void setPendingPosition(Position pending) {
		this.pending = pending;
	}
	
	/**
	 * Gets the Country object this Player currently resides in.
	 * If the player has arrived on its current position, the city it travelled to determines its country.
	 * Otherwise, the country is determined based on the city the player came from.
	 * @return
	 */
	public Country getCountryFrom() {
		return pos.getFrom().getCountry();
	}
	
	/**
	 * Gets the Country object this Player currently resides in.
	 * If the player has arrived on its current position, the city it travelled to determines its country.
	 * Otherwise, the country is determined based on the city the player came from.
	 * @return
	 */
	public Country getCountry() {
		if(pos.hasArrived()) {
			return pos.getTo().getCountry();
		}
		return pos.getFrom().getCountry();
	}
	
	/**
	 * Attemps to travel to the given city.
	 * A player can only travel towards the new city if it is not currently travelling (and is connected directly to the given city).
	 * If currently travelling, the city is placed on 'pending'.
	 * Can also turn around the current position, if 'from' is parsed as argument. 
	 * @param c The city to attempt to travel to.
	 */
	public void travelTo(City c) {
		City playerCity = getPosition().getTo();
		if(getPosition().hasArrived()) {
			for(Road r : getCountry().getRoads(playerCity)){
				if(r.getTo().equals(c)) {
					setPosition(getCountry().readyToTravel(playerCity, c));
				}
			}
		} else if(c.equals(getPosition().getFrom())) {
			turnAround();
		} else {
			setPendingPosition(getPosition().getTo().getCountry().readyToTravel(playerCity, c));
		}
	}

	/**
	 * Turns around this Player (travels towards 'from' instead of 'to').
	 */
	public void turnAround() {
		pos.turnAround();
	}
	
	/**
	 * Gets the display name of this Player object.
	 * @return A string representing the display name of this Player.
	 */
	public String getName() {
		return "GUI Player";
	}
	
	/**
	 * Gets the display color of this Player object.
	 * @return A Color object representing the display color of this Player.
	 */
	public Color getColor() {
		return Color.RED;
	}

	/**
	 * Compares this player to another player (sorts lexicographically based on display names).
	 */
	@Override
	public int compareTo(Player arg0) {
		return getName().compareTo(arg0.getName());
	}
}
