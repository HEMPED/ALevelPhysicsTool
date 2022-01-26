import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class Pendulum extends JFrame {
    //boolean used to stop the program
    boolean running = true;

    //declare pendulum object
    PendulumObj PO;

    //initialise stacks and other variables that are used with the undo and redo functions
    Stack<PendulumObj> undoStack = new Stack<>();
    Stack<PendulumObj> redoStack = new Stack<>();
    JButton undoB, redoB;

    //declare variables that are used for the sliders
    JSlider gravityS, lengthS, initAngleS;
    JLabel gravitySL, lengthSL, initAngleSL;
    boolean sliderChanged = false;

    //declare variables that are used for the menu bar
    JMenuBar menuBar;
    JButton saveB, loadB;

    //declare variables that are used in the extra input panel
    JButton extraButton;
    double lengthTF;
    double velocityTF = 0;
    double gravityTF;
    double angleTF;
    boolean TFSaved = false;

    //variables that are used when the user clicks the screen
    boolean clicked = false;

    //variables used with timing the simulation
    long start, end;
    double diff;

    public Pendulum(){
        //assigns values to the pendulum object
        PO = new PendulumObj(5, 9.81, (Math.PI/4), (Math.PI/4));

        //set layout for the whole frame and individual panels
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setBackground(Color.white);
        c.insets = new Insets(3,3,3,3);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        sliderPanel.setBackground(Color.WHITE);

        JPanel extraPanel = new JPanel();
        extraPanel.setBackground(Color.WHITE);
        extraPanel.setLayout(new GridBagLayout());

        //menuBar
        menuBar = new JMenuBar();

        saveB = new JButton("Save");
        saveB.setOpaque(true);
        saveB.setContentAreaFilled(false);
        saveB.setBorderPainted(false);
        saveB.setFocusable(false);

        saveButtonPressed SBP = new saveButtonPressed();
        saveB.addActionListener(SBP);

        loadB = new JButton("Load");
        loadB.setOpaque(true);
        loadB.setContentAreaFilled(false);
        loadB.setBorderPainted(false);
        loadB.setFocusable(false);

        loadButtonPressed LBP = new loadButtonPressed();
        loadB.addActionListener(LBP);

        menuBar.add(saveB);
        menuBar.add(loadB);

        setJMenuBar(menuBar);

        //create sliders and their labels
        gravityS = new JSlider(JSlider.HORIZONTAL,0,2000,981);
        gravityS.setMajorTickSpacing(100);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        sliderPanel.add(gravityS, c);

        gravitySL = new JLabel("Gravity: 9.81 N/kg");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        sliderPanel.add(gravitySL, c);

        lengthS = new JSlider(JSlider.HORIZONTAL,0,10000,5000);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        sliderPanel.add(lengthS, c);

        lengthSL = new JLabel("Length: 5.00 m");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        sliderPanel.add(lengthSL, c);

        initAngleS = new JSlider(JSlider.HORIZONTAL, -180,180, 45);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        sliderPanel.add(initAngleS, c);

        initAngleSL = new JLabel("Angle: 45 degrees");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        sliderPanel.add(initAngleSL, c);

        //add change listeners to the sliders
        gravitySChanged GSC = new gravitySChanged();
        gravityS.addChangeListener(GSC);

        lengthSChanged LSC = new lengthSChanged();
        lengthS.addChangeListener(LSC);

        initAngleSChanged IASC = new initAngleSChanged();
        initAngleS.addChangeListener(IASC);

        //add the slider panel to the main frame
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(sliderPanel,c);

        //add extra button to the extra panel and adds the extra panel to the frame
        extraButton = new JButton("Click here for more inputs");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        extraPanel.add(extraButton,c);

        //add undo and redo buttons to the extra panel
        undoB = new JButton("Undo");
        undoB.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        extraPanel.add(undoB, c);

        redoB = new JButton("Redo");
        redoB.setEnabled(false);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        extraPanel.add(redoB, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        add(extraPanel, c);

        //add action listener to the extra button
        extraButtonPressed EBP = new extraButtonPressed();
        extraButton.addActionListener(EBP);

        //add action listener to the undo and redo button
        undoButtonPressed UBP = new undoButtonPressed();
        undoB.addActionListener(UBP);

        redoButtonPressed RBP = new redoButtonPressed();
        redoB.addActionListener(RBP);

        //initialise panel for the pendulum and makes it take up all available space in the window (weightx, weighty)
        PendulumPanel pendulumPanel = new PendulumPanel();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        add(pendulumPanel,c);

        //starts the simulation
        new Thread(pendulumPanel).start();
    }

    //panel that contains the actual simulation of the pendulum
    public class PendulumPanel extends JPanel implements Runnable{

        //initialise integers that are used to draw the pendulum
        int pendulumX, pendulumY, pointX, pointY;
        public PendulumPanel(){
            //super(true);
            setLayout(new FlowLayout());
            setBackground(Color.DARK_GRAY);
            Dimension d = new Dimension(500, 500);
            setPreferredSize(d);

            //add mouse listener which allows the pendulum to be set to a certain position
            MListener mouseListener = new MListener();
            addMouseListener(mouseListener);
        }

        //draws the pendulum
        @Override
        public void paint(Graphics g){
            Graphics2D g1 = (Graphics2D)g;
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g1.setRenderingHints(rh);
            //fills the background
            g1.setColor(Color.white);
            g1.fillRect(0,0,getWidth(),getHeight());


            //creates the overlay which prints the current angle and velocity
            g1.setColor(Color.black);

            int currentAngle = (int) (PO.getAngle() * (180/Math.PI));
            double time2 = diff % PO.getTimePeriod();
            time2 = Math.round(time2 * 100) / 100.0;

            g1.drawString("Current Angle: " + currentAngle + " degrees", 5, 15);
            g1.drawString("Time:" + time2 + " s", 5, 30);

            //calculates the coordinates of the pendulum and fixed point
            calculatePoints();

            //draws fixed point
            g1.setColor(Color.black);
            g1.fillRoundRect(pointX - 2, pointY - 2, 4, 4, 4, 4);

            //draws the pendulum string
            g1.setColor(Color.black);
            g1.drawLine(pointX, pointY, pendulumX, pendulumY);

            //draws pendulum bob
            g1.setColor(Color.RED);
            g1.fillRoundRect(pendulumX - 10, pendulumY - 10, 20, 20, 20, 20);
        }

        public void run(){
            //gets current value of gravity and length of the pendulum
            PO.setGravity((double) gravityS.getValue() / 100);
            PO.setLength((double) lengthS.getValue() / 1000);

            start = System.nanoTime();
            calculate();
        }

        private void calculate(){
            while(running) {
                end = System.nanoTime();
                diff = (end - start) / 1000000000.0;
                //checks if any values have been changed
                checkValues();
                //calculates the current position of the pendulum
                calculateDisplacement();
                //redraws the image
                repaint();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(this,
                            "<HTML>Error running the program, please restart</HTML>",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void calculateDisplacement(){
            //calculates time period
            double timePeriod = 2 * Math.PI * Math.pow((PO.getLength() / PO.getGravity()), 0.5);
            PO.setTimePeriod(timePeriod);
            //calculates angular velocity
            double angularVelocity = Math.pow((PO.getGravity() / PO.getLength()), 0.5);
            PO.setAngularVelocity(angularVelocity);
            //calculates displacement
            double displacementAng = PO.getInitialAngle() * Math.cos(angularVelocity * diff);
            //sets the displacement as the angle
            if(PO.getInitialAngle() == Math.PI || PO.getAngle() == -1 * Math.PI){
                PO.setAngle(PO.getInitialAngle());
            } else {
                PO.setAngle(displacementAng);
            }
        }

        //method calculates the coordinate of the fixed point and the pendulum bob
        private void calculatePoints(){
            //pointX is dynamically calculated whenever the screen resizes- its always at the same relative
            //position compared to the window
            pointX = getWidth()/2;
            pointY = getHeight()/10;
            pendulumX = pointX + (int) (Math.sin(PO.getAngle()) * PO.getLength()*100);
            pendulumY = pointY + (int) (Math.cos(PO.getAngle()) * PO.getLength()*100);
        }

        private void checkValues(){
            //saves old values if a variable is changed
            if(sliderChanged | TFSaved | clicked){
                undoStack.push(new PendulumObj(PO.getLength(), PO.getGravity(), PO.getAngle(), PO.getInitialAngle()));
                start = System.nanoTime();
                //allows the undo button to be clicked
                undoB.setEnabled(true);
            }
            //checks if the sliders have been moved
            if (sliderChanged) {
                PO.setGravity((double) gravityS.getValue() / 100);
                PO.setLength((double) lengthS.getValue() / 1000);
                PO.setInitialAngle(initAngleS.getValue() * (Math.PI/180));
                PO.setAngle(PO.getInitialAngle());
                //resets the flag
                sliderChanged = false;
            }
            //checks if the user has inputted data values to the text fields in the extra panel
            if (TFSaved) {
                PO.setLength((int) lengthTF);
                PO.setGravity(gravityTF);
                PO.setInitialAngle(angleTF);
                PO.setAngle(PO.getInitialAngle());
                //resets the flag
                TFSaved = false;

                //sets the values of the sliders to the specified values
                gravityS.setValue((int)(PO.getGravity() * 100));

                int angleInt = (int) (PO.getAngle() * (180 / Math.PI));
                initAngleS.setValue(angleInt);

                lengthS.setValue((int) (PO.getLength() * 1000));

                //sets the slider changed flag to false
                sliderChanged = false;
            }

            if(clicked){
                //resets booleans so only 1 value is stored in the undo stack
                clicked = false;
                sliderChanged = false;
            }
        }

        //mouse listener added to the pendulumPanel and allows the user to quickly change the position of the pendulum
        class MListener extends MouseInputAdapter {
            public void mousePressed(MouseEvent e){
                double diffX, diffY, length, angle = 45;
                //get position of mouse cursor
                pendulumX = e.getX();
                pendulumY = e.getY();

                //find the difference between the anchor point and the cursor
                diffX = pointX - pendulumX;
                diffY = pointY - pendulumY;

                //calculate the new length of the pendulum
                length = Math.pow(diffX, 2) + Math.pow(diffY, 2);
                length = Math.pow(length, 0.5);
                length = length / 100;

                //calculate the new angle of the pendulum

                if(pendulumY > pointY){
                    angle = Math.atan(diffX / diffY);
                } else {
                    angle = Math.PI + Math.atan(diffX / diffY);
                }
                //set the values of sliders
                lengthS.setValue((int) (length * 1000));

                int angleInt = (int) (angle * (180 / Math.PI));
                if(angleInt > 180){
                    angleInt = angleInt - 360;
                }

                initAngleS.setValue(angleInt);
                //trigger the flag
                clicked = true;
            }
        }
    }

    //panel that is used to input data via text fields
    public class extraInputPanel extends JPanel{
        //text fields and labels that display the current value with its unit and allows the user to input data
        //text area used to display errors with the data that the user has inputted
        JTextField gravityTF, lengthTF, initAngleTF;
        JLabel gravityL, lengthL, initAngleL;
        JLabel gravityUnit, lengthUnit, initAngleUnit;
        JTextArea errors;
        JButton saveChanges;

        public extraInputPanel(){
            //set layout and background colour of the extra input panel
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3,3,3,3);
            setBackground(Color.white);

            //displays the variable, current value in the text field and the unit
            gravityL = new JLabel("Gravity: ");
            gravityL.setBackground(Color.white);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            add(gravityL, c);

            double gravTemp = (double) gravityS.getValue() / 100;
            gravityTF = new JTextField("" + gravTemp);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            add(gravityTF, c);

            gravityUnit = new JLabel("N/kg");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 0;
            add(gravityUnit, c);

            lengthL = new JLabel("Length: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            add(lengthL, c);

            double lengthTemp = (double) lengthS.getValue() / 1000;
            lengthTF = new JTextField("" + lengthTemp);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            add(lengthTF, c);

            lengthUnit = new JLabel("m");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 1;
            add(lengthUnit, c);

            initAngleL = new JLabel("Initial Angle: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            add(initAngleL, c);

            int initAngleTemp = initAngleS.getValue();
            initAngleTF = new JTextField("" + initAngleTemp);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 2;
            add(initAngleTF, c);

            initAngleUnit = new JLabel("degrees");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 2;
            add(initAngleUnit, c);

            //initiates the data validation
            saveChanges = new JButton("Save Changes");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 6;
            add(saveChanges, c);

            saveChangesPressed SCP = new saveChangesPressed();
            saveChanges.addActionListener(SCP);

            //errors is a text area used to show when an illegal value has been entered
            errors = new JTextArea("");
            errors.setLineWrap(true);
            errors.setWrapStyleWord(true);
            errors.setEditable(false);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = 500;
            c.weightx = 1.0;
            c.gridx = 0;
            c.gridy = 7;
            add(errors, c);
        }

        //One label is used to display all errors as it may be easier for the user to read and correct.
        public class saveChangesPressed implements ActionListener{
            //initialise values that are used within the class. ""T is used to store a temporary variable that is
            //being validated
            boolean isValidated = true;
            double lengthT = 5, gravityT = 9.81, initAngleT = 45, initVelocityT = 0;

            public void actionPerformed(ActionEvent saveChangesPressed){
                //clears the label every time the button is pressed
                errors.setText("");
                isValidated = true;

                //makes sure gravity is a number and negative or above 20N/kg
                try{
                    gravityT = Double.parseDouble(gravityTF.getText());
                } catch (NumberFormatException nfe){
                    isValidated = false;
                    if(errors.getText().equals("")) {
                        gravityT = (double) gravityS.getValue() / 100;
                        errors.setText("NUMBERS ONLY");
                    }else if(!errors.getText().contains("NUMBERS ONLY")){
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }

                if(gravityT < 0){
                    isValidated = false;
                    gravityT = PO.getGravity();
                    if(errors.getText().equals("")) {
                        errors.setText("GRAVITY CANNOT BE LESS THAN 0");
                    }else{
                        errors.setText(errors.getText() + ", GRAVITY CANNOT BE LESS THAN 0");
                    }
                }

                if(gravityT > 20){
                    isValidated = false;
                    gravityT = PO.getGravity();
                    if(errors.getText().equals("")) {
                        errors.setText("GRAVITY CANNOT BE GREATER THAN 20");
                    }else{
                        errors.setText(errors.getText() + ", GRAVITY CANNOT BE GREATER THAN 20");
                    }
                }

                //makes sure length is a number and isn't negative or above 10m
                try{
                    lengthT = Double.parseDouble(lengthTF.getText());
                }catch (NumberFormatException nfe){
                    isValidated = false;
                    lengthT = (double) lengthS.getValue() / 1000;

                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(!errors.getText().contains("NUMBERS ONLY")){
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }
                if(lengthT < 0){
                    isValidated = false;
                    lengthT = PO.getLength();
                    if(errors.getText().equals("")) {
                        errors.setText("LENGTH CANNOT BE LESS THAN 0");
                    }else{
                        errors.setText(errors.getText() + ", LENGTH CANNOT BE LESS THAN 0");
                    }
                }
                if(lengthT > 10){
                    isValidated = false;
                    lengthT = PO.getLength();
                    if(errors.getText().equals("")) {
                        errors.setText("LENGTH CANNOT BE GREATER THAN 10");
                    }else{
                        errors.setText(errors.getText() + ", LENGTH CANNOT BE GREATER THAN 10");
                    }
                }

                //makes sure the initial angles specified is a number and not above 180 or below -180
                try{
                    initAngleT = Double.parseDouble(initAngleTF.getText());
                }catch (NumberFormatException NFE){
                    isValidated = false;
                    initAngleT = initAngleS.getValue();
                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(!errors.getText().contains("NUMBERS ONLY")){
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }
                if(initAngleT < -180){
                    isValidated = false;
                    initAngleT = PO.getAngle();
                    if(errors.getText().equals("")) {
                        errors.setText("INITIAL ANGLE CANNOT BE LESS THAN -180");
                    }else{
                        errors.setText(errors.getText() + ", INITIAL ANGLE CANNOT BE LESS THAN -180");
                    }
                }
                if(initAngleT > 180){
                    isValidated = false;
                    initAngleT = PO.getAngle();
                    if(errors.getText().equals("")){
                        errors.setText("INITIAL ANGLE CANNOT BE GREATER THAN 180");
                    } else {
                        errors.setText(errors.getText() + ", INITIAL ANGLE CANNOT BE GREATER THAN 180");
                    }
                }

                //stores the validated temporary variables to the public ""Extra variables.
                Pendulum.this.lengthTF = lengthT;
                Pendulum.this.gravityTF = gravityT;
                angleTF = initAngleT * (Math.PI / 180);

                if(isValidated) {
                    TFSaved = true;
                }
            }
        }
    }

    //create change listeners for all the sliders that set the current value of the slider to the corresponding label
    public class gravitySChanged implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            double newGravity = (double) gravityS.getValue() / 100;
            gravitySL.setText("Gravity: " + newGravity + " N/kg");
            sliderChanged = true;
        }
    }

    public class lengthSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            double newLength = (double) lengthS.getValue() / 1000;
            lengthSL.setText("Length: " + newLength + " m");
            sliderChanged = true;
        }
    }

    public class initAngleSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            int newInitAngle = initAngleS.getValue();
            initAngleSL.setText("Angle: " + newInitAngle + " degrees");
            sliderChanged = true;
        }
    }

    //action listeners for the menubar
    public class saveButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent saveButtonPressed){
            Save save = new Save();
            save.openExplorer();
            save.start(PO);
        }
    }

    public class loadButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent loadButtonPressed){
            Load load = new Load();
            load.openExplorer();
            if(load.getFileChosen()) {
                read(load.getDirectory());
            }
        }
    }

    //action listener for the extra button that creates a new JFrame for the extra inputs
    public class extraButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent extraButtonPressed){
            JFrame extraInputFrame = new JFrame();
            extraInputFrame.setLayout(new FlowLayout());
            extraInputPanel EIP = new extraInputPanel();
            extraInputFrame.add(EIP);
            extraInputFrame.pack();
            extraInputFrame.getContentPane().setBackground(Color.white);
            extraInputFrame.setVisible(true);
        }
    }

    //action listeners for the undo and redo buttons
    public class undoButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent undoButtonPressed){
            if(!undoStack.empty()){
                //adds current values to the redo stack
                redoStack.push(new PendulumObj(PO.getLength(), PO.getGravity(),
                        PO.getAngle(), PO.getInitialAngle()));

                //gets the old values
                PendulumObj POtemp = undoStack.pop();
                //resets variables in the pendulum object to their old values
                PO.setLength(POtemp.getLength());
                PO.setInitialAngle(POtemp.getInitialAngle());
                PO.setAngle(POtemp.getInitialAngle());
                PO.setGravity(POtemp.getGravity());

                //resets sliders to their old values
                int angleInt = (int) (PO.getAngle() * (180 / Math.PI));

                gravityS.setValue((int) (PO.getGravity() * 100));
                lengthS.setValue((int) (PO.getLength() * 1000));
                initAngleS.setValue(angleInt);

                sliderChanged = false;

                //disables the button if that was the last value in the stack
                if(undoStack.empty()){
                    undoB.setEnabled(false);
                }

                //enables the redo button
                if(!redoB.isEnabled()){
                    redoB.setEnabled(true);
                }
            }
        }
    }

    public class redoButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent redoButtonPressed){
            if(!redoStack.empty()){
                //pushes the old values to the undo stack and allows the undo button to be pressed
                undoStack.push(new PendulumObj(PO.getLength(), PO.getGravity(), PO.getAngle(), PO.getInitialAngle()));

                //gets values from the redo stack
                PendulumObj POtemp = redoStack.pop();

                //resets variables in the pendulum object to their old values
                PO.setLength(POtemp.getLength());
                PO.setInitialAngle(POtemp.getInitialAngle());
                PO.setAngle(POtemp.getInitialAngle());
                PO.setGravity(POtemp.getGravity());

                //resets sliders to their old values
                int angleInt = (int) (PO.getAngle() * (180 / Math.PI));

                gravityS.setValue((int) (PO.getGravity() * 100));
                lengthS.setValue((int) (PO.getLength() * 1000));
                initAngleS.setValue(angleInt);

                sliderChanged = false;

                //resets the timer
                start = System.nanoTime();

                //enables undo button if it was disabled
                if(!undoB.isEnabled()){
                    undoB.setEnabled(true);
                }

                //disables redo button if that was the last pendulum object in the stack
                if(redoStack.empty()){
                    redoB.setEnabled(false);
                }
            }
        }
    }

    //Method to read values from file
    public void read(File directory){
        try{
            ObjectMapper mapper = new ObjectMapper();

            PendulumObj POTemp = mapper.readValue(directory, PendulumObj.class);

            PO.setAngle(POTemp.getInitialAngle());
            PO.setGravity(POTemp.getGravity());
            PO.setLength(POTemp.getLength());
            PO.setInitialAngle(POTemp.getInitialAngle());

            //sets the values of the sliders to the specified values
            gravityS.setValue((int)(PO.getGravity() * 100));

            int angleInt = (int) (PO.getAngle() * (180 / Math.PI));
            initAngleS.setValue(angleInt);

            lengthS.setValue((int) (PO.getLength() * 1000));

            //sets the slider changed flag to false
            sliderChanged = false;

            //resets the timer
            start = System.nanoTime();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "<HTML>Error reading from file. Check if you " +
                    "selected the correct file and retry.</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    //setter methods
    public void setRunning(boolean b){
        running = b;
    }
    //main method sets the size and tits of the frame.
    public static void main(String[] args){
        Pendulum pendulum = new Pendulum();
        pendulum.setVisible(true);
        pendulum.setSize(800,800);
        //starts maximised as the pendulum takes up a lot of room
        pendulum.setExtendedState(JFrame.MAXIMIZED_BOTH);
        pendulum.setTitle("Pendulum");
        pendulum.setLocation(0, 0);
        //window listener allows the user to save system resources when the x button is pressed by stopping the simulation
        pendulum.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                pendulum.setRunning(false);
            }
        });
    }
}
