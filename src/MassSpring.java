import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MassSpring extends JFrame {
    //time used to calculate displacement
    double time;
    long startTime;
    long currentTime;

    //button used for text field inputs
    JButton extraB;
    //doubles used to store the temporary information of the text fields
    double springConstantTF, displacementTF, lengthTF, massTF, extensionTF;
    //boolean that allows the program to save values from the text fields
    boolean TFChanged = false;

    //declare variables that are used for the sliders
    JPanel sliderPanel;
    JSlider massS, extensionS, springConstantS;
    JLabel massSL, extensionSL, springConstantSL;
    boolean sliderChanged = false;

    //declare variables that are used for the menu bar
    JMenuBar menuBar;
    JButton saveB, loadB;

    //object used to store the variables of the system
    MassSpringObj MSO = new MassSpringObj(20, 3, 7, 2, 3);

    //declare stacks and buttons for the undo and redo function
    protected Stack<MassSpringObj> undoStack = new Stack<>();
    protected Stack<MassSpringObj> redoStack = new Stack<>();
    JButton undoB, redoB;

    public MassSpring(){
        //sets the layout of the whole frame
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,3,3,3);

        //menuBar used for save and load functions
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

        //creating the slider panel and all the sliders and corresponding labels
        sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        sliderPanel.setBackground(Color.white);
        sliderPanel.setPreferredSize(new Dimension(350, 300));

        massS = new JSlider(JSlider.HORIZONTAL, 0, 1000, 200);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        sliderPanel.add(massS, c);

        extensionS = new JSlider(JSlider.HORIZONTAL, -700, 700, 300);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        sliderPanel.add(extensionS, c);

        springConstantS = new JSlider(JSlider.HORIZONTAL, 1, 200, 20);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        sliderPanel.add(springConstantS, c);

        massSL = new JLabel("Mass: 2kg");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        sliderPanel.add(massSL, c);

        extensionSL = new JLabel("Extension: 3.0cm");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        sliderPanel.add(extensionSL, c);

        springConstantSL = new JLabel("<HTML>Spring Constant<br>20N/m</html>");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        sliderPanel.add(springConstantSL, c);

        extraB = new JButton("More Inputs");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 8;
        sliderPanel.add(extraB, c);

        //add the action listeners to the sliders/buttons
        massSChanged MSC = new massSChanged();
        massS.addChangeListener(MSC);

        extensionSChanged ESC = new extensionSChanged();
        extensionS.addChangeListener(ESC);

        springConstantSChanged SCSC = new springConstantSChanged();
        springConstantS.addChangeListener(SCSC);

        extraButtonPressed EBP = new extraButtonPressed();
        extraB.addActionListener(EBP);

        //create the extra panel and all the buttons that go with it
        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new GridBagLayout());
        extraPanel.setBackground(Color.white);

        undoB = new JButton("Undo");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        extraPanel.add(undoB, c);

        //sets up buttons used for the undo and redo functions
        undoB.setEnabled(false);
        undoButtonPressed UBP = new undoButtonPressed();
        undoB.addActionListener(UBP);

        redoB = new JButton("Redo");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        extraPanel.add(redoB, c);

        redoB.setEnabled(false);
        redoButtonPressed RBP = new redoButtonPressed();
        redoB.addActionListener(RBP);

        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 1;
        c.gridy = 2;
        add(extraPanel, c);

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        add(sliderPanel, c);

        MassSpringPanel massSpringPanel = new MassSpringPanel();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        add(massSpringPanel, c);
        new Thread(massSpringPanel).start();
    }


    public class MassSpringPanel extends JPanel implements Runnable {
        //stores the "corners" of the spring once it has been turned into a 2d object
        ArrayList<Point> points = new ArrayList<>();

        public MassSpringPanel() {
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(100000, 600));
        }

        @Override
        public void paint(Graphics g) {
            //antialiasing used to smooth out the diagonal parts of the spring
            Graphics2D g1 = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g1.setRenderingHints(rh);

            //draws the background
            g1.setColor(Color.white);
            g1.fillRect(0, 0, getWidth(), getHeight());

            g1.setColor(Color.black);

            //connects each point
            for (int x = 0; x < points.size(); x++) {
                Point p = points.get(x);

                int xVal = (int) p.getX();
                int yVal = (int) p.getY();

                if (x != 0) {
                    Point prevPoint = points.get(x - 1);

                    int prevXVal = (int) prevPoint.getX();
                    int prevYVal = (int) prevPoint.getY();

                    g1.drawLine(prevXVal, prevYVal, xVal, yVal);
                }

                //draws the mass hanging off the spring
                if (x == points.size() - 1) {
                    g1.drawLine(xVal, yVal, xVal, yVal + 10);
                    g1.setColor(Color.red);
                    g1.fillRect((xVal - 20), yVal + 10, 40, 40);
                }
            }

            //overlay
            int overlayAnchorX = getWidth() - 150;
            int overlayAnchorY = 15;

            g.setColor(Color.BLACK);

            double displacement = Math.round(MSO.getDisplacement() * 100) / 100.0;
            double time2 = time % MSO.getTimePeriod();
            time2 = Math.round(time2 * 100) / 100.0;
            g.drawString("Displacement: " + displacement + "cm",overlayAnchorX, overlayAnchorY);
            g.drawString("Time: " + time2 + "s",overlayAnchorX, overlayAnchorY + 15);
        }

        //method used to split the spring into a series of points, calculated from its extended or compressed length
        //method also calculates how the spring squeezes.
        private void calculatePoints(double length) {
            int pointX, pointY;
            int anchorX, anchorY;
            double changeInLength = length / 50;
            //used for the compression factor as the spring stretches.
            double displacementRatio = MSO.getDisplacement() / MSO.getAmplitude();
            displacementRatio = Math.abs(displacementRatio);

            anchorY = 30;

            points.clear();


            for (int x = 1; x < 50; x++) {

                if (MSO.getDisplacement() > 0) {
                    anchorX = 100 + (int) (10 * displacementRatio);

                    if (x % 2 == 0) {
                        pointX = anchorX;
                    } else {
                        pointX = anchorX + 100 - (int) (20 * displacementRatio);
                    }

                    pointY = (int) (anchorY + changeInLength * x);

                    if (x == 1) {
                        int tempX = pointX - (pointX - anchorX) / 2;
                        points.add(new Point(tempX, (anchorY - 15)));
                        points.add(new Point(anchorX, anchorY));
                    }
                } else {
                    anchorX = 100;
                    if (x % 2 == 0) {
                        pointX = anchorX;
                    } else {
                        pointX = anchorX + 100;
                    }

                    pointY = (int) (anchorY + changeInLength * x);

                    if (x == 1) {
                        int tempX = anchorX + 50;
                        points.add(new Point(tempX, (anchorY - 15)));
                        points.add(new Point(anchorX, anchorY));
                    }
                }
                points.add(new Point(pointX, pointY));

                if (x == 49) {
                    int tempX = pointX - (pointX - anchorX) / 2;
                    points.add(new Point(tempX, (pointY + 15)));
                }
            }
        }

        //time period determines the frequency of the spring
        private void calculateTimePeriod() {
            double timePeriod = 2 * Math.PI * Math.pow((MSO.getMass() / MSO.getSpringConstant()), 0.5);
            double angularVelocity = (2 * Math.PI) / timePeriod;
            MSO.setTimePeriod(timePeriod);
            MSO.setAngularVelocity(angularVelocity);
        }

        //uses the cos() function and the time period to calculate where the spring is in its cycle.
        private void calculateDisplacement() {
            double displacement = MSO.getAmplitude() * Math.cos(MSO.getAngularVelocity() * time);
            MSO.setDisplacement(displacement);
        }

        public void run() {
            startTime = System.nanoTime();

            while (true) {
                //changes the simulation and pushes previous values to the undo stack if the sliders have been moved
                if (sliderChanged) {
                    //resets the timer
                    startTime = System.nanoTime();

                    //pushes old values to the undo stack
                    undoStack.push(new MassSpringObj(MSO.getSpringConstant(), MSO.getDisplacement(), MSO.getLength(), MSO.getMass(), MSO.getAmplitude()));
                    undoB.setEnabled(true);

                    //gets new values
                    double newMass = massS.getValue() / 100.0;
                    double newExtension = extensionS.getValue() / 100.0;

                    //changes the simulation
                    MSO.setMass(newMass);
                    MSO.setDisplacement(newExtension);
                    MSO.setAmplitude(newExtension);
                    MSO.setSpringConstant(springConstantS.getValue());

                    //resets the flag
                    sliderChanged = false;
                }

                if(TFChanged){
                    //pushes old values to the undo stack
                    undoStack.push(new MassSpringObj(MSO.getSpringConstant(), MSO.getDisplacement(), MSO.getLength(), MSO.getMass(), MSO.getAmplitude()));
                    undoB.setEnabled(true);

                    //changes the sliders
                    massS.setValue((int) (massTF * 100));
                    extensionS.setValue((int) (extensionTF * 100));
                    springConstantS.setValue((int) springConstantTF);
                    sliderChanged = false;

                    //changes the values of the simulation
                    MSO.setSpringConstant(springConstantTF);
                    MSO.setMass(massTF);
                    MSO.setDisplacement(displacementTF);
                    MSO.setLength(lengthTF);
                    MSO.setAmplitude(extensionTF);

                    //gets the current time
                    double angularVelocity = Math.pow((springConstantTF / massTF), 0.5);

                    double time = displacementTF / extensionTF;
                    time = Math.acos(time);
                    time = time / angularVelocity;
                    startTime = currentTime - (long) (time * 1000000000);

                    //resets the flag
                    TFChanged = false;
                }

                //only calculates motion if the mass isn't 0  to avoid errors
                if (MSO.getMass() != 0) {
                    //nanoTime used as it is more precise than getMillis()
                    currentTime = System.nanoTime();
                    time = (double) (currentTime - startTime) / 1000000000;

                    calculateTimePeriod();
                    calculateDisplacement();

                    calculatePoints((int) ((MSO.getLength() + MSO.getDisplacement()) * 50));

                } else {
                    //spring remains at its original length if no mass is attached
                    MSO.setDisplacement(0);
                    MSO.setAmplitude(0);
                }

                repaint();
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(this, "<HTML>Error running program, please restart</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public class extraInputPanel extends JPanel{
        //labels used to display information
        JLabel springConstantL, springConstantUnit, displacementL, displacementUnit, lengthL, lengthUnit, massL, massUnit, extensionL, extensionUnit;
        //text fields used to get inputs
        JTextField springConstantTF, displacementTF, lengthTF, massTF, extensionTF;

        JButton saveChanges;
        public extraInputPanel(){
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            MassSpring.this.springConstantTF = MSO.getSpringConstant();
            MassSpring.this.displacementTF = MSO.getDisplacement();
            MassSpring.this.lengthTF = MSO.getLength();
            MassSpring.this.massTF = MSO.getMass();
            MassSpring.this.extensionTF = MSO.getAmplitude();

            springConstantL = new JLabel("Spring Constant: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            add(springConstantL, c);

            springConstantTF = new JTextField("" + (Math.round(MSO.getSpringConstant() * 100) / 100));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            add(springConstantTF, c);

            springConstantUnit = new JLabel("N/m");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 0;
            add(springConstantUnit, c);

            displacementL = new JLabel("Displacement: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            add(displacementL, c);

            displacementTF = new JTextField("" + (Math.round(MSO.getDisplacement() * 100)) / 100);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            add(displacementTF, c);

            displacementUnit = new JLabel("cm");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 1;
            add(displacementUnit, c);

            lengthL = new JLabel("Length: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            add(lengthL, c);

            lengthTF = new JTextField("" + (Math.round(MSO.getLength() * 100) / 100));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 2;
            add(lengthTF, c);

            lengthUnit = new JLabel("cm");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 2;
            add(lengthUnit, c);

            massL = new JLabel("Mass: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;
            add(massL, c);

            massTF = new JTextField("" + (Math.round(MSO.getMass() * 100)/ 100));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 3;
            add(massTF, c);

            massUnit = new JLabel("kg");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 3;
            add(massUnit, c);

            extensionL = new JLabel("Extension: ");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 4;
            add(extensionL, c);

            extensionTF = new JTextField("" + (Math.round(MSO.getAmplitude() * 100) / 100));
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 4;
            add(extensionTF, c);

            extensionUnit = new JLabel("cm");
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 4;
            add(extensionUnit, c);

            saveChanges = new JButton("Save Changes");
            saveChangesPressed SCP = new saveChangesPressed();
            saveChanges.addActionListener(SCP);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 5;
            add(saveChanges, c);
        }

        public class saveChangesPressed implements ActionListener{
            double springConstantT, displacementT, lengthT, massT, amplitudeT;
            boolean notNumber, SCHigh, SCLow, DHigh, DLow, LHigh, LLow, MHigh, MLow, AHigh, ALow, isValidated = true;

            public void actionPerformed(ActionEvent event){
                try{
                    springConstantT = Double.parseDouble(springConstantTF.getText());
                    if(springConstantT < 0){
                        SCHigh = true;
                        isValidated = false;
                    } else {SCHigh = false;}
                    if(springConstantT > 200) {
                        SCLow = true;
                        isValidated = false;
                    } else {SCLow = false;}
                } catch (NumberFormatException NFE){
                    notNumber = true;
                    isValidated = false;
                }

                try{
                    lengthT = Double.parseDouble(lengthTF.getText());
                    if(lengthT < MSO.getDisplacement()){
                        LLow = true;
                        isValidated = false;
                    } else {LLow = false;}
                    if(lengthT > 15){
                        LHigh = true;
                        isValidated = false;
                    } else {LHigh = false;}
                }catch (NumberFormatException NFE) {
                    notNumber = true;
                    isValidated = false;
                }

                try{
                    amplitudeT = Double.parseDouble(extensionTF.getText());
                    if (amplitudeT < lengthT * -1) {
                        ALow = true;
                        isValidated = false;
                    }
                    if (amplitudeT > lengthT) {
                        AHigh = true;
                        isValidated = false;
                    }
                } catch (NumberFormatException NFE){
                    notNumber = true;
                    isValidated = false;
                }

                try{
                    displacementT = Double.parseDouble(displacementTF.getText());
                    if (displacementT < Math.abs(amplitudeT) * -1) {
                        DLow = true;
                        isValidated = false;
                    } else {
                        DLow = false;
                    }
                    if (displacementT > Math.abs(amplitudeT)) {
                        DHigh = true;
                        isValidated = false;
                    } else {

                        DHigh = false;
                    }
                }catch (NumberFormatException NFE){
                    notNumber = true;
                    isValidated = false;
                }

                try{
                    massT = Double.parseDouble(massTF.getText());
                    if(massT < 0){
                        MLow = true;
                        isValidated = false;
                    }
                    if(massT > 20){
                        MHigh = true;
                        isValidated = false;
                    }
                } catch (NumberFormatException NFE){
                    notNumber = true;
                    isValidated = false;
                }

                if(isValidated){
                    //stores the values
                    MassSpring.this.springConstantTF = springConstantT;
                    MassSpring.this.displacementTF = displacementT;
                    MassSpring.this.lengthTF = lengthT;
                    MassSpring.this.massTF = massT;
                    MassSpring.this.extensionTF = amplitudeT;

                    //resets the flags
                    notNumber = SCHigh = SCLow = DHigh = DLow = LLow = MHigh = MLow = AHigh = ALow = false;
                    TFChanged = true;
                } else {
                    extraInputPanel ep = new extraInputPanel();
                    if(notNumber){
                        JOptionPane.showMessageDialog(ep, "<HTML>Only numbers allowed</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        notNumber = false;
                    } else if(SCHigh || SCLow){
                        JOptionPane.showMessageDialog(ep, "<HTML>Spring constant must be between 1 and 200</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        SCHigh = SCLow = false;
                    } else if(DHigh || DLow){
                        JOptionPane.showMessageDialog(ep, "<HTML>Displacement must be lower than the extension</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        DHigh = DLow = false;
                    } else if (LLow){
                        JOptionPane.showMessageDialog(ep, "<HTML>Length must be positive</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        LLow = false;
                    } else if (LHigh){
                        JOptionPane.showMessageDialog(ep, "<HTML>Length must be less than 15</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        LHigh = false;
                    } else if (MLow){
                        JOptionPane.showMessageDialog(ep, "<HTML>Mass cannot be negative</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        MLow = false;
                    } else if (MHigh){
                        JOptionPane.showMessageDialog(ep, "<HTML>Mass cannot be more than 10</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        MHigh = false;
                    } else if (AHigh || ALow) {
                        JOptionPane.showMessageDialog(ep, "<HTML>Extension cannot be more than length</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
                        AHigh = ALow = false;
                    }
                }
            }
        }
    }

    //Change listeners for sliders change the value of their corresponding label and
    //the sliderChanged boolean to true so the simulation can be updated in the while loop
    public class massSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent massSChanged) {
            double newMass = (double) massS.getValue() / 100;
            massSL.setText("Mass: " + newMass + "kg");

            sliderChanged = true;
        }
    }

    public class extensionSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent extensionSChanged) {
            double newExtension = (double) extensionS.getValue() / 100;
            extensionSL.setText("Extension: " + newExtension + "cm");

            sliderChanged = true;
        }
    }

    public class springConstantSChanged implements ChangeListener{
        public void stateChanged(ChangeEvent springConstantSChanged) {
            double newSpringConstant = springConstantS.getValue();
            String newLabelString = "<HTML>Spring Constant:<br>" + newSpringConstant + "</br>N/m</html>";
            springConstantSL.setText(newLabelString);

            sliderChanged = true;
        }
    }

    //creates a new frame for the other inputs
    public class extraButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent extraButtonPressed){
            JFrame frame = new JFrame("Extra Inputs");
            frame.setLayout(new FlowLayout());
            extraInputPanel ep = new extraInputPanel();
            frame.add(ep);

            frame.pack();
            frame.setVisible(true);
        }
    }

    //allows the user to load a stored simulation with a .json file
    public class loadButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            Load load = new Load();
            load.openExplorer();
            if(load.getFileChosen()) {
                read(load.getDirectory());
            }
        }
    }

    //allows the user to store their simulation as a .json file
    public class saveButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            Save save = new Save();
            save.openExplorer();
            save.start(MSO);
        }
    }

    //action listeners for the undo and redo buttons
    public class undoButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent undoButtonPressed){
            if(!undoStack.empty()){
                //adds current values to the redo stack
                redoStack.push(new MassSpringObj(MSO.getSpringConstant(), MSO.getDisplacement(), MSO.getLength(), MSO.getMass(), MSO.getAmplitude()));

                //gets the old values
                MassSpringObj MSOtemp = undoStack.pop();
                if(!undoStack.empty()) {
                    MSOtemp = undoStack.pop();
                }
                //resets variables in the pendulum object to their old values
                MSO.setSpringConstant(MSOtemp.getSpringConstant());
                MSO.setAngularVelocity(MSOtemp.getAngularVelocity());
                MSO.setDisplacement(MSOtemp.getAmplitude());
                MSO.setLength(MSOtemp.getLength());
                MSO.setTimePeriod(MSOtemp.getTimePeriod());
                MSO.setAmplitude(MSOtemp.getAmplitude());
                MSO.setMass(MSOtemp.getMass());

                startTime = System.nanoTime();

                //resets sliders to their old values
                massS.setValue((int) (MSO.getMass() * 100));
                extensionS.setValue((int) (MSO.getAmplitude() * 100));
                springConstantS.setValue((int) (MSO.getSpringConstant()));
                sliderChanged = false;

                //disables the button if that was the last value in the stack
                if(undoStack.empty()){
                    undoB.setEnabled(false);
                }

                //enables the redo button
                redoB.setEnabled(true);
            }
        }
    }

    public class redoButtonPressed implements ActionListener{
        public void actionPerformed(ActionEvent redoButtonPressed){
            if(!redoStack.empty()){
                //adds current values to the undo stack
                undoStack.push(new MassSpringObj(MSO.getSpringConstant(), MSO.getDisplacement(), MSO.getLength(), MSO.getMass(), MSO.getAmplitude()));

                //gets the old values
                MassSpringObj MSOtemp = redoStack.pop();
                //resets variables in the pendulum object to their old values
                MSO.setSpringConstant(MSOtemp.getSpringConstant());
                MSO.setAngularVelocity(MSOtemp.getAngularVelocity());
                MSO.setDisplacement(MSOtemp.getAmplitude());
                MSO.setLength(MSOtemp.getLength());
                MSO.setTimePeriod(MSOtemp.getTimePeriod());
                MSO.setAmplitude(MSOtemp.getAmplitude());
                MSO.setMass(MSOtemp.getMass());

                startTime = System.nanoTime();

                //resets sliders to their old values
                massS.setValue((int) (MSO.getMass() * 100));
                extensionS.setValue((int) (MSO.getAmplitude() * 100));
                springConstantS.setValue((int) (MSO.getSpringConstant()));
                sliderChanged = false;

                //disables the button if that was the last value in the stack
                if(redoStack.empty()){
                    redoB.setEnabled(false);
                }

                //enables the undo button
                undoB.setEnabled(true);
            }
        }
    }

    //Method to retrieve values from the .json file
    public void read(File directory){
        try{
            ObjectMapper mapper = new ObjectMapper();

            //gets the values from file
            MassSpringObj MSOTemp = mapper.readValue(directory, MassSpringObj.class);

            //changes the simulation
            MSO.setSpringConstant(MSOTemp.getSpringConstant());
            MSO.setDisplacement(MSOTemp.getAmplitude());
            MSO.setLength(MSOTemp.getLength());
            MSO.setTimePeriod(MSOTemp.getTimePeriod());
            MSO.setAngularVelocity(MSOTemp.getAngularVelocity());
            MSO.setMass(MSOTemp.getMass());
            MSO.setAmplitude(MSOTemp.getAmplitude());

            startTime = System.nanoTime();
            undoStack.push(new MassSpringObj(MSO.getSpringConstant(), MSO.getDisplacement(), MSO.getLength(), MSO.getMass(), MSO.getAmplitude()));

            //sets the values of the sliders
            sliderChanged = false;
            massS.setValue((int) (MSO.getMass() * 100));
            extensionS.setValue((int) (MSO.getAmplitude() * 100));
            springConstantS.setValue((int) (MSO.getSpringConstant()));
            sliderChanged = true;

        } catch (IOException e) {
            //lets the user know if there was an error
            JOptionPane.showMessageDialog(this, "<HTML>Error reading from file. Check if you selected the correct file and retry</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    //sets up the window
    public static void main(String[] args){
        MassSpring frame = new MassSpring();
        frame.getContentPane().setBackground(Color.white);
        frame.setSize(new Dimension(800, 800));
        frame.setTitle("Mass Spring System");
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
}
