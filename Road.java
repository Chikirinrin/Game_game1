/**
 * This Class represents a road between two cities. A road has
 * References to the two City-objects it connects. Furthermore it
 * has a length which is represented by an integer, which provides
 * information about how long it takes to travel between the cities.
 *
 *
 * @author Lasse Sode & Timm Askøe
 * @version 1.0.0
 */
public class Road implements Comparable<Road>
{
    /** reference to the cities connected by this Road*/
    private City from, to;

    /** Length of this Road. */

    private int length;

    /**
     * Constructor for objects of class Road
     * @param from The City the road is going from.
     * @param to The City the road is going to.
     * @param length The time it takes to travel the road.
     */
    public Road(City from, City to, int length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }


    /**
     * Returns a reference to the City where this Road starts.
     * @return from city.
     */
    public City getFrom() {
        return from;
    }

    /**
     * Return a reference to the City where this Road ends.
     * @return to city
     */
    public City getTo() {
        return to;
    }

    /**
     * Returns a reference to the length of the Road.
     * @return length integer.
     */
    public int getLength() {
        return length;
    }

    /**
     * Compares the Road's from and to fields, and sorts in
     * Alphabetical order after the name of the cities where
     * the Roads starts. If two roads starts in the same city, it'll
     * sort after the Roads' destination.
     * @param road The road object being compared.
     * @return Integer, for sorting.
     */
    public int compareTo(Road road){
        if(this.from.equals(road.getFrom())){
            return (this.to.compareTo(road.getTo()));
        }
        return (this.from.compareTo(road.getFrom()));
    }

}