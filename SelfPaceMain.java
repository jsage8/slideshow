import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

// @author Jonathan Sage
// @phone 206-498-5442
// @email jsage8@gmail.com

public final class SelfPaceMain extends JFrame {
    private static final int FRAME_WIDTH_ERROR = 300;
    private static final int FRAME_HEIGHT_ERROR = 100;
    private static final int FRAME_WIDTH_ABOUT = 300;
    private static final int FRAME_HEIGHT_ABOUT = 200;

    //Declare GUI JComponents that require further interaction
    private JPanel mainPanel;
    
    private JPanel basicInfoPanel;
    private JPanel trickPacePanel;
    private JPanel submitPanel;
    
    private JPanel explanationPanel;
    private JMenuItem overwriteChoice;
    private JMenuItem howToChoice;

    private JLabel explanationPaceLabel;
    private JLabel explanationOrderLabel;
    private JMenuBar bar;

    //For writing to file
    private PrintWriter outputStream = null;

    //Declare text fields for taking experiment information
    private JTextField subjectNumberText;
    private JTextField subjectAgeText;
    private JTextField experimentInitialsText;
    private JTextField experimentDateText;
    
    //Declare dropdown menu fields for taking experiment information
    private JComboBox sexComboBox;
    private JComboBox trickNumberComboBox;
    
    //Declare pace and trick option arrays
    private String[] paceArray = {"Set Pace", "Set Pace Free Pause", "Set Pace Subgoal Pause", "Set Pace Timed Pause", "Yoked Pace", "Self Pace"};
    private String[] trickArray = {"Card Coin", "Cups", "Rings", "String"};
    private int[] handcuffsSubgoals = {18, 28, 36};
    private int[] cardSubgoals = {25, 81, 123, 149, 161};
    private int[] cupsSubgoals = {14, 44, 74, 97, 114, 116 };
    private int[] stringSubgoals = {59, 73, 83, 119, 130, 155};
    private int[] ringsSubgoals = {21, 65, 82, 98, 113, 146};
            
    //Declare button to start slideshow
    private JButton launch;

    //Declare actions used for keybinding pressing 'ENTER' will act
    //like clicking the launch button.
    private Action enterAction;
    
    //Declare strings for storing experiment information
    private String subjectNumberString;
    private String subjectAgeString;
    private String subjectSexString;
    private String experimentInitialsString;
    private String experimentDateString;
    
    //Declare array for storing experiment information
    private TrickSettings[] experimentTrickSettings;
    
    private int trickNumber;
    private JComboBox[] trickInputArray;
    private JComboBox[] paceInputArray;
    private JPanel[] pacePanelArray;
    private JComponent[][] paceOptionsArray;
    private JComponent[][] pauseOptionsArray;
    
    //If true the help display is visible
    private boolean help = false;
    
    //Turning on overwrite permission will set this to true
    private boolean overwrite = false;
    
    FrameListener passer;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SelfPaceMain gui = new SelfPaceMain();
                gui.setVisible(true);
            }
        });
    }

    public SelfPaceMain() {
        super("Paced Slideshow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        //Menu Bar
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        
        overwriteChoice = new JMenuItem("Enter Overwrite Mode");
        overwriteChoice.setActionCommand("Overwrite");
        overwriteChoice.addActionListener(new MenuListener());
        fileMenu.add(overwriteChoice);
        
        JMenuItem clearChoice = new JMenuItem("Clear Fields");
        clearChoice.addActionListener(new MenuListener());
        fileMenu.add(clearChoice);

        fileMenu.addSeparator();
        
        JMenuItem exitChoice = new JMenuItem("Exit");
        exitChoice.addActionListener(new MenuListener());
        fileMenu.add(exitChoice);

        howToChoice = new JMenuItem("How To Enter Information");
        howToChoice.setActionCommand("howTo");
        howToChoice.addActionListener(new MenuListener());
        helpMenu.add(howToChoice);

        helpMenu.addSeparator();
        
        JMenuItem aboutChoice = new JMenuItem("About This Program");
        aboutChoice.setActionCommand("About");
        aboutChoice.addActionListener(new MenuListener());
        helpMenu.add(aboutChoice);

        bar = new JMenuBar();
        bar.add(fileMenu);
        bar.add(helpMenu);
        setJMenuBar(bar);
        //End Menu Bar

        //Begin Main Content Panel
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.setBackground(new Color(218, 218, 218));
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        basicInfoPanel = new JPanel();
        basicInfoPanel.setBorder(new EmptyBorder(10,0,10,0));
        basicInfoPanel.setBackground(Color.WHITE);
        basicInfoPanel.setLayout(new BorderLayout());
        mainPanel.add(basicInfoPanel, BorderLayout.NORTH);
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(6, 1, 10, 10));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setBorder(new EmptyBorder(0,5,0,5));
        Dimension d = labelPanel.getPreferredSize();
        d.width = 150;
        labelPanel.setPreferredSize(d);
        basicInfoPanel.add(labelPanel, BorderLayout.WEST);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0,5,0,5));
        basicInfoPanel.add(formPanel, BorderLayout.CENTER);
        
        trickPacePanel = new JPanel();
        trickPacePanel.setBorder(new EmptyBorder(5,5,5,5));
        trickPacePanel.setLayout(new BorderLayout());
        trickPacePanel.setBackground(Color.WHITE);
        trickPacePanel.setSize(480, 100);
        mainPanel.add(trickPacePanel, BorderLayout.CENTER);
        
        submitPanel = new JPanel();
        submitPanel.setBorder(new EmptyBorder(10,10,10,10));
        submitPanel.setLayout(new BorderLayout());
        submitPanel.setBackground(Color.WHITE);
        mainPanel.add(submitPanel, BorderLayout.SOUTH);
        //End Main Content Panel

        //Begin labelPanel and formPanel Content Panel
        JLabel subjectNumberLabel = new JLabel("Subject Number: ");
        labelPanel.add(subjectNumberLabel);
        subjectNumberText = new JTextField(30);
        subjectNumberText.setText(getNumber());
        formPanel.add(subjectNumberText);

        JLabel subjectAgeLabel = new JLabel("Subject Age: ");
        labelPanel.add(subjectAgeLabel);
        subjectAgeText = new JTextField(30);
        formPanel.add(subjectAgeText);

        JLabel subjectSexLabel = new JLabel("Subject Sex: ");
        labelPanel.add(subjectSexLabel);
        String[] sexArray = new String[2];
        sexArray[0] = "Male";
        sexArray[1] = "Female";
        sexComboBox = new JComboBox(sexArray);
        formPanel.add(sexComboBox);
        
        JLabel experimentInitialsLabel = new JLabel("Experimenter's Initials: ");
        labelPanel.add(experimentInitialsLabel);
        experimentInitialsText = new JTextField(30);
        formPanel.add(experimentInitialsText);

        JLabel experimentDateLabel = new JLabel("Experiment Date: ");
        labelPanel.add(experimentDateLabel);
        experimentDateText = new JTextField(30);
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        experimentDateText.setText(dateFormat.format(date));
        formPanel.add(experimentDateText);

        JLabel experimentTrickNumberLabel = new JLabel("Number of tricks: ");
        labelPanel.add(experimentTrickNumberLabel);
        
        String[] trickCountArray = new String[10];
        for(int i = 0; i < trickCountArray.length; i++) {
            trickCountArray[i] = "" + (i+1);
        }
        trickNumberComboBox = new JComboBox(trickCountArray);
        trickNumberComboBox.setSelectedIndex(3);
        formPanel.add(trickNumberComboBox);
        
        launch = new JButton("Begin Slideshow");
        launch.setActionCommand("Begin Slideshow");
        submitPanel.add(launch);
          
        passer = new FrameListener(sexComboBox, trickInputArray, paceInputArray);
        launch.addActionListener(passer);
        trickNumberComboBox.addActionListener(new TrickListener(passer));
        
        // Manually fire the action listener for trickNumberComboBox on Load
        // http://stackoverflow.com/questions/3079524/how-do-i-manually-invoke-an-action-in-swing
        for(ActionListener a: trickNumberComboBox.getActionListeners()) {
            a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
            });
        }


        //End Main Content Panel

        //Begin Explanation Panel
        explanationPanel = new JPanel();
        explanationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        explanationPanel.setLayout(new GridLayout(8, 1, 10, 10));
        explanationPanel.setBackground(new Color(218, 218, 218));
        add(explanationPanel, BorderLayout.EAST);

        explanationPanel.add(new JLabel("<html><font color='0000FF'>A text datafile will be created with this name</font></html>"));
        explanationPanel.add(new JLabel("<html><font color='0000FF'>Enter subject age in years. Decimal ok (ex: 18.5).</font></html>"));
        explanationPanel.add(new JLabel("<html><font color='0000FF'>Select male or female</font></html>"));
        explanationPanel.add(new JLabel("<html><font color='0000FF'>Enter your initials</font></html>"));
        explanationPanel.add(new JLabel("<html><font color='0000FF'>Enter date (mm-dd-yyyy). Default system date.</font></html>"));
        explanationOrderLabel = new JLabel("<html><font color='0000FF'>Determines trick order</font></html>");
        explanationPanel.add(explanationOrderLabel);
        explanationPaceLabel = new JLabel("<html><font color='0000FF'>Slides advance as user clicks.</font></html>");
        explanationPanel.add(explanationPaceLabel);
        explanationPanel.add(new JLabel("<html><font color='0000FF'>Write these parameters to file and begin slideshow</font></html>"));
        explanationPanel.setVisible(false);
        //End Explanation Panel
        
         /****************************************** 
         * Key Binding
         * The code below is used to make 3 different key binds
         * ENTER will proceed past the transition[0] slide into the next trick
         * R will repeat the handcuff trick if at transition[0] slide
         * ESC will exit the slideshow
         ******************************************/
        
        // Key bound AbstractAction items 
        enterAction = new SelfPaceMain.EnterAction();
        
        // Gets the mainImagePanel InputMap and pairs the key to the action
        mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "doEnterAction");
        
        // This line pairs the AbstractAction enterAction to the action "doEnterAction"
        mainPanel.getActionMap().put("doEnterAction", enterAction);

        /******************************************
         * End Key Binding
         ******************************************/
        pack();
    }

    private class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if(actionCommand.equals("Overwrite")) {
                ConfirmWindow confirmation = new ConfirmWindow("Are you sure you want to enter Overwrite Mode?");
                confirmation.setVisible(true);
            }
            else if(actionCommand.equals("Clear Fields")) {
                subjectNumberText.setText("");
                subjectAgeText.setText("");
                experimentInitialsText.setText("");
                experimentDateText.setText("");
            }
            else if(actionCommand.equals("Exit")) {
                System.exit(0);
            }
            else if(actionCommand.equals("howTo")) {
                if(!help) {
                    howToChoice.setText("Quit How To Help");
                    howToChoice.setActionCommand("howTo");
                    explanationPanel.setVisible(true);
                    help = true;
                    pack();
                }
                else if(help) {
                    howToChoice.setText("How To Enter Information");
                    howToChoice.setActionCommand("howTo");
                    explanationPanel.setVisible(false);
                    help = false;
                    pack();
                }
            }
            else if(actionCommand.equals("About")) {
                AboutWindow aboutWindow = new AboutWindow();
                aboutWindow.setVisible(true);
            }
        }
    }
    
    private class TrickListener implements ActionListener {
        private FrameListener passer;
        
        public TrickListener(FrameListener passer) {
            this.passer = passer;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            trickPacePanel.removeAll();
            String experimentTrickNumberString = (String)trickNumberComboBox.getSelectedItem();
            try {
                trickNumber = Integer.parseInt(experimentTrickNumberString.trim());
            }
            catch(NumberFormatException a) {
                System.out.println("Trick Number Input Invalid");
            }
            
            JPanel dynamicLabelPacePanel = new JPanel();
            dynamicLabelPacePanel.setLayout(new GridLayout((trickNumber + 1),1));
            dynamicLabelPacePanel.setBackground(Color.WHITE);
            Dimension d = dynamicLabelPacePanel.getPreferredSize();
            d.width = 150;
            dynamicLabelPacePanel.setPreferredSize(d);
            trickPacePanel.add(dynamicLabelPacePanel, BorderLayout.WEST);
            dynamicLabelPacePanel.add(new JLabel(""));
            
            JPanel dynamicTrickPacePanel = new JPanel();
            dynamicTrickPacePanel.setLayout(new GridLayout((trickNumber + 1),3,5,5));
            dynamicTrickPacePanel.setBackground(Color.WHITE);
            trickPacePanel.add(dynamicTrickPacePanel, BorderLayout.CENTER);
            dynamicTrickPacePanel.add(new JLabel("Trick Name"));
            dynamicTrickPacePanel.add(new JLabel("Pace"));
            dynamicTrickPacePanel.add(new JLabel("Pace Options"));

            trickInputArray = new JComboBox[trickNumber];
            paceInputArray = new JComboBox[trickNumber];
            pacePanelArray = new JPanel[trickNumber];
            paceOptionsArray = new JComponent[trickNumber][6];
            pauseOptionsArray = new JComponent[trickNumber][6];
            
            for(int i = 0; i < trickNumber; i++) {
                dynamicLabelPacePanel.add(new JLabel("Trick " + (i + 1)));
                trickInputArray[i] = new JComboBox(trickArray);
                dynamicTrickPacePanel.add(trickInputArray[i]);
                paceInputArray[i] = new JComboBox(paceArray);
                paceInputArray[i].addActionListener(new PaceListener());
                dynamicTrickPacePanel.add(paceInputArray[i]);
                
                JPanel[] cards = new JPanel[paceArray.length];

                JTextField[] defaultTimes = new JTextField[4];
                for(int j = 0; j < defaultTimes.length; j++) {
                    defaultTimes[j] = new JTextField("750", 5);
                    paceOptionsArray[i][j] = defaultTimes[j];
                }
                
                JTextField[] defaultPause = new JTextField[4];
                for(int j = 0; j < defaultTimes.length; j++) {
                    defaultPause[j] = new JTextField("20", 5);
                    pauseOptionsArray[i][j] = defaultPause[j];
                }
                
                //Yoked Pace Option
                ArrayList<String> dataFileArrayList = new ArrayList<String>(100); 
                String dataFileName;
                File folder = new File(".");
                File[] allFiles = folder.listFiles();

                for(int j = 0; j < allFiles.length; j++) {
                    if(allFiles[j].isFile()) {
                       dataFileName = allFiles[j].getName();
                       if(dataFileName.endsWith(".txt")) {
                           dataFileArrayList.add(dataFileName);
                       }
                    }
                }
                dataFileArrayList.trimToSize();
                String[] dataFileArray = new String[dataFileArrayList.size()];
                for(int j = 0; j < dataFileArrayList.size(); j++) {
                    dataFileArray[j] = dataFileArrayList.get(j);
                }
                JComboBox yokedComboBox = new JComboBox(dataFileArray);
                yokedComboBox.setEditable(false);
                
                //Timed Pace Options
                for(int j = 0; j < 3; j++) {
                    cards[j] = new JPanel();
                    cards[j].setLayout(new BorderLayout());
                    cards[j].setBackground(Color.WHITE);
                    cards[j].add(new JLabel("Time: "), BorderLayout.WEST);
                    cards[j].add(defaultTimes[j], BorderLayout.CENTER);
                    cards[j].add(new JLabel("ms"), BorderLayout.EAST);
                }
                
                //Set Pace Timed Pause Options
                cards[3] = new JPanel();
                cards[3].setLayout(new GridLayout(1,5));
                cards[3].setBackground(Color.WHITE);
                
                cards[3].add(new JLabel("Time: "));
                cards[3].add(defaultTimes[3]);
                cards[3].add(new JLabel("ms"));
                cards[3].add(new JLabel("Slides: "));
                cards[3].add(defaultPause[3]);
                
                //Yoked Pace Options
                cards[4] = new JPanel();
                cards[4].setLayout(new BorderLayout());
                cards[4].setBackground(Color.WHITE);
                cards[4].add(new JLabel("File: "), BorderLayout.WEST);
                cards[4].add(yokedComboBox, BorderLayout.CENTER);
                paceOptionsArray[i][4] = yokedComboBox;
                
                //Self Pace Options
                cards[5] = new JPanel();
                cards[5].setBackground(Color.LIGHT_GRAY);
                paceOptionsArray[i][5] = null;
                
                pacePanelArray[i] = new JPanel(new CardLayout());
                for(int j = 0; j < paceArray.length; j++) {
                    pacePanelArray[i].add(cards[j], paceArray[j]);
                }
                dynamicTrickPacePanel.add(pacePanelArray[i]);
            }
            
            passer.setTrickInputArray(trickInputArray);
            passer.setPaceInputArray(paceInputArray);
            pack();
            validate();
        }
    }
    
    private class PaceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for(int i = 0; i < paceInputArray.length; i++) {
                if(e.getSource() == paceInputArray[i]) {
                    CardLayout c1 = (CardLayout)(pacePanelArray[i].getLayout());
                    c1.show(pacePanelArray[i], (String)paceInputArray[i].getSelectedItem());
                }
            }
        }
    }
    
    private class FrameListener implements ActionListener {
        private JComboBox sexComboBox;
        private JComboBox[] trickInputArray;
        private JComboBox[] paceInputArray;
        
        public FrameListener(JComboBox sexComboBox, JComboBox[] trickInputArray, JComboBox[] paceInputArray) {
            this.sexComboBox = sexComboBox;
            this.trickInputArray = trickInputArray;
            this.paceInputArray = paceInputArray;
        }
        
        public void setTrickInputArray(JComboBox[] trickInputArray) {
            this.trickInputArray = trickInputArray;
        }
        
        public void setPaceInputArray(JComboBox[] paceInputArray) {
            this.paceInputArray = paceInputArray;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
                        
            //Declare age which is a parsed double from the JTextField subjectAgeText.
            //Double was used to allow experimenters to enter half years.
            //For example 18.5
            //Initialize to -1 as an impossible age
            double age = -1;
            
            if(actionCommand.equals("Begin Slideshow")) {
                if(!(subjectNumberText.getText().equals("")) && !(subjectAgeText.getText().equals("")) && !(experimentInitialsText.getText().equals("")) && !(experimentDateText.getText().equals(""))) {
                    subjectNumberString = subjectNumberText.getText();
                    subjectAgeString = subjectAgeText.getText();
                    subjectSexString = (String)sexComboBox.getSelectedItem();
                    experimentInitialsString = experimentInitialsText.getText();
                    experimentDateString = experimentDateText.getText();
                    
                    experimentTrickSettings = new TrickSettings[trickInputArray.length * 2];
                    
                    for(int i = 0, j = 0; i < trickInputArray.length; j++) {
                        //Check what pace type was used
                        int paceIndex = paceInputArray[i].getSelectedIndex();
                        JComponent input = paceOptionsArray[i][paceIndex];
                        JComponent pauseInput = pauseOptionsArray[i][paceIndex];
                        if(j%2 == 0) {
                            experimentTrickSettings[j] = new TrickSettings("handcuffs", 50, (String)paceInputArray[i].getSelectedItem(), handcuffsSubgoals);

                            if(paceIndex < 3) {
                                JTextField inputText = (JTextField)input;
                                experimentTrickSettings[j].setDuration(inputText.getText());
                            }
                            else if(paceIndex == 3) {
                                JTextField inputText = (JTextField)input;
                                experimentTrickSettings[j].setDuration(inputText.getText());
                                JTextField pauseText = (JTextField)pauseInput;
                                experimentTrickSettings[j].setSlidePause(pauseText.getText());
                            }
                            else if(paceIndex == 4) {
                                JComboBox inputBox = (JComboBox)input;
                                experimentTrickSettings[j].setFile((String)inputBox.getSelectedItem());
                            }
                            else if(paceIndex == 5) {
                                experimentTrickSettings[j].setDuration(0);
                            }
                        }
                        else {
                            String trick = (String)trickInputArray[i].getSelectedItem();
                            int trickSize = 0;
                            int[] subgoals = null;
                            if(trick.equals("Card Coin")) {
                                trick = "card";
                                trickSize = 171;
                                subgoals = cardSubgoals;
                            }
                            else if(trick.equals("Cups")) {
                                trick = "cups";
                                trickSize = 176;
                                subgoals = cupsSubgoals;
                            }
                            else if(trick.equals("Rings")) {
                                trick = "rings";
                                trickSize = 159;
                                subgoals = ringsSubgoals;
                            }
                            else if(trick.equals("String")) {
                                trick = "string";
                                trickSize = 176;
                                subgoals = stringSubgoals;
                            }
                            
                            experimentTrickSettings[j] = new TrickSettings(trick, trickSize, (String)paceInputArray[i].getSelectedItem(), subgoals);

                            if(paceIndex < 3) {
                                JTextField inputText = (JTextField)input;
                                experimentTrickSettings[j].setDuration(inputText.getText());
                            }
                            else if(paceIndex == 3) {
                                JTextField inputText = (JTextField)input;
                                experimentTrickSettings[j].setDuration(inputText.getText());
                                JTextField pauseText = (JTextField)pauseInput;
                                experimentTrickSettings[j].setSlidePause(pauseText.getText());
                            }
                            else if(paceIndex == 4) {
                                JComboBox inputBox = (JComboBox)input;
                                experimentTrickSettings[j].setFile((String)inputBox.getSelectedItem());
                            }
                            else if(paceIndex == 5) {
                                experimentTrickSettings[j].setDuration(0);
                            }
                            i++;
                        }
                    }
                    
                    try {
                        age = Double.parseDouble(subjectAgeString.trim());
                    }
                    catch(NumberFormatException a) {
                        System.out.println("Age Input Invalid");
                    }
                    if(subjectNumberString.equals("")) {
                        //Do Nothing
                    }
                    else if(age <= 0 || age > 110) {
                        subjectAgeText.setText("");
                        ErrorWindow smallWindow = new ErrorWindow("Enter Valid Subject Age (0.1-110.0)");
                        smallWindow.setVisible(true);
                    }
                    else {
                        File fileObject = new File(subjectNumberString + ".txt");
                        if(fileObject.exists() && overwrite == false) {
                            ConfirmWindow confirmation = new ConfirmWindow("The file already exists, Overwrite Mode?");
                            confirmation.setVisible(true);
                        }
                        else if(!(fileObject.exists()) || overwrite == true) {
                            SelfPaceMain.this.setVisible(false);
                            writeFile(fileObject);
                            outputStream.println("Subject Number:\t" + subjectNumberString);
                            outputStream.println("Subject Age:\t" + subjectAgeString);
                            outputStream.println("Subject Sex:\t" + subjectSexString);
                            outputStream.println("Experimenter Initials:\t" + experimentInitialsString);
                            outputStream.println("Experiment Date:\t" + experimentDateString);
                            outputStream.close();
                            SwingUtilities.invokeLater(new Runnable() 
                            {
                                @Override
                                public void run() 
                                {
                                    new SlideShow(SelfPaceMain.this).display();
                                }
                            });
                        }
                    }
                }
                else {
                    ErrorWindow smallWindow = new ErrorWindow("Missing Information");
                    smallWindow.setVisible(true);
                }
            }
        }
    }
       
    private class ErrorWindow extends JFrame {
        public ErrorWindow(String e) {
            String errorMessage = e;
            setSize(FRAME_WIDTH_ERROR, FRAME_HEIGHT_ERROR);
            getContentPane().setBackground(Color.WHITE);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());
            
            JLabel errorLabel = new JLabel(errorMessage);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
            
            JButton exitButton = new JButton("Close");
            exitButton.addActionListener(new ExitListener());
            add(exitButton, BorderLayout.SOUTH);
        }
    }
    
    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            JButton sourceButton = (JButton)e.getSource();
            Component frameCheck = sourceButton;
            int i = 0;            
            String frameTest = "null";
            Class<?> c;
            while(!frameTest.equals("javax.swing.JFrame")) {
                frameCheck = frameCheck.getParent();
                c = frameCheck.getClass();
                frameTest = c.getSuperclass().getName().toString();
            }
            JFrame frame = (JFrame)frameCheck;
            if(actionCommand.equals("Close")) {
                frame.dispose();
            }
            else if(actionCommand.equals("Overwrite")) {
                overwriteChoice.setText("Quit Overwrite Mode");
                overwrite = true;
                launch.setText("Begin [Overwrite Mode]");
                launch.setBackground(Color.RED);
                launch.setOpaque(true);
                frame.dispose();
            }
            else if(actionCommand.equals("Quit Overwrite")) {
                overwriteChoice.setText("Enter Overwrite Mode");
                overwrite = false;
                launch.setText("Begin SlideShow");
                launch.setBackground(null);
                launch.setOpaque(false);
                frame.dispose();
            }
            else if(actionCommand.equals("Cancel")) {
                subjectNumberText.setText("");
                subjectNumberString = "";
                frame.dispose();
            }
        }
    }
    
    private class ConfirmWindow extends JFrame {
        private String confirmMessage;
        
        public ConfirmWindow(String msg) {
            this.confirmMessage = msg;
            
            setTitle("Confirm Overwrite Permission");
            setSize(FRAME_WIDTH_ERROR, FRAME_HEIGHT_ERROR);
            getContentPane().setBackground(Color.WHITE);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setLayout(new BorderLayout());
            
            JLabel confirmLabel = new JLabel(confirmMessage);
            confirmLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(confirmLabel, BorderLayout.CENTER);
            
            JPanel confirmPanel = new JPanel();
            confirmPanel.setLayout(new FlowLayout());
            add(confirmPanel,BorderLayout.SOUTH);
            
            JButton overwriteButton = null;
            if(!overwrite) {
                overwriteButton = new JButton("Overwrite");
            }
            else if(overwrite) {
                overwriteButton = new JButton("Quit Overwrite");
            }
            overwriteButton.addActionListener(new ExitListener());
            confirmPanel.add(overwriteButton);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ExitListener());
            confirmPanel.add(cancelButton);
        }
    }

    private class AboutWindow extends JFrame {
        public AboutWindow() {
            setTitle("About This Program");
            setSize(FRAME_WIDTH_ABOUT, FRAME_HEIGHT_ABOUT);
            getContentPane().setBackground(Color.WHITE);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());
            
            JPanel aboutPanel = new JPanel();
            aboutPanel.setLayout(new BorderLayout());
            aboutPanel.setBorder(new EmptyBorder(20,0,20,0));
            add(aboutPanel);
            
            JLabel line1 = new JLabel("Self Paced Slideshow v2.0");
            JLabel line2 = new JLabel("Written by Jonathan Sage");
            JLabel line3 = new JLabel("Compiled 2013");
            
            line1.setHorizontalAlignment(JLabel.CENTER);
            line2.setHorizontalAlignment(JLabel.CENTER);
            line3.setHorizontalAlignment(JLabel.CENTER);
            
            aboutPanel.add(line1, BorderLayout.NORTH);
            aboutPanel.add(line2, BorderLayout.CENTER);
            aboutPanel.add(line3, BorderLayout.SOUTH);
            
            JButton exitButton = new JButton("Close");
            exitButton.addActionListener(new ExitListener());
            add(exitButton, BorderLayout.SOUTH);
        }
    }
    
    private class EnterAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Enter was pressed");
            launch.doClick();
        }
    }
    
    private String getNumber() {
        boolean exists = true;
        int digits = 4;
        int num;
        String format;
        String result;
        String subjectNumber = null;
        for(int i = 1; exists && i < 9999; i++) {
            exists = false;
            num = i;
            format = String.format("%%0%dd", digits);
            result = String.format(format, num);
            subjectNumber = "subject_" + result;
            File fileObject = new File(subjectNumber + ".txt");
            if(fileObject.exists()) {
                exists = true;
            }
        }
        return subjectNumber;
    }
    
    private void writeFile(File fileObject) {
        try {
            outputStream = new PrintWriter(new FileOutputStream(fileObject, false));
        }
        catch(FileNotFoundException e) {
            System.out.println("Cannot find file " + subjectNumberString + ".txt or it could not be opened.");
            System.exit(0);
        }
    }
     
    public String getSubjectNumber() {
        return subjectNumberString;
    }
    
    public TrickSettings[] getTrickSettings() {
        return experimentTrickSettings;
    }
}