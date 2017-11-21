
/**
 * Write a description of class City here.
 * The City class represents a city, with two integers 'value'
 * and 'initialValue'. The city also has a string, witch matches the name of the city.
 * The last field variable with is a country, in where the city is.
 *
 * @author (Lasse Sode , Timm Askøe)
 * @version (a version number or a date)
 */
public class City implements Comparable<City>
{
    /** instance variables - replace the example below with your own*/
    private String name;
    private int value;
    private int initialValue;
    private Country country;

    /** Constructor for city, where we initialise the field variables */
    public City(String name, int value, Country country) {
        this.name=name;
        this.value = value;
        this.initialValue = value;
        this.country = country;
    }

    /** Returns the name of the city *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /** Returns the value of the city
     * @return integer value
     */
    public int getValue() {
        return value;
    }

    /** Returns the country of witch the city is in
     * @return Country country
     */
    public Country getCountry(){
        return country;
    }

    /** Add an amount to the value of the city*
     * @param amount the amount we add to value
     */
    public void changeValue(int amount){
        this.value = getValue()+amount;
    }

    /** Resets the value to the initial value*/
    public void reset(){
        this.value = this.initialValue;
    }

    /**
     * Compares the Cities name, and sorts in
     * Alphabetical order after the name of the cities
     * @param city The city object being compared.
     * @return Integer, for sorting.
     */
    public int compareTo(City city) {
        return name.compareTo(city.getName());
    }

    /** Reduces the value of the city with the value of the arrive bonus
     *  @return integer b, b for bonus
     */
    public int arrive(){
        int b = country.bonus(value);
        value -= b;
        return b;
    }
    public int hashCode(){
        return 19*name.hashCode();
    }
}
