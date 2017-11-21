
/**
 * Write a description of class Position here.
 * The position class represents a position of the player is compared to the cities.
 *
 * @author (Lasse Sode , Timm Askøe)
 * @version (1.0.0)
 */
public class Position
{
    /** instance variables - replace the example below with your own*/
    private City from;
    private City to;
    private int distance;
    private int total;

    /** Constructor for the Position class. The field variables is initialised*
     * @param from City the player comes from
     * @param to City the player goes to
     * @param distance the distance to the city we are going to
     * @param total the total distance between the two cities
     */

    public Position(City from, City to, int distance) {
        this.from = from; // Byen vi kommer fra
        this.to = to; // Byen vi er på vej til
        this.distance = distance; // Afstand der er tilbage
        this.total = distance; // Totale afstand
    }

    /** instance variables - replace the example below with your own*
     * @return City from
     */
    public City getFrom() {
        return from;
    }

    /** Return the city where the player is going to *
     * @return City To
     */
    public City getTo() {
        return to;
    }

    /** Return the distance from the player to the city *
     *  @return integer distance
     */
    public int getDistance() {
        return distance;
    }

    /** Return the distance from the player to the city
     * @return integer total distance
     */
    public int getTotal() {
        return total;
    }

    /** Returns true if the distance to the city the player is going to is equals to zero, else it returns false  *
     * @return boolean if we have arrived it is true.
     */
    public boolean hasArrived(){
        if(distance == 0){
            return true;
        }
        return false;
    }


    /** Counts distance down by 1, each time the player moves one step further towards the city.
     * But only if the player isn't already there
     * @return boolean if we can move it is true
     */
    public boolean move(){
        if(distance > 0){
            distance--;
            return true;
        }
        return false;
    }

    /** Turns the player around, so the player now is going to the city he was coming from.
     * By setting the distance to the length he has already moved towards the city.
     * Total should still be the same, turning around doesn't change the location of the cities.
     */
    public void turnAround(){
        distance = total-distance;
        City tempTo = to;
        to = from;
        from = tempTo;
    }
}
