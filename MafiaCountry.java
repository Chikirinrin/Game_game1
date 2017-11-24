import java.util.List;
import java.util.Map;
import java.util.Random;

public class MafiaCountry extends Country {
    /**
     * Creates a new Country object.
     *
     * @param name    The name of the Country.
     * @param network The network of Roads for each city.
     */
    public MafiaCountry(String name, Map<City, List<Road>> network) {
        super(name, network);
    }

    @Override
    public int bonus(int value){
       int n = getGame().getRandom().nextInt(100);
       if(getGame().getSettings().getRisk()> n){
           return -getGame().getLoss();
       }
       else{
           return super.bonus(value);
       }
    }
}
