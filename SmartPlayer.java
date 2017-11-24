import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;

/**
 * A smart AI player which uses a depth-first search to determine the optimal path.
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class SmartPlayer extends Player {

    /**
     * Instantiates a new SmartPlayer with the specified position.
     * @param pos The position of this player.
     */
    public SmartPlayer(Position pos) {
        super(pos);
    }
    
    @Override
    public void step(){
        super.step();
        if(getPosition().hasArrived()){
            City city = getPosition().getTo();
            setPosition(getCountry().readyToTravel(city, maximizeValue(city, getCountry().getGame().getStepsLeft())));
        } 
    }
    
    private City maximizeValue(City c, int n){
        HashMap<City, Integer> visited = new HashMap<City, Integer>();
        visited.put(c, 1);
        Path best = maximizeValue(visited, c, n);
        if(best.isEmpty())
            return c;
        return best.getRoad().getTo();
    }
    
    private static final int MAX_DEPTH = 26;
    private Path maximizeValue(HashMap<City, Integer> visits, City c, int i){
        int n = i;
        if(i>MAX_DEPTH)n=MAX_DEPTH;
        Path p = new Path(this);
        for(Road r : c.getCountry().getRoads(c)){
            if(r.getLength() <= n){
                HashMap<City, Integer> newVisits = new HashMap<City, Integer>(visits);
                City to = r.getTo();
                int v = 0;
                if(newVisits.containsKey(to))
                    v=newVisits.get(to);
                newVisits.put(to, ++v);
                
                Path subPath = maximizeValue(newVisits, to, n - r.getLength());
                subPath.addRoad(r, v);
                double newValue = subPath.getValue();
                if(subPath.compareTo(p) == 1){
                    p = subPath;
                }
            }
        }
        
        return p;
    }

    @Override
    public String getName(){
        return "Smart Player";
    }
    
    @Override
    public Color getColor(){
        return Color.CYAN;
    }
}

class Path {

    private Stack<Road> edges;
    private int length;
    private double value;
    private double impulsiveness = 1.10;
    
    private SmartPlayer source;
    
    public Path(SmartPlayer source){
        this.source = source;
        edges = new Stack<Road>();
        length = 0;
        value = 0;
    }
    public int compareTo(Path p){
        if(value > p.value)
            return 1;
        if(value < p.value)
            return -1;
        if(length < p.length)
            return 1;
        if(length > p.length)
            return -1;
        return edges.peek().compareTo(p.edges.peek());
    }
    
    public Road getRoad(){
        return edges.peek();
    }
    
    public int getLength(){
        return length;
    }
    
    public double getValue(){
        return value;
    }
    
    public boolean isEmpty(){
        return edges.isEmpty();
    }
    
    @Override
    public String toString(){
        if(edges.isEmpty())return "[]";
        StringBuilder sb = new StringBuilder("[");
        for(Road r : edges)
            sb.append(r + ", ");
        String s = sb.toString();
        return s.substring(0,s.length()-2)+"]";
    }
    
    public void addRoad(Road r, int penalty){
        if(!edges.isEmpty()){
            Road top = getRoad();
            if(!top.getFrom().equals(r.getTo()))
                throw new RuntimeException("Invalid road. You tried to add road to "+r.getTo()+", but the next city is "+top.getFrom());
        } else {
            value += valueFrom(r, penalty);
        }
        edges.add(r);
        length += r.getLength();
        
        
        value+=valueTo(r, penalty);
    }
    
    public double valueFrom(Road r, int penalty){
        Settings s = r.getFrom().getCountry().getGame().getSettings();
        double v = r.getFrom().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));//Check if we have to pay toll
        
        //Check if we visit a capital
        if(r.getFrom() instanceof CapitalCity)
            v -= value/2;
        
        if(r.getFrom().getCountry() instanceof MafiaCountry)
            v -= s.getRisk() / 100.0 * (s.getMinRobbery() + s.getMaxRobbery())/2;
        
        return v;
    }
    
    public double valueTo(Road r, int penalty){
        Settings s = r.getFrom().getCountry().getGame().getSettings();
        double v = r.getTo().getValue() / (Math.pow(2,penalty-1) * Math.pow(impulsiveness, edges.size()));
        
        //Check if we have to pay toll
        if(r.getTo() instanceof BorderCity && !r.getFrom().getCountry().equals(r.getTo().getCountry()))
            v -= source.getMoney() * s.getTollToBePaid() / 100.0;
        
        //Check if we visit a capital
        if(r.getTo() instanceof CapitalCity)
            v -= source.getMoney()/2;
        
        if(r.getTo().getCountry() instanceof MafiaCountry)
            v -= s.getRisk() / 100.0 * (s.getMinRobbery() + s.getMaxRobbery())/2;
        
        return v;
    }
}