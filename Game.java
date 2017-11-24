import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A Game object is an instance of NordicTraveller.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class Game {
 
	/** Set of all countries in the game */
	private List<Country> countries;
	
	/** List of all players */
	private List<Player>  players;
	
	/** Reference to the GUI Player */
	private Player guiPlayer;
	
	/** The random generator */
	private Random random;
	
	/** Whether or not to log this game */
	private boolean logging;
	
	/** Variables for time */
	private int totalTimeLeft = 50, timeLeft = getTotalTimeLeft();

	/** The seed of this Game instance (used for Random) */
	private int seed;

	/** Positions of the GUI for the various cities (in pixels) */
    private Map<City, Point> guiPosition;
    
    /** The settings for this Game */
    private Settings settings;
    
    /** Whether or not this Game is forcefully aborted */
    private boolean aborted=false;
    
    /**
     * Instantiates a new Game object with a random seed.
     */
    public Game(){
    	this((int)(Math.random()*Integer.MAX_VALUE));
    }
    
    /**
     * Instantiates a new Game object with a given seed.
     * @param seed
     */
	public Game(int seed){
		//Create random
		this.seed = seed;
		random = new Random(seed);
		
		//Instantiate collections
		countries = new ArrayList<Country>();
		players   = new ArrayList<Player>();
		guiPosition = new HashMap<City, Point>();
		
		//Try to load Settings from file, otherwise default to normal settings
		try{
			settings = new Settings(new String(Files.readAllBytes(Paths.get("settings.dat"))));
		} catch(IOException e){
			settings = new Settings();
		} catch(SettingsException e){
			settings = new Settings();
		}

		//Logging
		logging = false;
	}
	
	public void abort(){
		aborted = true;
	}
	
	/**
	 * Determines whether or not this game is still ongoing.
	 * @return True if this game is ongoing, false otherwise.
	 */
	public boolean ongoing(){
		return !aborted && timeLeft!=0;
	}
	
	/**
	 * Get the global Random object.
	 * @return A reference to the current Random generator.
	 */
	public Random getRandom(){
		return random;
	}
	
	/**
	 * Get the Settings object.
	 * @return A reference to the current Settings object.
	 */
	public Settings getSettings(){
		return settings;
	}
	
	/**
	 * Determines how badly a Player gets robbed.
	 * Returns a random integer in the interval [minRobbery, maxRobbery] using the current Settings object.
	 * Uses the global Random generator.
	 * @return An integer representing how many euroes the given player lost.
	 */
	public int getLoss(){
		return settings.getMinRobbery() + random.nextInt(settings.getMaxRobbery() - settings.getMinRobbery() + 1);
	}
	
	/**
	 * Associates a given City object with a position on the GUI.
	 * @param c The city.
	 * @param p The position (in pixels).
	 */
	public void putPosition(City c, Point p){
		guiPosition.put(c, p);
	}
	
	/**
	 * Get the GUI position of a given City.
	 * @param c The city.
	 * @return The GUI position (in pixels) of the city 'c'.
	 */
	public Point getPosition(City c){
		return guiPosition.get(c);
	}
	
	/**
	 * Returns a random position within this Game instance.
	 * First chooses a random country, and then a random city within that country.
	 * @return A randomly chosen Position.
	 */
	public Position getRandomStartingPosition(){
		Country c = getRandom(countries);
		return c.position(getRandom(c.getCities()));
	}
	
	/**
	 * Resets this given Game instance. This consists of the following actions:
	 *  * Reset the Log.
	 *  * Reset the Random object with some seed.
	 *  * Possibly removes the LogPlayer.
	 *  * Resets all countries.
	 *  * Assigns randomly positions to all players.
	 * @param repeat Whether or not to repeat the current seed. If not, a new seed is randomly chosen.
	 * @param removeLog Whether or not to remove the LogPlayer (if it exists).
	 */
	public void reset(boolean repeat, boolean removeLog){
		if(!repeat){
			seed = random.nextInt(Integer.MAX_VALUE);
		}
		random = new Random(seed);
		timeLeft = totalTimeLeft;
		aborted=false;
		

		
		for(Country c : countries)
			c.reset();
		Collections.sort(players);
		for(Player p : players){
			p.reset();
			p.setPosition(getRandomStartingPosition());
		}
	}
	
	/**
	 * Returns a random element in a given Set.
	 * @param set The set.
	 * @return A randomly chosen element from the given set.
	 */
	private <T> T getRandom(Collection<T> set){
		int r = random.nextInt(set.size());
		int i=0;
		for(T t : set)
			if(i++==r)
				return t;
		return null;
	}
	
	/**
	 * Associates the GUI player with some specific Player instance. 
	 * @param p The Player object representing the GUI Player.
	 */
	public void setGUIPlayer(Player p){
		this.guiPlayer = p;
		players.add(p);
	}
	
	/**
	 * Get a list of all countries within this Game instance.
	 * @return A list of all countries.
	 */
	public List<Country> getCountries(){
		return countries;
	}
	
	/**
	 * Adds a given Country to this Game instance.
	 * @param c The country to add.
	 */
	public void addCountry(Country c){
		countries.add(c);
		c.setGame(this);
		Collections.sort(countries, Comparator.comparing(k -> k.getName()));
	}
	
	/**
	 * Gets the GUI Player (the one controlled by the GUI).
	 * @return A reference to the GUI Player.
	 */
	public Player getGUIPlayer(){
		return guiPlayer;
	}
	
	/**
	 * Gets a list of all players associated with this Game instance.
	 * @return A list of all players.
	 */
	public List<Player> getPlayers(){
		return players;
	}
	
	/**
	 * Gets a specific City object, based on its name (findOne).
	 * Will traverse all countries, and if a city with the name 'search' is found, it is returned.
	 * If no such city exists, null is returned.
	 * @param search The name of the city to search for (case sensitive).
	 * @return A City object with the name 'search', or null.
	 */
	public City getCity(String search){
		City city = null;
		for(Country c : countries){
			city = c.getCity(search);
			if(city != null)
				return city;
		}
		return null;
	}
	
	/**
	 * Advances this Game instance one step.
	 * A step consists of moving all players once on the road they're currently travelling, as well as updating money.
	 * If this step was the last step (that is, getStepsLeft()==1 before invoking this method), the Log representing the game is saved to 'last.log'.
	 */
	public void step(){
		if(timeLeft==0 || aborted)
			return;
		Collections.sort(players);
		for(Player p : players){
			if(p.getClass()==RandomPlayer.class && !settings.isActive(0))
				continue;
			if(p.getClass()==GreedyPlayer.class && !settings.isActive(1))
				continue;
			if(p.getClass()==SmartPlayer.class && !settings.isActive(2))
				continue;
			p.step();
			if(p.getMoney()<0)p.reset();
		}
		--timeLeft;
	}
	/**
	 * Gets the number of steps remaining in this Game instance.
	 * @return An integer representing how many steps this Game object can take before reaching the end.
	 */
	public int getStepsLeft(){
		return timeLeft;
	}
	
	/**
	 * This method is called whenever a City is clicked.
	 * Is used mainly by the GUI instance to invoke player commands.
	 * It is also used by the LogPlayer to simulate mouse clicks.
	 * @param c The city to click.
	 */
	public void clickCity(City c){
		
		guiPlayer.travelTo(c);
	}
	
	/**
	 * Adds roads between 'a' and 'b' (if they exist) with a given length.
	 * Adds a road from a->b, as well as a road from b->a (it is a symmetrical operator).
	 * @param a A string representing the first city.
	 * @param b A string representing the second city.
	 * @param len The length of the road to construct.
	 */
	public void addRoads(String a, String b, int len) {
		City A = null, B = null;
		for(Country country : countries){
			for(City city : country.getCities()){
				if(city.getName().equals(a))
					A = city;
				if(city.getName().equals(b))
					B = city;
			}
		}
		if(A==null)
			throw new RuntimeException("No such city: '"+a+"'.");
		if(B==null)
			throw new RuntimeException("No such city: '"+b+"'.");
		addRoads(A,B,len);
	}

	/**
	 * Adds roads between 'a' and 'b' (if they exist) with a given length.
	 * Adds a road from a->b, as well as a road from b->a (it is a symmetrical operator).
	 * @param a The first City object.
	 * @param b The second City object.
	 * @param i The length of the road to construct.
	 */
	public void addRoads(City a, City b, int i) {
		for(Country country : countries){
			country.addRoads(a, b, i);
			if(country.getNetwork().containsKey(a)){
				Collections.sort(country.getNetwork().get(a), Comparator.comparing(r -> r.getFrom().getName()));
				Collections.sort(country.getNetwork().get(a), Comparator.comparing(r -> r.getTo().getName()));
			}
			if(country.getNetwork().containsKey(b)){
				Collections.sort(country.getNetwork().get(b), Comparator.comparing(r -> r.getFrom().getName()));
				Collections.sort(country.getNetwork().get(b), Comparator.comparing(r -> r.getTo().getName()));
			}
		}
	}

	/**
	 * Gets how much time this game had at its conception.
	 * @return An integer representing how many steps this Game had available when it was created.
	 */
	public int getTotalTimeLeft() {
		return totalTimeLeft;
	}

	/**
	 * Changes the total time left of this Game instance.
	 * @param totalTimeLeft The new total time left.
	 */
	public void setTotalTimeLeft(int totalTimeLeft) {
		this.totalTimeLeft = totalTimeLeft;
	}
}
