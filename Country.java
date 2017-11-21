import java.util.List;
import java.util.Map;
import java.util.*;


/**
 * The Country class represents a country with a network of cities
 * and roads, which is represented by the fieldvariable of the type
 * Map<City, List<Road>>. Every city is depicted in a List<Road> -
 * a list containing all the roads starting in the given city.
 *
 * @author Lasse Sode & Timm Askøe
 * @version 1.0.0
 */

public class Country
{
    /** Reference to the road network for every city. */
    private Map<City,List<Road>> network;
    /** The name of the country*/
    private String name;
    /** Reference to the country*/
    private Country country;
    /** Reference to the game the country belongs to*/
    private Game game;

    /**
     * Creates a new Country object.
     * @param name The name of the Country.
     * @param network The network of Roads for each city.
     */
    public Country(String name, Map<City, List<Road>> network) {
        this.network = network;
        this.name = name;
    }

    /**
     * Returns a reference to the game which the country is a part of.
     * @return game Game.
     */
    public Game getGame(){
        return game;
    }
    /**
     * Mutator method which sets which game the country belongs in.
     * @param game the game which the country should be a part of.
     */
    public void setGame(Game game){
        this.game = game;
    }

    /**
     * Returns the Country's network.
     * @return returns a Map<City, List<Road>> representing the country's network.
     */
    public Map<City, List<Road>> getNetwork() {
        return network;
    }

    /**
     * Get the country object.
     * @return A reference to the Country object. Overflødig og meningsløs.
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Get the name of the country.
     * @return A String with the name of the Country.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns all roads starting in City. If the City doesn't
     * exist in the current country, an empty list is returned.
     * @param city The starting City.
     * @return Roads starting in the City.
     */
    public List<Road> getRoads(City city){
        if(network.containsKey(city)){
            return network.get(city);
        }
        return Collections.emptyList();
    }

    /**
     * Returns a sorted list of all cities in the country.
     * @return Sorted ArrayList of all cities in the country.
     */
    public List<City> getCities(){
        List<City> cities  = new ArrayList<>(network.keySet());
        Collections.sort(cities);
        return cities;
    }

    /**
     * Gets the City which is represented by a string equal to the
     * parameter name. If this City doesn't exist, null is returned.
     * @param name The name of the City searched for.
     * @return the City object or null.
     */
    public City getCity(String name){
        for (City city : getCities() ){
            if(city.getName().equals(name)){
                return city;
            }
        }
        return null;
    }

    /**
     * Resets the values of all the cities in the country.
     */
    public void reset(){
        for(City city: getCities()){
            city.reset();
        }
    }

    /**
     * Calculates the size of the bonus which the player recieves,
     * when entering a City.
     * @param value the value of the City.
     * @return An integer representing how much of the city's value the player recieved as a bonus.
     */
    public int bonus(int value){
        if(value>0){
            return game.getRandom().nextInt(value+1);
        }
        return 0;
    }

    /**
     * Adds roads between City a & City B, with a given length.
     * If both Cities are a part of the same country, 2 Roads going
     * between both cities are created.
     * If only one of the Cities are a part of the country, one Road from
     * the City in the country to the other country's City is created.
     * If none of the Cities are located in the country, no Roads are created.
     * @param a City a.
     * @param b City b.
     * @param length The length of the Road.
     */
    public void addRoads(City a, City b, int length){
        if(network.containsKey(a)) {
            Road roadab = new Road(a, b, length);
            network.get(a).add(roadab);
        }

        if(network.containsKey(b)){
            Road roadba = new Road(b,a,length);
            network.get(b).add(roadba);
        }

    }

    /**
     * Returns the Position of the city. As if the player is standing
     * in the City, without having chosen a direction yet.
     * @param city the City which position is searched for.
     * @return a Position object looking like Position(city, city, 0).
     */

    public Position position(City city){
        return new Position(city, city, 0);
    }

    /**
     * Returns a Position object, representing the player being ready to
     * start his travel from "from" towards "to", while still standing in "from".
     * If "from" and "to" are the same cities, the position of the City is returned.
     * If there are no direct way from "from" towards "to", or "from" isn't
     * a part of the Country, the position
     * of the City is returned.
     * @param from The City which the player is standing in.
     * @param to The City which the player is readt to travel to.
     * @return a Position object.
     */
    public Position readyToTravel(City from, City to){
        if (from.equals(to)){
            return position(from);
        }

        for (Road road : getRoads(from)) {
            if (road.getTo().equals(to)) {
                return new Position(from, to, road.getLength());
            }
        }
        return position(from);
    }

     @Override
    public int hashCode(){
        return 17*name.hashCode();
    }

    @Override
    /**
     *  Overwriting of the Object class' equals method, so that two Countries are the same, only if their names'
     *  are the same.
     *  @param The other country/object which is being compared.
     *  @return A boolean, true if they equal eachother, false if not.
     */
    public boolean equals(Object otherCo ) {

        if (this == otherCo) {
            return true;
        }
        if (otherCo == null) {
            return false;
        }
        if (getClass() != otherCo.getClass()) {
            return false;
        }
        Country other = (Country)otherCo;
        return name.equals(other.name);
    }
}

