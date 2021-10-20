import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Stack;

public class Pendulum extends JFrame {
    //declare pendulum object
    public pendulumObj PO = new pendulumObj(5, 0, 0, 9.81, (Math.PI/4), (Math.PI/4), 0.015);

    //initialise stacks and other variables that are used with the undo and redo functions
    public Stack<pendulumObj> undoStack = new Stack<>();
    public Stack<pendulumObj> redoStack = new Stack<>();
    JButton undoB, redoB;

    //declare variables that are used for the sliders
    JSlider gravityS, lengthS, initAngleS;
    JLabel gravitySL, lengthSL, initAngleSL, overlayL;
    boolean variableChanged = false;

    //declare variables that are used for the menu bar
    JMenuBar menuBar;
    JButton saveB, loadB;

    //declare variables that are used in the extra input panel
    JButton extraButton;
    public double lengthExtra;
    public double velocityExtra = 0;
    public double gravityExtra;
    public double angleExtra;
    public double dtExtra;
    boolean TFSaved = false;

    //variables that are used when the user clicks the screen
    boolean clicked = false;

    public Pendulum(){
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

        loadB = new JButton("Load");
        loadB.setOpaque(true);
        loadB.setContentAreaFilled(false);
        loadB.setBorderPainted(false);
        loadB.setFocusable(false);

        menuBar.add(saveB);
        menuBar.add(loadB);

        setJMenuBar(menuBar);

        //create sliders and their labels
        gravityS = new JSlider(JSlider.HORIZONTAL,0,2000,981);
        gravityS.setMajorTickSpacing(1000);
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
        //initialise variables that are used for the stopwatch
        long start, end;

        public PendulumPanel(){
            setLayout(new FlowLayout());
            setBackground(Color.DARK_GRAY);
            Dimension d = new Dimension(500, 500);
            setPreferredSize(d);
            setDoubleBuffered(true);

            //add mouse listener which allows the pendulum to be set to a certain position
            MListener mouseListener = new MListener();
            addMouseListener(mouseListener);
        }

        //draws the pendulum
        @Override
        public void paint(Graphics g){
            //fills the background
            g.setColor(Color.white);
            g.fillRect(0,0,getWidth(),getHeight());


            //initialise the overlay which prints the current angle and velocity
            g.setColor(Color.black);

            int currentAngle = (int) (PO.getAngle() * (180/Math.PI));
            double currentVelocity = (double) (Math.round(PO.getVelocity() * 100)) / 100;
            double currentAngularVelocity = (double) Math.round((PO.getVelocity() / PO.getLength()) * 100) / 100;


            g.drawString("Current Angle: " + currentAngle + " degrees", 3, 13);
            g.drawString("Current Velocity: " + currentVelocity + " m/s", 3, 28);
            g.drawString("Current Angular Velocity: " + currentAngularVelocity + " rad/s", 3, 43);

            //calculates the points of the pendulum and fixed point
            pointX = getWidth()/2;
            pointY = getHeight()/10;
            pendulumX = pointX + (int) (Math.sin(PO.getAngle()) * PO.getLength()*100);
            pendulumY = pointY + (int) (Math.cos(PO.getAngle()) * PO.getLength()*100);

            //draws fixed point
            g.setColor(Color.BLACK);
            g.drawLine(pointX, pointY, pendulumX, pendulumY);
            g.fillRoundRect(pointX - 5, pointY - 5,
                    10, 10, 10, 10);

            //draws pendulum bob
            g.setColor(Color.RED);
            g.fillRoundRect(pendulumX - 10, pendulumY - 10,
                    20, 20, 20, 20);


        }

        public void run(){
            //gets current value of gravity and length of the pendulum
            PO.setGravity((double) gravityS.getValue() / 100);
            PO.setLength((double) lengthS.getValue() / 1000);

            start = System.currentTimeMillis();
            calculate();
        }

        public void calculate(){
            int angleInt;
            double angleAccel;

            while(true) {
                //saves old variables to undo stack if a variable is changed
                if(variableChanged | TFSaved | clicked){
                    undoStack.push(new pendulumObj(PO.getLength(), PO.getVelocity(), PO.getInitialVelocity(), PO.getGravity(), PO.getAngle(), PO.getInitialAngle(), PO.getDt()));
                    //allows the undo button to be clicked
                    undoB.setEnabled(true);
                }

                //checks if the sliders have been moved
                if (variableChanged) {
                    PO.setGravity((double) gravityS.getValue() / 100);
                    PO.setLength((double) lengthS.getValue() / 1000);
                    PO.setInitialAngle(initAngleS.getValue() * (Math.PI/180));
                    PO.setAngle(PO.getInitialAngle());
                    PO.setVelocity(0);
                    variableChanged = false;
                }
                //checks if the user has inputted data values to the text fields in the extra panel
                if (TFSaved) {
                    PO.setLength((int) lengthExtra);
                    PO.setGravity(gravityExtra);
                    PO.setInitialAngle(angleExtra);
                    PO.setAngle(PO.getInitialAngle());
                    PO.setDt(dtExtra);
                    PO.setInitialVelocity(velocityExtra);
                    TFSaved = false;

                    //sets the values of the sliders to the specified values
                    gravityS.setValue((int)(PO.getGravity() * 100));

                    angleInt = (int) (PO.getAngle() * (180 / Math.PI));
                    initAngleS.setValue(angleInt);

                    lengthS.setValue((int) (PO.getLength() * 1000));

                    //sets the slider changed flag to false
                    variableChanged = false;
                }

                if(clicked){
                    clicked = false;
                    variableChanged = false;
                }

                //recalculates the velocity and angle of the pendulum and redraws the image
                angleAccel = Math.sin(PO.getAngle()) * -1 * PO.getGravity();
                //makes sure the program is not assuming that the velocity is not constantly increasing when the angle is 180 degrees
                if(PO.getAngle() == -Math.PI || PO.getAngle() == Math.PI){
                    PO.setVelocity(0);
                }
                PO.setVelocity(PO.getVelocity() + angleAccel* 0.015);
                PO.setAngle(PO.getAngle() + PO.getVelocity() * 0.015);
                if(PO.getAngle() == -Math.PI || PO.getAngle() == Math.PI){
                    PO.setVelocity(0);
                }

                //calculates the displacement of the pendulum (for the graph)
                double equilibriumX = pointX;
                double equilibriumY = pointY + PO.getLength();
                double displacement = Math.pow((equilibriumX - pendulumX), 2) + Math.pow((equilibriumY - pendulumY), 2);
                displacement = Math.pow(displacement, 0.5);
                displacement = displacement / (double) 100;

                if(PO.getAngle() < 0){
                    displacement = displacement * -1;
                }

                //redraws the image
                repaint();

                //waits a certain amount of time that can be specified by the user
                try {
                    Thread.sleep((long) (PO.getDt() * 2300));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

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
        JTextField gravityTF, lengthTF, initAngleTF, dtTF, initVelocityTF;
        JLabel gravityL, lengthL, initAngleL, dtL, initVelocityL;
        JLabel gravityUnit, lengthUnit, initAngleUnit, dtUnit, initVelocityUnit;
        JTextArea errors;
        JButton saveChanges;

        public extraInputPanel(){
            //set layout and background colour of the extra input panel
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3,3,3,3);
            setBackground(Color.white);

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

            dtL = new JLabel("Time per tick: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;
            add(dtL, c);

            dtTF = new JTextField("" + PO.getDt());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 3;
            add(dtTF, c);

            dtUnit = new JLabel("seconds");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 3;
            add(dtUnit, c);

            initVelocityL = new JLabel("Initial Velocity");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 4;
            add(initVelocityL, c);

            initVelocityTF = new JTextField("0");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 4;
            add(initVelocityTF, c);

            initVelocityUnit = new JLabel("m/s");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 4;
            add(initVelocityUnit, c);

            saveChanges = new JButton("Save Changes");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 6;
            add(saveChanges, c);

            saveChangesPressed SCP = new saveChangesPressed();
            saveChanges.addActionListener(SCP);

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

        public class saveChangesPressed implements ActionListener{
            //initialise values that are used within the class. ""T is used to store a temporary variable that is being validated
            boolean isValidated = true;
            double lengthT = 5, gravityT = 9.81, initAngleT = 45, dtT = 0.1, initVelocityT = 0;

            public void actionPerformed(ActionEvent saveChangesPressed){
                //re-validates data every time the saveChanges button is pressed
                errors.setText("");

                //makes sure gravity is a number and negative or above 20N/kg
                try{
                    gravityT = Double.parseDouble(gravityTF.getText());
                } catch (NumberFormatException nfe){
                    isValidated = false;
                    if(errors.getText().equals("")) {
                        gravityT = (double) gravityS.getValue() / 100;
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){

                    }else{
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
                    }else if(errors.getText().contains("NUMBERS ONLY")){

                    }else{
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
                    }else if(errors.getText().contains("NUMBERS ONLY")){
                    }else{

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



                //makes sure the dt is a number an positive.
                try {
                    dtT = Double.parseDouble(dtTF.getText());
                } catch (NumberFormatException NFE){
                    isValidated = false;
                    dtT = PO.getDt();
                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){
                    }else{
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }

                if(dtT < 0){
                    isValidated = false;
                    dtT = PO.getAngle();
                    if(errors.getText().equals("")){
                        errors.setText("DT CANNOT BE LESS THAN 0");
                        dtT = 0.1;
                    } else {
                        errors.setText(errors.getText() + ", DT CANNOT BE LESS THAN 0");
                    }
                }

                //makes sure the initial velocity specified is a number
                try {
                    initVelocityT = Double.parseDouble(initVelocityTF.getText());
                }catch(NumberFormatException NFE){
                    isValidated = false;
                    initVelocityT = 0;
                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){
                    }else{
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }

                //stores the validated temporary variables to the public ""Extra variables.
                lengthExtra = lengthT;
                gravityExtra = gravityT;
                angleExtra = initAngleT * (Math.PI / 180);
                dtExtra = dtT;
                velocityExtra = initVelocityT;

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
            variableChanged = true;
        }
    }

    public class lengthSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            double newLength = (double) lengthS.getValue() / 1000;
            lengthSL.setText("Length: " + newLength + " m");
            variableChanged = true;
        }
    }

    public class initAngleSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent e){
            int newInitAngle = initAngleS.getValue();
            initAngleSL.setText("Angle: " + newInitAngle + " degrees");
            variableChanged = true;
        }
    }

    //action listeners for the menubar
    public class saveButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent saveButtonPressed){

        }
    }

    public class loadButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent loadButtonPressed){

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
                redoStack.push(new pendulumObj(PO.getLength(), PO.getVelocity(), PO.getInitialVelocity(), PO.getGravity(), PO.getAngle(), PO.getInitialAngle(), PO.getDt()));

                //gets the old values
                pendulumObj POtemp = undoStack.pop();
                //resets variables in the pendulum object to their old values
                PO.setVelocity(POtemp.getInitialVelocity());
                PO.setVelocity(POtemp.getInitialVelocity());
                PO.setLength(POtemp.getLength());
                PO.setInitialAngle(POtemp.getInitialAngle());
                PO.setAngle(POtemp.getInitialAngle());
                PO.setGravity(POtemp.getGravity());
                PO.setDt(POtemp.getDt());

                //resets sliders to their old values
                int angleInt = (int) (PO.getAngle() * (180 / Math.PI));

                gravityS.setValue((int) (PO.getGravity() * 100));
                lengthS.setValue((int) (PO.getLength() * 1000));
                initAngleS.setValue(angleInt);

                variableChanged = false;

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
                //gets values from the redo stack
                pendulumObj POtemp = redoStack.pop();

                //resets variables in the pendulum object to their old values
                PO.setVelocity(POtemp.getInitialVelocity());
                PO.setVelocity(POtemp.getInitialVelocity());
                PO.setLength(POtemp.getLength());
                PO.setInitialAngle(POtemp.getInitialAngle());
                PO.setAngle(POtemp.getInitialAngle());
                PO.setGravity(POtemp.getGravity());
                PO.setDt(POtemp.getDt());

                //resets sliders to their old values
                int angleInt = (int) (PO.getAngle() * (180 / Math.PI));

                gravityS.setValue((int) (PO.getGravity() * 100));
                lengthS.setValue((int) (PO.getLength() * 1000));
                initAngleS.setValue(angleInt);

                variableChanged = false;

                //pushes the old values to the redo stack and allows the redo button to be pressed
                undoStack.push(new pendulumObj(POtemp.getLength(), POtemp.getVelocity(), POtemp.getInitialVelocity(), POtemp.getGravity(), POtemp.getAngle(), POtemp.getInitialAngle(), POtemp.getDt()));

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

    //Getter Methods

    public Object getPendulumObject(){
        return PO;
    }

    //main method sets the size and tits of the frame.
    public static void main(String[] args){
        Pendulum pendulum = new Pendulum();
        pendulum.setVisible(true);
        pendulum.setSize(800,800);
        pendulum.setExtendedState(JFrame.MAXIMIZED_BOTH);
        pendulum.setTitle("Pendulum");
        pendulum.setLocation(100, 50);
    }
}

class pendulumObj{
    //local variables of the pendulum object
    private double length, velocity, gravity, angle, dt, initialAngle, initialVelocity;

    public pendulumObj(double len, double vel, double initVel, double grav, double ang, double initAng, double DT){
        length = len;
        velocity = vel;
        gravity = grav;
        angle = ang;
        dt = DT;
        initialAngle = initAng;
        initialVelocity = initVel;
    }

    //getter methods for the pendulum object
    public double getLength(){
        return length;
    }

    public double getVelocity(){
        return velocity;
    }

    public double getGravity(){
        return gravity;
    }

    public double getAngle(){
        return angle;
    }

    public double getDt(){
        return dt;
    }

    public double getInitialVelocity(){
        return initialVelocity;
    }

    public double getInitialAngle(){return initialAngle;}

    //setter methods for the pendulum object
    public void setLength(double len){
        length = len;
    }

    public void setVelocity(double vel){
        velocity = vel;
    }

    public void setGravity(double grav){
        gravity = grav;
    }

    public void setAngle(double ang){
        angle = ang;
    }

    public void setDt(double DT){
        dt = DT;
    }

    public void setInitialAngle(double ang){
        initialAngle = ang;}

    public void setInitialVelocity(double velocity){
        initialVelocity = velocity;
    }
}
