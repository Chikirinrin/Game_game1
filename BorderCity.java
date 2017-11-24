public class BorderCity extends City {
    /**
     * Constructor for city, where we initialise the field variables
     *
     * @param name the city name
     * @param value the value of the city
     * @param country witch country the city in is
     */

    public BorderCity(String name, int value, Country country) {
        super(name, value, country);
    }
    @Override
    public int arrive(Player p){
        int b = super.arrive(p);
        if(!getCountry().equals(p.getCountryFrom())){
                int toll = ((getCountry().getGame().getSettings().getTollToBePaid()*p.getMoney())/100);
                changeValue(toll);
                return b-toll;
           }
        return b;
    }
}
