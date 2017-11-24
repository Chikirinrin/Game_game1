import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CapitalCityTest {

    private Game game;
    private Country country1, country2;
    private City cityA, cityB, cityC, cityD, cityE, cityF, cityG;

    @Before
    public void setUp() {
        game = new Game(0);
        game.getRandom().setSeed(0);
        Map<City, List<Road>> network1 = new HashMap<>();
        Map<City, List<Road>> network2 = new HashMap<>();

        // Create countries
        country1 = new Country("Country 1", network1);
        country2 = new Country("Country 2", network2);
        country1.setGame(game);
        country2.setGame(game);

        // Create Cities
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country1);
        cityC = new City("City C", 40, country1);
        cityD = new CapitalCity("City D", 100, country1);
        cityE = new CapitalCity("City E", 50, country2);
        cityF = new City("City F", 90, country2);
        cityG = new City("City G", 70, country2);

        // Create road lists
        List<Road> roadsA = new ArrayList<Road>(),
                roadsB = new ArrayList<>(),
                roadsC = new ArrayList<>(),
                roadsD = new ArrayList<>(),
                roadsE = new ArrayList<>(),
                roadsF = new ArrayList<>(),
                roadsG = new ArrayList<>();

        network1.put(cityA, roadsA);
        network1.put(cityB, roadsB);
        network1.put(cityC, roadsC);
        network1.put(cityD, roadsD);
        network2.put(cityE, roadsE);
        network2.put(cityF, roadsF);
        network2.put(cityG, roadsG);

        // Create roads
        country1.addRoads(cityA, cityB, 4);
        country1.addRoads(cityA, cityC, 3);
        country1.addRoads(cityA, cityD, 5);
        country1.addRoads(cityB, cityD, 2);
        country1.addRoads(cityC, cityD, 2);
        country1.addRoads(cityC, cityE, 4);
        country1.addRoads(cityD, cityF, 3);
        country2.addRoads(cityE, cityC, 4);
        country2.addRoads(cityE, cityF, 2);
        country2.addRoads(cityE, cityG, 5);
        country2.addRoads(cityF, cityD, 3);
        country2.addRoads(cityF, cityG, 6);
    }

    @Test
    public void arrive() throws Exception {
        for(int i =0; i<1000;i++) {
            Player player = new Player(new Position(cityA, cityE, 0), 250);
            game.getRandom().setSeed(i);
            int bonus = country2.bonus(50);
            int toll = (game.getSettings().getTollToBePaid() * player.getMoney()) / 100;
            int funMoney = player.getCountry().getGame().getRandom().nextInt(player.getMoney()- toll + bonus +1);
            game.getRandom().setSeed(i);
            int arrive = cityE.arrive(player);
            assertEquals(arrive, bonus - toll - funMoney);
            assertEquals(cityE.getValue(), 50 - bonus + toll + funMoney);
            cityE.reset();
        }
    }

    @Test
    public void arriveFromSameCountry() throws Exception {
        for(int i =0; i<1000;i++) {
            Player player = new Player(new Position(cityF, cityE, 0), 250);
            game.getRandom().setSeed(i);
            int bonus = country2.bonus(50);
            int funMoney = player.getCountry().getGame().getRandom().nextInt(player.getMoney()+bonus+1);
            game.getRandom().setSeed(i);
            int arrive = cityE.arrive(player);
            assertEquals(arrive, bonus-funMoney);
            assertEquals(cityE.getValue(), 50-bonus+funMoney);
            cityE.reset();
        }
    }

}