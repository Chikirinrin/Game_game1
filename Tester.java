import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.PrintWriter;

public class Tester
{
    public static void main(String[] args){
        try{
            PrintWriter pw = new PrintWriter("test-result.dat","windows-1252");
            if(performTest()){
                pw.write("true");
                pw.close();
                return;
            }
            pw.write("false");
            pw.close();
        } catch(IOException e){}
    }

    /**
     * Tests whether or not this computer game (probably) works.
     * Runs a game with a predetermined seed, and checks if all values match with the expected values.
     * Will print errors to System.out.
     * @return Returns true if the implementation passes the test, and false otherwise.
     */
    public static boolean performTest() throws IOException {
        
        if(!Files.exists(Paths.get("network.dat"))) {
            System.out.println("Cannot perform test without 'network.dat'. Aborting...");
            return false;
        }
        
        if(!Files.exists(Paths.get("test.dat"))) {
            System.out.println("Cannot perform test without 'test.dat'. Aborting...");
            return false;
        }
        
        Game g = Generator.generateGame(0, "network.dat");

        boolean cities = false;
        int t = 0;
        int p = 0;
        Class<?> playerClass = null;
        
        for(String line : new String(Files.readAllBytes(Paths.get("test.dat")), "windows-1252").split("\r\n")) {
            if(line.startsWith("t=") && !line.startsWith("t=0")) {
                g.step();
                t++;
                cities=false;
                p=0;
                continue;
            }
            
            if(line.startsWith("\tcities")) {
                cities = true;
                continue;
            }
            if(line.startsWith("\t\t") && !cities) {
                Player player = null;
                for(Player pl : g.getPlayers()){
                    if(pl.getClass() == playerClass) {
                        player = pl;
                        break;
                    }
                }
                String l = line.substring("\t\t".length());
                switch(p++) {
                    case 0:
                        if(player.getMoney() != Integer.parseInt(l)){
                            System.out.println("Error at time "+t+": The player "+player.getName()+" does not have the expected amount of money.\n\tExpected: "+l+"€\n\tReceived: "+player.getMoney()+"€");
                            return false;
                        }
                        break;
                        
                    case 1:
                        if(player.getPosition().getDistance() != Integer.parseInt(l)){
                            System.out.println("Error at time "+t+": The player "+player.getName()+" does not have the expected remaining number of steps.\n\tExpected: "+l+"\n\tReceived: "+player.getPosition().getDistance());
                            return false;
                        }
                        break;
                        
                    case 2:
                        if(!player.getPosition().getFrom().getName().equals(l)){
                            System.out.println("Error at time "+t+": The player "+player.getName()+" does not have the expected from city.\n\tExpected: "+l+"\n\tReceived: "+player.getPosition().getFrom().getName());
                            return false;
                        }
                        break;
                        
                    case 3:
                        if(!player.getPosition().getTo().getName().equals(l)){
                            System.out.println("Error at time "+t+": The player "+player.getName()+" does not have the expected to city.\n\tExpected: "+l+"\n\tReceived: "+player.getPosition().getTo().getName());
                            return false;
                        }
                        break;
                }
                continue;
            }
            if(line.startsWith("\t") && !cities){
                playerClass = Player.class;
                if(line.startsWith("\tSmart")){
                    playerClass = SmartPlayer.class;
                }
                if(line.startsWith("\tGreedy")){
                    playerClass = GreedyPlayer.class;
                }
                if(line.startsWith("\tRandom")){
                    playerClass = RandomPlayer.class;
                }
                continue;
            }
            if(cities) {
                String[] del = line.substring("\t\t".length()).split("\t");
                int goal = Integer.parseInt(del[1]);
                if(g.getCity(del[0]).getValue() != goal) {
                    System.out.println("Error at time "+t+": The city "+del[0]+" does not have the expected value.\n\tExpected: "+goal+"€\n\tReceived: "+g.getCity(del[0]).getValue()+"€");
                    return false;
                }
                continue;
            }
        }
        
        System.out.println("Test successful!");        
        return true;
    }
}
