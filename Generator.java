import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates an instance of NordicTraveller based on a String representation of the internal network.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class Generator {

    public static Game generateGame(int seed, String filename){
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(filename)), "windows-1252");
        } catch (IOException e) {
            return null;
        }
        return generateGame(seed, text.replace("\r", "").split("\n"));
    }
    
    private static Game generateGame(int seed, String[] data){
        Game g = new Game(seed);
        
        Country country = null;
        Map<City, List<Road>> network = null;
        boolean roads = false;
        for(String line : data){
            if(line.startsWith("\t") && roads){
                if(!line.contains(","))continue;
                String[] road = line.substring(1).replace(" ","").split(",");
                if(road.length!=3)
                    throw new NetworkParseException("Invalid argument count on line '"+line+"'. Expected 3, received "+road.length+".");
                g.addRoads(road[0], road[1], Integer.parseInt(road[2]));
                
            } else if(line.startsWith("\t")){
                if(country == null) throw new NetworkParseException("No country defined.");
                String[] city = line.substring(1).replace(" ","").split(",");
                int value = Integer.parseInt(city[1]);
                City c;
                if(city[0].startsWith("#"))
                    c = new CapitalCity(city[0].substring(1), value, country);
                else if(city[0].startsWith("|"))
                    c = new BorderCity(city[0].substring(1), value, country);
                else
                    c = new City(city[0].replace("#", "").replace("|", ""), value, country);
                network.put(c, new ArrayList<Road>());
                int x = Integer.parseInt(city[2]), y = Integer.parseInt(city[3]);
                g.putPosition(c, new Point(x,y));
            } else {
                if(country!=null)
                    g.addCountry(country);
                if(line.equals("Roads")){
                    roads = true;
                    continue;
                }
                network = new HashMap<City, List<Road>>();
                if(line.equalsIgnoreCase("sweden"))
                    country = new MafiaCountry(line, network);
                else
                    country = new Country(line, network);
            }
        }

        g.getPlayers().add(new SmartPlayer(g.getRandomStartingPosition()));
        g.getPlayers().add(new GreedyPlayer(g.getRandomStartingPosition()));
        g.getPlayers().add(new RandomPlayer(g.getRandomStartingPosition()));
    
        Player p = new Player(g.getRandomStartingPosition());
        g.setGUIPlayer(p);
        
        g.reset(false, false);
        return g;
    }
    
}

class NetworkParseException extends RuntimeException {

    public NetworkParseException(String string) {
        super(string);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -919893285667013374L;
    
}
