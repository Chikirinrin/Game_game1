import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A GUI written in Java Swing which wraps around a Game instance.
 * Is used to get a graphical view of the data in a Game.
 * 
 * @author Nikolaj Ignatieff Schwartzbach
 * @version 1.0.0
 *
 */
public class GUI {

    /** The JPanel where the world are drawn. */
    private WorldPanel panel;
    private JPanel superpanel, buttons;
    
    /** The JFrame, the main GUI. */
    private JFrame mainFrame, options;
    
    /** The JFileChooser object used for I/O (logs) */
    private JFileChooser fileChooser;

    /** More graphical components */
    private JRadioButton slowButton, medButton, fastButton, sonicButton;
    
    /** Check-boxes */
    private JCheckBox random, greedy, smart;
    
    /** Textfields */
    private JTextField tollSizeTextField, muggingTextField;
    
    /** Buttons */
    private JButton optionsButton, pauseResumeButton, abortButton;
    
    /** Reference to the Game instance */
    private Game game;
    
    /** Delay in ms between subsequent frames */
    private int frameDelay = 500;
    
    /** Width and height of the inner window, in pixels */
    public int WIDTH = 520,
               HEIGHT = 635;
    
    /** Main game Timer */
    private Timer timer;
    
    /** Whether or not this game is currently paused */
    private boolean paused = false;
    
    /** Reference to the City which is currently under the mouse */
    public static City hover;
    
    /** The current game speed (0 = stop, 1 = slow, .. ) */
    public static int speed = 2;
    
    /**
     * Constructor for the GUI class.
     * Creates a Game instance autonomously.
     */
    public GUI(){
        //Initialize Game
        game = Generator.generateGame((int)(Math.random()*Integer.MAX_VALUE), "network.dat");
        
        //Initialize buttons
        buttons = createButtonPanel();      
        options = createOptionsDialogBox();
        
        //Initialize ActorPanel
        panel = new WorldPanel(game, WIDTH, HEIGHT);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        //Handle mouse click events in the inner window
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Click on cities
                for(Country country : game.getCountries()){
                    for(City c : country.getCities()){
                        Point p = game.getPosition(c);
                        double dist = Math.hypot(p.getX() - e.getX(), p.getY() - e.getY());
                        if(dist < WorldPanel.MIN_CIRCLE_RADIUS + 5){
                            game.clickCity(c);
                        }
                    }
                }
                
                //Click to change game speed
                if(e.getX()>280 && e.getX()<280+39*4+6 && e.getY()>590 && e.getY()<610){
                    speed = 1+Math.min((e.getX()-280) / 39, 4);
                    mainFrame.repaint();
                    setSpeed(speed);
                }
            }
        });
        
        //Hovering over cities
        panel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent arg0) { }

            @Override
            public void mouseMoved(MouseEvent e){
                
                //Assume not hovering 
                int i=0;
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
                //Hover over cities
                for(Country country : game.getCountries()){
                    for(City c : country.getCities()){
                        Point p = game.getPosition(c);
                        double dist = Math.hypot(p.getX() - e.getX(), p.getY() - e.getY());
                        if(dist < WorldPanel.MIN_CIRCLE_RADIUS + 5){
                            panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            i++;
                            hover = c;
                            break;
                        }
                    }
                }
                if(i==0)
                    hover = null;
                
                //Hovering over game speed
                if(e.getX()>280 && e.getX()<280+39*4+6 && e.getY()>590 && e.getY()<610){
                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                mainFrame.repaint();
            }
            
        });
        
        //Initialize file chooser
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Log files", "log");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        
        //Initialize the super JPanel (which contains the other JPanels)
        superpanel = new JPanel();
        superpanel.setLayout(new BoxLayout(superpanel, BoxLayout.Y_AXIS));
        superpanel.add(panel);
        superpanel.add(buttons);
        
        //Initialize and setup the the JFrame
        mainFrame = new JFrame("Nordic Traveller - Introduktion til Programmering");
        mainFrame.add(superpanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.setContentPane(superpanel);
        mainFrame.setVisible(true);
        
        //Initialize the game timer
        timer = new Timer(frameDelay, e->{
            game.step();
            optionsButton.setEnabled(!game.ongoing());
            pauseResumeButton.setEnabled(game.ongoing());
            mainFrame.repaint();
        });

        //Apply existing settings to current game
        applyExistingSettings();
                
    }
    
    /**
     * Changes the state of the GUI elements
     */
    public void applyExistingSettings(){
        SwingUtilities.invokeLater(() -> {
            //Active players
            random.setSelected(game.getSettings().isActive(0));
            greedy.setSelected(game.getSettings().isActive(1));
            smart.setSelected(game.getSettings().isActive(2));
        
            //Text-fields
            tollSizeTextField.setText(""+game.getSettings().getTollToBePaid());
            muggingTextField.setText(""+game.getSettings().getRisk());
        
            //Game speed
            speed = game.getSettings().getGameSpeed();
            setSpeed(speed);
        });
        
    }
    
    /**
     * Changes the game speed
     * @param speed The new speed of the game. 0 <= speed <= 4
     */
    public void setSpeed(int speed){
        SwingUtilities.invokeLater(() -> {
            //Stop the game timer, and unselect all GUI buttons
            timer.stop();
            slowButton.setSelected(false);
            medButton.setSelected(false);
            fastButton.setSelected(false);
            sonicButton.setSelected(false);
        
            //Change the speed
            switch(speed){
                case 1:
                    slowButton.setSelected(true);
                    timer.setDelay(3000);
                    if(!paused)
                        timer.start();
                    break;
                case 2:
                    medButton.setSelected(true);
                    timer.setDelay(1000);
                    if(!paused)
                        timer.start();
                    break;
                case 3:
                    fastButton.setSelected(true);
                    timer.setDelay(400);
                    if(!paused)
                        timer.start();
                    break;
                case 4:
                    sonicButton.setSelected(true);
                    timer.setDelay(100);
                    if(!paused)
                        timer.start();
                    break;
            }
            game.getSettings().setGameSpeed(speed);
        });
    }
    
    /**
     * Creates the JPanel which contains the buttons in the bottom of the GUI
     * @return A JPanel containing some buttons to control the game
     */
    public JPanel createButtonPanel(){
        //Initialize the JPanel, using a GridLayout
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3));
        
        //Instantiate the 'New'-button
        JButton newButton = new JButton("New game");
        //Connect an ActionListener
        newButton.addActionListener(e -> {game.reset(false, true); mainFrame.repaint();});
        //Add it to the button panel
        buttons.add(newButton);

        //Add the 'Pause game'-button
        pauseResumeButton = new JButton("Pause game");
        //Connect an ActionListener
        pauseResumeButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                paused = !paused;
                if(paused){
                    timer.stop();
                    pauseResumeButton.setText("Resume game");
                } else {
                    timer.start();
                    pauseResumeButton.setText("Pause game");
                }
            });
        });
        //Add it to the button panel
        buttons.add(pauseResumeButton);
        
        //Add the 'Abort game'-button
        abortButton = new JButton("Abort game");
        abortButton.addActionListener(e -> {
            game.abort();
        });
        buttons.add(abortButton);

        //Add the 'Options...'-button
        optionsButton = new JButton("Options...");
        optionsButton.setEnabled(false);
        optionsButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Stop the game timer
                timer.stop(); 
                
                //Hide the main window
                mainFrame.setVisible(false); 
                
                //Show the options window
                options.setVisible(true);
            }
        });
        buttons.add(optionsButton);
        //Return the JPanel
        return buttons;
    }
    
    /**
     * Creates the JFrame which represents the Options...-menu.
     * @return  A JFrame representing the Options...-menu.
     */
    public JFrame createOptionsDialogBox(){
        JPanel superpanel = new JPanel();
        superpanel.setLayout(new BorderLayout());
        
        JPanel options = new JPanel();
        options.setLayout(new BorderLayout());
        
        //Active players
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        
        random = new JCheckBox("Random Player");
        random.setSelected(true);
        playerPanel.add(random);

        greedy = new JCheckBox("Greedy Player");
        greedy.setSelected(true);
        playerPanel.add(greedy);

        smart = new JCheckBox("Smart Player");
        smart.setSelected(true);
        playerPanel.add(smart);
        
        //Text input
        JPanel tollAndRobberyPanel = new JPanel();
        tollAndRobberyPanel.setLayout(new GridLayout(2,3,5,5));                    
        
        //Toll size
        JLabel tollSizeLabel = new JLabel("Toll to be paid:");
        tollAndRobberyPanel.add(tollSizeLabel);
        
        tollSizeTextField = new JTextField("20", 10);
        tollAndRobberyPanel.add(tollSizeTextField);

        JLabel percTollSize = new JLabel("% in [0,50]");
        tollAndRobberyPanel.add(percTollSize);
                
        //Rob risk
        JLabel muggingLabel = new JLabel("Risk of robbery:");                       
        tollAndRobberyPanel.add(muggingLabel);

        muggingTextField = new JTextField("20", 10);
        tollAndRobberyPanel.add(muggingTextField);

        JLabel percMugging = new JLabel("% in [0,50]");
        tollAndRobberyPanel.add(percMugging);
        
        //Speed options
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new FlowLayout());

        slowButton = new JRadioButton("SLOW");
        speedPanel.add(slowButton);

        medButton = new JRadioButton("MED");
        medButton.setSelected(true);
        speedPanel.add(medButton);

        fastButton = new JRadioButton("FAST");
        speedPanel.add(fastButton);
        
        sonicButton = new JRadioButton("SONIC");
        speedPanel.add(sonicButton);
        
        ButtonGroup group = new ButtonGroup();
        group.add(slowButton);
        group.add(medButton);
        group.add(fastButton);
        group.add(sonicButton);
        
        //Add panels to superpanel
        JPanel superPlayerPanel = new JPanel();
        superPlayerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(new EmptyBorder(5,5,5,5));
        superPlayerPanel.setBorder(new TitledBorder("Active automatic players"));
        superPlayerPanel.add(playerPanel, BorderLayout.WEST);
        options.add(superPlayerPanel, BorderLayout.NORTH);

        JPanel superTextPanel = new JPanel();
        superTextPanel.add(tollAndRobberyPanel);
        tollAndRobberyPanel.setBorder(new EmptyBorder(0,5,0,5));
        superTextPanel.setBorder(new TitledBorder("Toll and robbery"));
        options.add(superTextPanel, BorderLayout.CENTER);

        options.add(speedPanel, BorderLayout.SOUTH);
        TitledBorder bSpeed = new TitledBorder("Game speed");
        speedPanel.setBorder(bSpeed);
        
        JButton applyButton = new JButton("Apply changes");
        
        superpanel.add(options, BorderLayout.NORTH);
        superpanel.add(applyButton, BorderLayout.SOUTH);
        
        JFrame frame = new JFrame("(options) Nordic Traveller - Introduktion til Programmering");
        frame.add(superpanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(superpanel);
        frame.pack();

        applyButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                game.reset(false, true);
                
                //Enabled players
                game.getSettings().setActive(0, random.isSelected());
                game.getSettings().setActive(1, greedy.isSelected());
                game.getSettings().setActive(2, smart.isSelected());
                
                //Toll size & mugging
                int tollSize, riskRob;
                try{
                    tollSize = Integer.parseInt(tollSizeTextField.getText());
                    riskRob  = Integer.parseInt(muggingTextField.getText());
                    if(tollSize < 0 || riskRob < 0 || tollSize > 50 || riskRob > 50){
                        JOptionPane.showMessageDialog(frame, "'Toll size' and 'Risk rob' must be between 0 and 50.", "Malformed input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(frame, "'Toll size' and 'Risk rob' must be integers.", "Malformed input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                
                game.getSettings().setRisk(riskRob);
                game.getSettings().setTollToBePaid(tollSize);
                
                frame.setVisible(false);
                mainFrame.setVisible(true);
                mainFrame.repaint();
                
                if(slowButton.isSelected())
                    speed = 1;

                if(medButton.isSelected())
                    speed = 2;

                if(fastButton.isSelected())
                    speed = 3;

                if(sonicButton.isSelected())
                    speed = 4;
                
                setSpeed(speed);
            }
            
        });
        
        return frame;
    }
    
    /**
     * Tests the Save button.
     * This method is invoked when testing the functionality of the Save button.
     */
    private void testSaveButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Save' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tests the Play button.
     * This method is invoked when testing the functionality of the Play button.
     */
    private void testPlayButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Play' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tests the Repeat button.
     * This method is invoked when testing the functionality of the Repeat button.
     */
    private void testRepeatButton(){
        JOptionPane.showMessageDialog(mainFrame, "You have clicked the 'Repeat' button.", "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startGUI(){
        mainFrame.pack();
        mainFrame.repaint();
    }
    
    /**
     * Starts the game.
     */
    public static void createGameBoard() {
        if(!Files.exists(Paths.get("network.dat"))){
            JOptionPane.showMessageDialog(null, "'network.dat' does not exist in the current project. Game closing.", "Unable to start NordicTraveller", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!Files.exists(Paths.get("map.png"))){
            JOptionPane.showMessageDialog(null, "'map.png' does not exist in the current project. Game closing.", "Unable to start NordicTraveller", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new GUI().startGUI();
    }

}

class WorldPanel extends JPanel {
    
    private static final long serialVersionUID = -4313765288063966250L;

    public final static int  MIN_CIRCLE_RADIUS   = 7,
                             ROAD_CIRCLE_RADIUS  = 2,
                             PLAYER_RADIUS = 4;
    
    private final static Color COLOR_BACKGROUND     = new Color(116, 204, 244),
                               COLOR_CITY_STROKE    = Color.BLACK,
                               COLOR_ROAD           = Color.WHITE,
                               COLOR_BORDER_ROAD    = Color.WHITE,
                               COLOR_PLAYER_STROKE  = Color.BLACK,
                               COLOR_TEXT           = Color.BLACK,
                               COLOR_BAR_OUTLINE    = Color.BLACK,
                               COLOR_BAR_TIME_FILL  = Color.BLUE,
                               COLOR_BAR_SPEED_FILL = new Color(211,211,211),
                               COLOR_BAR_BACKGROUND = Color.WHITE;
    
    private final static Font FONT_BODY   = new Font("SansSerif", Font.PLAIN, 12),
                              FONT_HEADER = new Font("SansSerif", Font.BOLD, 16),
                              FONT_SC     = new Font("SansSerif", Font.BOLD, 10);
    
    private final static Stroke STROKE_DEFAULT = new BasicStroke(0.9f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND),
                                STROKE_THICK   = new BasicStroke(1.1f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
    
    private int width, height;
    private Game game;
    
    private BufferedImage img;
    
    public WorldPanel(Game game, int width, int height){
        this.game = game;
        this.width = width;
        this.height = height;
        try {
            this.img = ImageIO.read(new File("map.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        //Clear the screen
        super.paintComponent(g);
        
        //Get the Graphics2D object and enable anti-aliasing
        Graphics2D g2d = (Graphics2D) g;        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(STROKE_DEFAULT);
        
        //Draw white background
        g2d.setColor(COLOR_BACKGROUND);
        g2d.fillRect(0, 0, width, height);
        
        //Draw map
        g2d.drawImage(img, 0, 0, null);

        //Time bar
        //Draw text
        g2d.setColor(COLOR_TEXT);
        g2d.setFont(FONT_BODY);
        g2d.drawString("Time left:", 15, 20);
        g2d.drawString(""+game.getStepsLeft(), 150, 35);
        //Draw bar
        double ticks = game.getStepsLeft() / (double)game.getTotalTimeLeft();
        g2d.setColor(COLOR_BAR_BACKGROUND);
        g2d.fillRect(15, 25, 130, 10);
        g2d.setColor(COLOR_BAR_TIME_FILL);
        g2d.fillRect(15, 25, (int)(130 * ticks), 10);
        g2d.setColor(COLOR_BAR_OUTLINE);
        g2d.drawRect(15, 25, 130, 10);
        
        //Hi-score
        List<Player> players = new ArrayList<Player>(game.getPlayers());
        Collections.sort(players, (p0, p1) -> { return (p1.getMoney() + (p1.getName().equals("GUI Player")?1000:0)) - (p0.getMoney() + (p0.getName().equals("GUI Player")?1000:0)); });
        int i=0;
        for(Player p : players){
            if(!isEnabled(p))
                continue;
            int y = 75+33*i++;
            //Draw text
            g2d.setColor(COLOR_TEXT);
            g2d.setFont(FONT_BODY);
            g2d.drawString(p.getName()+":", 15, y-5);
            g2d.drawString(p.getMoney()+" €", 150, y+10);
            //Draw bar
            double money = p.getMoney() / 1400.0;
            g2d.setColor(COLOR_BAR_BACKGROUND);
            g2d.fillRect(15, y, 130, 10);
            g2d.setColor(p.getColor());
            g2d.fillRect(15, y, (int)(130 * money), 10);
            g2d.setColor(COLOR_BAR_OUTLINE);
            g2d.drawRect(15, y, 130, 10);
        }
        
        //Speed
        g2d.setFont(FONT_HEADER);
        g2d.drawString("Game speed", 280, 580);
        g2d.setColor(COLOR_BAR_BACKGROUND);
        g2d.fillRect(280, 590, 160, 20);

        g2d.setColor(COLOR_BAR_SPEED_FILL);
        g2d.fillRect(280+(GUI.speed-1)*39, 590, 39+(GUI.speed==4?6:0), 20);
        
        g2d.setColor(COLOR_BAR_OUTLINE);
        g2d.drawRect(280, 590, 160, 20);
        g2d.setFont(FONT_SC);
        g2d.drawString("SLOW", 286, 605);
        g2d.drawRect(280, 590, 39, 20);
        g2d.drawString("MED", 328, 605);
        g2d.drawRect(280, 590, 39*2, 20);
        g2d.drawString("FAST", 366, 605);
        g2d.drawRect(280, 590, 39*3, 20);
        g2d.drawString("SONIC", 403, 605);
        
        //City info
        if(GUI.hover != null){
            g2d.setFont(FONT_SC);
            if(GUI.hover.getClass() == CapitalCity.class)
                g2d.drawString("Capital of "+GUI.hover.getCountry().getName().toUpperCase(), 350, 470);
            else
                g2d.drawString(GUI.hover.getCountry().getName().toUpperCase(), 350, 470);
            g2d.setFont(FONT_BODY);
            g2d.drawString(GUI.hover.getValue()+" €", 350, 486);
            g2d.setFont(FONT_HEADER);
            g2d.drawString(GUI.hover.getName(), 350, 458);
        }
        g2d.setStroke(STROKE_THICK);
        for(Country country : game.getCountries()){
            //First draw all roads
            for(City city : country.getCities()){
                for(Road road : country.getRoads(city)){
                    drawRoad(g2d, road);
                }
            }
        }

        for(Country country : game.getCountries()){
            //Then draw all cities
            for(City city : country.getCities()){
                drawCity(g2d, city);
            }
        }

        g2d.setStroke(STROKE_DEFAULT);
        //Draw all players
        for(Player player : game.getPlayers()){
            drawPlayer(g2d, player, true);
        }
    }
        
    private Point getPosition(City c){
        return game.getPosition(c);
    }

    private void drawRoad(Graphics2D g2d, Road r){
        if(r.getFrom().getName().compareTo(r.getTo().getName()) > 0) return;
        Point posFrom = getPosition(r.getFrom()),
              posTo = getPosition(r.getTo());
        
        g2d.setColor(COLOR_ROAD);
        if(!r.getFrom().getCountry().equals(r.getTo().getCountry()))
            g2d.setColor(COLOR_BORDER_ROAD);
        g2d.drawLine(posFrom.x, posFrom.y, posTo.x, posTo.y);
        
        for(int i=0; i<r.getLength(); i++)
            drawRoadDot(g2d, r, i);
    }
    
    private boolean isEnabled(Player p){
        if(p.getClass() == RandomPlayer.class)
            return game.getSettings().isActive(0);
        if(p.getClass() == GreedyPlayer.class)
            return game.getSettings().isActive(1);
        if(p.getClass() == SmartPlayer.class)
            return game.getSettings().isActive(2);
        return true;
    }
    
    private void drawPlayer(Graphics2D g2d, Player p, boolean ai){
        if(!isEnabled(p))
            return;
        
        Point from = getPosition(p.getPosition().getFrom()),
              to   = getPosition(p.getPosition().getTo());
        
        double f = (p.getPosition().getTotal()-p.getPosition().getDistance())/(double)p.getPosition().getTotal();
        int x = from.x + (int)(f * (to.x - from.x)),
            y = from.y + (int)(f * (to.y - from.y));

        Shape shape = new Ellipse2D.Double(x - PLAYER_RADIUS, y - PLAYER_RADIUS, 2*PLAYER_RADIUS, 2*PLAYER_RADIUS);
        g2d.setColor(p.getColor());
        g2d.fill(shape);
        g2d.setColor(COLOR_PLAYER_STROKE);
        g2d.draw(shape);
    }
    
    private void drawRoadDot(Graphics2D g2d, Road r, int i){
        Point from = getPosition(r.getFrom()),
              to = getPosition(r.getTo());
        
        double f = (i)/(double)r.getLength();
        int x = from.x + (int)Math.round(f * (to.x - from.x)),
            y = from.y + (int)Math.round(f * (to.y - from.y));

        int radius = ROAD_CIRCLE_RADIUS;
        Ellipse2D.Double shape = new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
        g2d.setColor(COLOR_ROAD);
        if(!r.getFrom().getCountry().equals(r.getTo().getCountry()))
            g2d.setColor(COLOR_BORDER_ROAD);
        g2d.fill(shape);
    }
    
    private int makeLegal(int col){
        if(col<0)
            return 0;
        if(col>255)
            return 255;
        return col;
    }
    
    private Color cityColor(double x){
        Color from = Color.WHITE, to = Color.WHITE;
        if(x < 0.33)
            to = Color.RED;
        if(0.33 <= x && x < 0.66)
            to = Color.YELLOW;
        if(0.66 <= x)
            to = Color.GREEN;
        
        int r = from.getRed()   + (int)(x*(to.getRed()   - from.getRed())),
            g = from.getGreen() + (int)(x*(to.getGreen() - from.getGreen())),
            b = from.getBlue()  + (int)(x*(to.getBlue()  - from.getBlue()));
        
        
            
        return new Color(makeLegal(r),makeLegal(g),makeLegal(b));
    }
    
    private void drawCity(Graphics2D g2d, City c){
        Point pos = getPosition(c);
        int radius = MIN_CIRCLE_RADIUS;
        if(c.equals(GUI.hover))
            radius=radius + 2;
        
        if(c.getClass() == CapitalCity.class){
            radius = radius+3;
        }
        
        Ellipse2D.Double shape = new Ellipse2D.Double(pos.x - radius, pos.y - radius, 2*radius, 2*radius);
        double val = Math.pow(c.getValue() / 250.0, 1.0);
        Color col = cityColor(val);
        g2d.setColor(col);
        g2d.fill(shape);
        g2d.setColor(COLOR_CITY_STROKE);
        g2d.draw(shape);
    }
}