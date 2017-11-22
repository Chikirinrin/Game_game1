import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RoadTest {
    private Country country1, country2;
    private City cityA, cityB, cityC;
    private Map<City, List<Road>> network1;
    @Before
    public void setUp() {
        network1 = new HashMap<>();
        country1 = new Country("country 1",network1);
        cityA = new City("CityA",80, country1);
        cityB = new City("CityB",70, country1);
        cityC = new City("CityC", 10, country1);
    }



    @Test
    public void Road(){
        Road road = new Road(cityA, cityB,4);
        Road road2 = new Road(cityB,cityA, 4);

        assertEquals(road.getFrom(), cityA);
        assertEquals(road.getTo(), cityB);
        assertEquals(road.getLength(),4);
        assertEquals(road2.getFrom(), cityB);
        assertEquals(road2.getTo(), cityA);
        assertEquals(road2.getLength(),4);
    }

    @Test
    public void compareTo(){
        Road road1 = new Road(cityA, cityB, 0);
        Road road2 = new Road(cityA, cityC, 0);
        Road road3 = new Road(cityB, cityA, 0);
        assertTrue(road1.compareTo(road2) < 0);
        assertTrue(road1.compareTo(road3) < 0);
        assertTrue(road3.compareTo(road2) > 0);
        assertTrue(road3.compareTo(road1) > 0);
        assertTrue(road2.compareTo(road1) > 0);
        assertTrue(road2.compareTo(road3) < 0);
        assertEquals(road1.compareTo(road1), 0);
    }


}