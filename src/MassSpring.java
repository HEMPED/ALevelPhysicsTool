import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
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

public class MassSpring extends JFrame {
    //time used to calculate displacement
    double time;

    //declare variables that are used for the sliders
    JPanel sliderPanel;
    JSlider massS, gravityS, extensionS, springConstantS;
    JLabel massSL, gravitySL, extensionSL, springConstantSL;

    //declare variables that are used for the menu bar
    JMenuBar menuBar;
    JButton saveB, loadB;

    //object used to store the variables of the system
    MassSpringObj MSO = new MassSpringObj(15,9.81, 3, 7, 2, 3);

    //declare stacks and buttons for the undo and redo function
    protected Stack<MassSpringObj> undoStack = new Stack<>();
    protected Stack<MassSpringObj> redoStack = new Stack<>();
    JButton undoB, redoB;

    public MassSpring(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,3,3,3);

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

        sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        sliderPanel.setBackground(Color.white);
        sliderPanel.setPreferredSize(new Dimension(300, 300));

        gravityS = new JSlider(JSlider.HORIZONTAL, 0, 2000, 981);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        sliderPanel.add(gravityS, c);

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

        gravitySL = new JLabel("Gravity: 9.81N/kg");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        sliderPanel.add(gravitySL, c);

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

        springConstantSL = new JLabel("<HTML>Spring Constant<br>20N/kg</html>");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 7;
        sliderPanel.add(springConstantSL, c);

        gravitySChanged GSC = new gravitySChanged();
        gravityS.addChangeListener(GSC);

        massSChanged MSC = new massSChanged();
        massS.addChangeListener(MSC);

        extensionSChanged ESC = new extensionSChanged();
        extensionS.addChangeListener(ESC);

        springConstantSChanged SCSC = new springConstantSChanged();
        springConstantS.addChangeListener(SCSC);

        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1;
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


    public class MassSpringPanel extends JPanel implements Runnable{
        ArrayList<Point> points = new ArrayList<>();

        public MassSpringPanel(){
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(100000, 600));
        }

        @Override
        public void paint(Graphics g){
            Graphics2D g1 = (Graphics2D)g;
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g1.setRenderingHints(rh);

            g1.setColor(Color.white);
            g1.fillRect(0,0,getWidth(),getHeight());

            g1.setColor(Color.black);

            for(int x = 0; x < points.size(); x++){
                Point p = points.get(x);

                int xVal = (int) p.getX();
                int yVal = (int) p.getY();

                if(x != 0) {
                    Point prevPoint = points.get(x - 1);

                    int prevXVal = (int) prevPoint.getX();
                    int prevYVal = (int) prevPoint.getY();

                    g1.drawLine(prevXVal, prevYVal, xVal, yVal);
                }

                if(x == points.size() - 1){
                    g1.drawLine(xVal, yVal, xVal, yVal + 10);
                    g1.setColor(Color.red);
                    g1.fillRect((xVal - 20), yVal + 10, 40, 40);
                }
            }

        }

        private void calculatePoints(double length){
            int pointX, pointY;
            int anchorX, anchorY;
            double changeInLength = length / 50;
            double displacementRatio = MSO.getDisplacement() / MSO.getAmplitude();
            displacementRatio = Math.abs(displacementRatio);

            anchorX = 100;
            anchorY = 30;

            points.clear();


            for(int x = 1; x < 50; x++){

                if(MSO.getDisplacement() > 0) {
                    anchorX = 100 + (int) (10 * displacementRatio);

                    if (x % 2 == 0) {
                        pointX = anchorX;
                    } else {
                        pointX = anchorX + 100 - (int) (20 * displacementRatio);
                    }

                    pointY = (int) (anchorY + changeInLength * x);

                    if(x == 1){
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

                    if(x == 1){
                        int tempX = anchorX + 50;
                        points.add(new Point(tempX, (anchorY - 15)));
                        points.add(new Point(anchorX, anchorY));
                    }
                }
                points.add(new Point(pointX, pointY));

                if(x == 49){
                    int tempX = pointX - (pointX - anchorX) / 2;
                    points.add(new Point(tempX, (pointY + 15)));
                }
            }
        }

        private void calculateTimePeriod(){
            double timePeriod = 2 * Math.PI * Math.pow((MSO.getMass() / MSO.getSpringConstant()), 0.5);
            double angularVelocity = (2 * Math.PI) / timePeriod;
            MSO.setAngularVelocity(angularVelocity);
        }

        private void calculateDisplacement(){
            double displacement = MSO.getAmplitude() * Math.cos(MSO.getAngularVelocity() * time);
            MSO.setDisplacement(displacement);
        }

        public void run() {
            long startTime = System.nanoTime();
            long currentTime;

            while(true) {
                if(MSO.getMass() != 0) {
                    currentTime = System.nanoTime();
                    time = (double) (currentTime - startTime) / 1000000000;

                    calculateTimePeriod();
                    calculateDisplacement();

                    calculatePoints((int) ((MSO.getLength() + MSO.getDisplacement()) * 50));

                }else{
                    MSO.setDisplacement(0);
                }

                repaint();
                try{
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public class gravitySChanged implements ChangeListener{

        public void stateChanged(ChangeEvent e) {
            double newGravity = (double) gravityS.getValue() / 100;
            gravitySL.setText("Gravity: " + newGravity + "N/kg");

            MSO.setGravity(newGravity);
        }
    }

    public class massSChanged implements ChangeListener{

        public void stateChanged(ChangeEvent e) {
            double newMass = (double) massS.getValue() / 100;
            massSL.setText("Mass: " + newMass + "kg");

            MSO.setMass(newMass);
        }
    }

    public class extensionSChanged implements ChangeListener{

        public void stateChanged(ChangeEvent e) {
            double newExtension = (double) extensionS.getValue() / 100;
            extensionSL.setText("Extension: " + newExtension + "cm");

            MSO.setAmplitude(newExtension);
            MSO.setDisplacement(newExtension);
        }
    }

    public class springConstantSChanged implements ChangeListener{

        public void stateChanged(ChangeEvent e) {
            double newSpringConstant = springConstantS.getValue();
            String newLabelString = "<HTML>Spring Constant:<br>" +newSpringConstant+ "</br>N/kg</html>";
            springConstantSL.setText(newLabelString);

            MSO.setSpringConstant(newSpringConstant);
        }
    }

    public class loadButtonPressed implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            Load load = new Load();
            load.openExplorer();
            if(load.getFileChosen() == true) {
                read(load.getDirectory());
            }
        }
    }

    public class saveButtonPressed implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            Save save = new Save();
            save.openExplorer();
            save.start(MSO);
        }
    }

    //Method to read values from file
    public void read(File directory){
        try{
            ObjectMapper mapper = new ObjectMapper();

            //gets the values from file
            MassSpringObj MSOTemp = mapper.readValue(directory, MassSpringObj.class);

            //changes the simulation
            MSO.setSpringConstant(MSOTemp.getSpringConstant());
            MSO.setGravity(MSOTemp.getGravity());
            MSO.setDisplacement(MSOTemp.getAmplitude());
            MSO.setLength(MSOTemp.getLength());
            MSO.setTimePeriod(MSOTemp.getTimePeriod());
            MSO.setAngularVelocity(MSOTemp.getAngularVelocity());
            MSO.setAcceleration(MSOTemp.getAcceleration());
            MSO.setMass(MSOTemp.getMass());
            MSO.setAmplitude(MSOTemp.getAmplitude());

            time = 0;

            //sets the values of the sliders
            massS.setValue((int) (MSO.getMass() * 100));
            gravityS.setValue((int) (MSO.getGravity() * 100));
            extensionS.setValue((int) (MSO.getAmplitude() * 100));
            springConstantS.setValue((int) (MSO.getSpringConstant()));

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "<HTML>Error reading from file. Check if you selected the correct file and retry</HTML>", "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }


    public static void main(String[] args){
        MassSpring frame = new MassSpring();
        frame.getContentPane().setBackground(Color.white);
        frame.setSize(new Dimension(800, 800));
        frame.setTitle("Mass Spring System");
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
}
