public class CapitalCity extends BorderCity {
    /**
     * Constructor for city, where we initialise the field variables
     *
     * @param name    the city name
     * @param value   the value of the city
     * @param country witch country the city in is
     */
    public CapitalCity(String name, int value, Country country) {
        super(name, value, country);
    }

    @Override
    public int arrive(Player p) {
        int b = super.arrive(p);
        int money = b+p.getMoney();
        int funMoney = getCountry().getGame().getRandom().nextInt(money+1);
        changeValue(funMoney);
        return b-funMoney;
    }
}

