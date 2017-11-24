import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MafiaCountryTest {
    private Game game;
    private Country country1, country2, country3;
    private City cityA, cityB, cityC, cityD, cityE, cityF, cityG;
    private Map<City,List<Road>> network1, network2;

    @Before
    public void setUp() {
        game = new Game(0);
        game.getRandom().setSeed(0);
        network1 = new HashMap<>();
        network2 = new HashMap<>();

        // Create countries
        country1 = new Country("Country 1", network1);
        country2 = new MafiaCountry("Country 2", network2);
        country3 = new Country("Country 1", network2);
        country1.setGame(game);
        country2.setGame(game);
        country3.setGame(game);

        // Create Cities
        cityA = new City("City A", 80, country1);
        cityB = new City("City B", 60, country1);
        cityC = new City("City C", 40, country1);
        cityD = new City("City D", 100, country1);
        cityE = new City("City E", 50, country2);
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

        country1.addRoads(cityA, cityD, 5);
        country1.addRoads(cityB, cityD, 2);
        country1.addRoads(cityC, cityD, 2);
        country1.addRoads(cityC, cityE, 4);
        country1.addRoads(cityD, cityF, 3);
        country2.addRoads(cityE, cityC, 4);
        country2.addRoads(cityE, cityF, 2);
        country2.addRoads(cityE, cityG, 5);

    }

    @Test
    public void MafiaCountry(){
        Country country1 = new MafiaCountry("country1", network1);
        Country country2 = new MafiaCountry("country2", network2);
        assertEquals(country1.getName(),"country1");
        assertEquals(country1.getNetwork(), network1);
        assertEquals(country2.getName(),"country2");
        assertEquals(country2.getNetwork(), network2);
    }

    @Test
    public void bonus() throws Exception {
        country3.getGame().getSettings().setMinMaxRobbery(10,50);
            for(int seed = 0; seed < 1000; seed++){
                game.getRandom().setSeed(seed);
                int robs = 0;
                int loss = 0;
                Set<Integer> values = new HashSet<>();
                for(int i = 0; i<50000; i++) {
                    int bonus = country2.bonus(80);
                    if(bonus < 0) {
                        robs++;
                        assertTrue(10 <= -bonus && -bonus <= 50);
                        loss -= bonus;
                        values.add(-bonus);
                    } }
                assertTrue(9000 <= robs && robs <= 12000);
                assertTrue(285000 < loss && loss < 315000);
                assertEquals(values.size(), 41);
            }

    }

}