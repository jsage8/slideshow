import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.*;

// @author Jonathan Sage
// @phone 206-498-5442
// @email jsage8@gmail.com

public class SlideShow extends JFrame {
    //screenImage will be replaced with each new slide
    private BufferedImage screenImage;
    private int width;
    private int height;
    
    //For writing to file
    private PrintWriter outputStream;
    
    //Create panel for displaying images using paintComponent()
    private PaintPanel mainImagePanel;
    
    private MasterListener masterListener;
    private Timer timer;
    private TimeListener timeListener;
    
    //Used for keybinding
    private Action enterAction;
//    private Action repeatAction;
    private Action escapeAction;
    private static final String enter = "ENTER";
//    private static final String repeat = "R";
    private static final String escape = "ESCAPE";

    private boolean inBegin = false;
    
    //Set to true during interims between trick repititions
    //Used to help pre-load the repeated trick
    private boolean inInterim = false;
    
    //If true then "ENTER" key must be pressed to advance
    //During handcuff transition only "R" key will repeat handcuff slides
    private boolean inTransition = false;
    
    private boolean isPaused = false;
    
    
    /******************************************
     * order is used by the MouseListener class Mouse to determine what order
     * the slides are presented in.
     * subjectNumber is used by the method writeFile to determine which file
     * information is written to.
     * experiment will store all of the information from the instance of
     * SelfPaceMain that created this instance of SlideShow.
     * order and subjectNumber are set using that information.
     ******************************************/
    final SelfPaceMain experiment;
    private String subjectNumber;
    private TrickSettings[] trickSettings;
    private TrickSettings[] interimSettings;
    private Mouse mouse;

    //Used by timeStamp() for documenting time per slide
    private long time0 = 0;
    private long time1;
    
    public SlideShow(SelfPaceMain e) {
        /******************************************
         * Pass entire class SelfPaceMain to SlideShow.
         * This allows access to:
         * JTextField information
         *     subjectNumber
         * TrickSettings array with methods to retrieve trick information 
         *         getTrick()
         *         getPace()
         *         getDuration()
         *         getFile()
         ******************************************/
        
        experiment = e;
        subjectNumber = experiment.getSubjectNumber();
        trickSettings = experiment.getTrickSettings();
        
        interimSettings = new TrickSettings[3];
        interimSettings[0] = new TrickSettings("begin", 1);
        interimSettings[1] = new TrickSettings("interim", 2);
        interimSettings[2] = new TrickSettings("transition", 1);

        // Write to File
        outputStream = writeFile(subjectNumber);
        
        try
        {
            screenImage = ImageIO.read(new File("images/begin1.jpg"));
        }
        catch (IOException nm) 
        {
            System.out.println("begin");
            System.out.println(nm.getMessage());
            outputStream.close();
            System.exit(0);
        }
                    
        //Create panel so that I can use key binding which requires JComponent
        mainImagePanel = new PaintPanel();      
        add(mainImagePanel);
                
        /****************************************** 
         * Key Binding
         * The code below is used to make 3 different key binds
         * ENTER will proceed past the transition[0] slide into the next trick
         * R will repeat the handcuff trick if at transition[0] slide
         * ESC will exit the slideshow
         ******************************************/
        
        // Key bound AbstractAction items 
        enterAction = new EnterAction();
//        repeatAction = new RepeatAction();
        escapeAction = new EscapeAction();
 
        // Gets the mainImagePanel InputMap and pairs the key to the action
        mainImagePanel.getInputMap().put(KeyStroke.getKeyStroke(enter), "doEnterAction");
//        mainImagePanel.getInputMap().put(KeyStroke.getKeyStroke(repeat), "doRepeatAction");
        mainImagePanel.getInputMap().put(KeyStroke.getKeyStroke(escape), "doEscapeAction");
        
        // This line pairs the AbstractAction enterAction to the action "doEnterAction"
        mainImagePanel.getActionMap().put("doEnterAction", enterAction);
//        mainImagePanel.getActionMap().put("doRepeatAction", repeatAction);
        mainImagePanel.getActionMap().put("doEscapeAction", escapeAction);
        
        masterListener = new MasterListener();
        masterListener.newListener();
        
        /******************************************
         * End Key Binding
         ******************************************/
    }
    
    public void display() {
        /****************************************** 
         * Fullscreen Exclusive Mode
         * http://stackoverflow.com/questions/7456227
         ******************************************/ 
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice dev = env.getDefaultScreenDevice();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.darkGray);
        this.setResizable(false);
        this.setIgnoreRepaint(true);
        this.setUndecorated(true);
        this.pack();
        dev.setFullScreenWindow(this);
        width = this.getWidth();
        height = this.getHeight();
        /******************************************
         * End Fullscreen Exclusive Mode
         ******************************************/
        
        /****************************************** 
         * Mouse Custom Transparent Cursor
         * http://stackoverflow.com/questions/1984071
         ******************************************/       
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        cursorImg, new Point(0, 0), "blank cursor");
        // Set the blank cursor to the JFrame.
        this.setCursor(blankCursor);
        /******************************************
         * End Custom Cursor
         ******************************************/
    }
    
    //This method executes a specific SwingWorker class to preload images
    public void start(TrickSettings trickSettings, int currentIndex, boolean isInitial) {
        new ImageWorker(trickSettings, currentIndex, isInitial).execute();
    }
    
    private PrintWriter writeFile(String subjectNumber) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(subjectNumber + ".txt", true));
        }
        catch(FileNotFoundException e) {
            System.out.println("Cannot find file " + subjectNumber + ".txt or it could not be opened.");
            System.exit(0);
        }
        finally {
            return out;
        }
    }

    //Stretches and displays images in fullscreen window
    private class PaintPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) { 
            if(screenImage != null) {
                super.paintComponent(g);
                g.drawImage(screenImage, 0, 0, width, height, this);
            }  
        }
    }
    
    //Prints out time spent on each slide
    public void timeStamp() {
        time1 = System.currentTimeMillis();
        if(time0 != 0) {
            outputStream.println(time1 - time0);
        }
        time0 = System.currentTimeMillis();
    }
       
    /******************************************
     * User Input
     * User Input Classes for Key Binding Actions and Mouse Click Actions
     ******************************************/
    private class EnterAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(inTransition && trickSettings[trickSettings.length - 1].getIsDone()) {
                timeStamp();
                outputStream.println("Program Finished Successfully");
                outputStream.close();
                System.exit(0);
            }
            else if(inTransition) {
                timeStamp();
                inTransition = false;
                masterListener.newListener();
            }
        }
    }
      
    private class EscapeAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            timeStamp();
            outputStream.println("Program Terminated by ESC Key");
            outputStream.close();
            System.exit(0);
        }
    }
        
    private class MasterListener {
        public void newListener() {
            for(int i = 0; i < trickSettings.length; i++) {
                if(!trickSettings[i].getIsDone()) {
                    String pace = trickSettings[i].getPace();
                    outputStream.println("\nTrick Number:\t" + (i + 1));
                    outputStream.println("Trick Name:\t" + trickSettings[i].getTrick());
                    outputStream.println("Trick Pace:\t" + trickSettings[i].getPace());
                    if(pace.equals("Set Pace") || pace.equals("Set Pace Free Pause") || pace.equals("Set Pace Subgoal Pause") || pace.equals("Set Pace Timed Pause")) {
                        outputStream.println("Slide Duration:\t" + trickSettings[i].getDuration());
                        if(pace.equals("Set Pace Timed Pause")) {
                            outputStream.println("Slides Between Pause:\t" + trickSettings[i].getSlidePause());
                        }
                    }
                    if(pace.equals("Yoked Pace")) {
                        outputStream.println("Yoked File:\t" + trickSettings[i].getFile());
                    }
                    start(trickSettings[i], 0, true);
                    start(interimSettings[0], 0, true);
                    inBegin = true;
                    isPaused = false;
                    time0 = 0;
                    mouse = new Mouse(trickSettings[i], interimSettings);
                    timeListener = new TimeListener(trickSettings[i], interimSettings);
                    addMouseListener(mouse);
                    
                    if(pace.equals("Set Pace") || pace.equals("Set Pace Free Pause") || pace.equals("Set Pace Subgoal Pause") || pace.equals("Set Pace Timed Pause")) {
                        timer = new Timer(trickSettings[i].getDuration(), timeListener);
                        timer.start();
                    }
                    break;
                } 
            }
        }
    }
    
    /******************************************
     * Timed Pace Control Structure
     * Experimenter inputs slide duration in milliseconds
     * TimeListener is called every x milliseconds and simulates mouse clicking
     ******************************************/
    private class TimeListener implements ActionListener {
        private TrickSettings trickSettings;
        private TrickSettings[] interimSettings;
        private String trick;
        private int size;
        private String pace;
        private int slidePause;
        private int[] subgoals;
        private int timesSeen;
        private int index;
        private int beginIndex;
        private int interimIndex;
        private int transitionIndex;
        
        public TimeListener(TrickSettings trickSettings, TrickSettings[] interimSettings) {
            this.trickSettings = trickSettings;
            this.interimSettings = interimSettings;
            this.trick = trickSettings.getTrick();
            this.size = trickSettings.getSize();
            this.pace = trickSettings.getPace();
            this.slidePause = trickSettings.getSlidePause();
            this.subgoals = trickSettings.getSubgoals();
            this.timesSeen = 0;
            this.index = 0;
            this.beginIndex = 0;
            this.interimIndex = 0;
            this.transitionIndex = 0;
        }
        
        public void displayTrick() {
            timeStamp();
            screenImage = trickSettings.getImage(index);
            repaint();
            outputStream.print(trick + "[" + (index + 1) + "]\t");
            index++;

            if(pace.equals("Set Pace Timed Pause") && index % slidePause == 0) {
                isPaused = true;
                timer.stop();
            }
            
            if(pace.equals("Set Pace Subgoal Pause")) {
                for(int i = 0; i < subgoals.length && subgoals[i] <= index; i++) {
                    if(subgoals[i] == index) {
                        isPaused = true;
                        timer.stop();
                    }
                }
            }
            
            //purge used slides and refresh slide buffer
            if(index < size - 5 && index % 10 == 0) {
                for(int i = 0; i < index; i++) {
                    trickSettings.setImage(i, null);
                }
                start(trickSettings, index + 10, false);
            }
            
            //refresh start of trick if near end of first run
            if(index == size - 5 && timesSeen == 0) {
                start(trickSettings, 0, true);
                start(interimSettings[1], 0, true);
            }
            else if(index == size - 5 && timesSeen == 1) {
                start(interimSettings[0], 0, true);
                start(interimSettings[2], 0, true);
            }
            //end of first run enter interim slides
            else if(index >= size && timesSeen == 0) {
                inInterim = true;
            }
            //end of second run enter transition
            else if(index >= size && timesSeen == 1) {
                inTransition = true;
            }
        }
        
        public void displayBegin() {
            if(pace.equals("Set Pace") || pace.equals("Set Pace Free Pause") || pace.equals("Set Pace Subgoal Pause") || pace.equals("Set Pace Timed Pause")) {
                isPaused = true;
                timer.stop();
            }
            timeStamp();
            screenImage = interimSettings[0].getImage(beginIndex);
            repaint();
            outputStream.print("begin[" + (beginIndex + 1) + "]\t");
        }

        public void displayInterim() {
            if(interimIndex == 0) {
                timesSeen++;
                index = 0;
            }

            timeStamp();
            screenImage = interimSettings[1].getImage(interimIndex);
            repaint();
            outputStream.print("interim[" + (interimIndex + 1) + "]\t");
            interimIndex++;

            if(interimIndex >= interimSettings[1].getSize()) {
                inInterim = false;
            }
        }

        public void displayTransition() {
            timeStamp();
            screenImage = interimSettings[2].getImage(transitionIndex);
            repaint();
            outputStream.print("transition[" + (transitionIndex + 1) + "]\t");

            //Purge used slides
            trickSettings.setImages(null);
            //Set trick to done
            trickSettings.setIsDone(true);
            //Remove mouse listener
            removeMouseListener(mouse);
            //Stop timer
            if(pace.equals("Set Pace") || pace.equals("Set Pace Free Pause") || pace.equals("Set Pace Subgoal Pause") || pace.equals("Set Pace Timed Pause")) {
                timer.stop();
            }
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if(inTransition) {
                displayTransition();
            }
            else if(inInterim) {
                displayInterim();
            }
            else if(inBegin) {
                displayBegin();
            }
            else {
                displayTrick();
            }
        }
    }
    /******************************************
     * End Timed Pace Control Structure
     ******************************************/
    
    private class Mouse extends MouseAdapter
    {
        private String pace;
        
        public Mouse(TrickSettings trickSettings, TrickSettings[] interimSettings) {
            this.pace = trickSettings.getPace();
        }
                
        @Override
        public void mouseClicked(MouseEvent e) 
        {
            if(pace.equals("Set Pace") || pace.equals("Set Pace Free Pause") || pace.equals("Set Pace Subgoal Pause") || pace.equals("Set Pace Timed Pause")) {
                if(inBegin) {
                    inBegin = false;
                    isPaused = false;
                    timeListener.actionPerformed(new ActionEvent(timeListener, ActionEvent.ACTION_PERFORMED, "restart"));
                    timer.restart();
                }
                else if(isPaused) {
                    if(pace.equals("Set Pace Free Pause")) {
                        isPaused = false;
                        timeListener.actionPerformed(new ActionEvent(timeListener, ActionEvent.ACTION_PERFORMED, "restart"));
                        timer.restart();
                    }
                    else if(pace.equals("Set Pace Timed Pause")) {
                        isPaused = false;
                        timeListener.actionPerformed(new ActionEvent(timeListener, ActionEvent.ACTION_PERFORMED, "restart"));
                        timer.restart();
                    }
                    else if(pace.equals("Set Pace Subgoal Pause")) {
                        isPaused = false;
                        timeListener.actionPerformed(new ActionEvent(timeListener, ActionEvent.ACTION_PERFORMED, "restart"));
                        timer.restart();
                    }
                }
                else if(!isPaused) {
                    if(pace.equals("Set Pace Free Pause")) {
                        isPaused = true;
                        timer.stop();
                    }
                }
            }
            else if(pace.equals("Self Pace")) {
                inBegin = false;
                isPaused = false;
                timeListener.actionPerformed(new ActionEvent(timeListener, ActionEvent.ACTION_PERFORMED, "clicked"));
            }
        }
    }
}