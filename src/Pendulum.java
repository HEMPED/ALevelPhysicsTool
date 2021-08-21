import jdk.jfr.consumer.RecordedObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class Pendulum extends JFrame {
    public double angle = Math.PI/4;
    public int length = 500;
    public double dt = 0.1;
    JSlider gravityS, lengthS, initAngleS;
    JLabel gravitySL, lengthSL, initAngleSL;
    JButton extraButton;
    boolean variableChanged = false;
    boolean TFSaved = false;

    public Pendulum(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,3,3,3);

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());

        JPanel extraPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());

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

        initAngleS = new JSlider(JSlider.HORIZONTAL, 0,175, 45);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        sliderPanel.add(initAngleS, c);

        initAngleSL = new JLabel("Angle: 45 degrees");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        sliderPanel.add(initAngleSL, c);

        gravitySChanged GSC = new gravitySChanged();
        gravityS.addChangeListener(GSC);

        lengthSChanged LSC = new lengthSChanged();
        lengthS.addChangeListener(LSC);

        initAngleSChanged IASC = new initAngleSChanged();
        initAngleS.addChangeListener(IASC);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(sliderPanel,c);

        extraButton = new JButton("Click here for more inputs");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        extraPanel.add(extraButton,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        add(extraPanel, c);

        extraButtonPressed EBP = new extraButtonPressed();
        extraButton.addActionListener(EBP);

        PendulumPanel pendulumPanel = new PendulumPanel();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        add(pendulumPanel,c);
        new Thread(pendulumPanel).start();
    }

    public class PendulumPanel extends JPanel implements Runnable{
        public PendulumPanel(){
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(500,500));
            setDoubleBuffered(true);
        }

        @Override
        public void paint(Graphics g){
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setColor(Color.BLACK);

            int pointX = getWidth()/2, pointY = getHeight()/4;
            int pendulumX = pointX + (int) (Math.sin(angle) * length);
            int pendulumY = pointY + (int) (Math.cos(angle) * length);

            g.drawLine(pointX, pointY, pendulumX, pendulumY);
            g.fillOval(pointX - 3, pointY - 6,7,10);
            g.fillOval(pendulumX - 7, pendulumY - 7, 14, 14);
        }

        public void run(){
            double angleAccel = 0, angleVelocity = 0;
            double gravity = (double) gravityS.getValue() / 100;
            length = lengthS.getValue() / 10;

            calculate(gravity, angleAccel, angleVelocity, dt);
        }

        public void calculate(double gravity, double angleAccel, double angleVelocity, double dt){
            if(variableChanged) {
                gravity = (double) gravityS.getValue() / 100;
                length = lengthS.getValue() / 10;
                angle = initAngleS.getValue() / (180 / Math.PI);
                angleVelocity = 0;
                variableChanged = false;
            }
            angleAccel = (-1 * gravity) / length * Math.sin(angle);
            angleVelocity = angleVelocity + angleAccel * dt;
            angle = angle + angleVelocity * dt;
            repaint();

            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            calculate(gravity, angleAccel, angleVelocity, dt);
        }
    }

    public class extraInputPanel extends JPanel{
        JTextField gravityTF, lengthTF, initAngleTF, dtTF, initVelocityTF;
        JLabel gravityL, lengthL, initAngleL, dtL, initVelocityL;
        JLabel gravityUnit, lengthUnit, initAngleUnit, dtUnit, initVelocityUnit;
        JTextArea errors;
        JButton saveChanges;

        public extraInputPanel(){
            setLayout(new GridBagLayout());
            setBackground(Color.white);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3,3,3,3);

            gravityL = new JLabel("Gravity: ");
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

            double lengthTemp = lengthS.getValue() / 1000;
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

            dtTF = new JTextField("" + dt);
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
            c.gridy = 5;
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
            c.gridy = 6;
            add(errors, c);
        }

        public class saveChangesPressed implements ActionListener{
            boolean isValidated = true;
            double lengthT, gravityT, initAngleT;

            public void actionPerformed(ActionEvent saveChangesPressed){
                errors.setText("");

                try{
                    gravityT = Double.parseDouble(gravityTF.getText());
                } catch (NumberFormatException nfe){
                    if(errors.getText().equals("")) {
                        gravityT = 9.81;
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){

                    }else{
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }

                if(gravityT < 0){
                    if(errors.getText().equals("")) {
                        errors.setText("GRAVITY CANNOT BE LESS THAN 0");
                    }else{
                        errors.setText(errors.getText() + ", GRAVITY CANNOT BE LESS THAN 0");
                    }
                }

                if(gravityT > 20){
                    if(errors.getText().equals("")) {
                        errors.setText("GRAVITY CANNOT BE GREATER THAN 20");
                    }else{
                        errors.setText(errors.getText() + ", GRAVITY CANNOT BE GREATER THAN 20");
                    }
                }


                try{
                    lengthT = Double.parseDouble(lengthTF.getText());
                }catch (NumberFormatException nfe){
                    lengthT = 5;

                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){

                    }else{
                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }
                if(lengthT < 0){
                    if(errors.getText().equals("")) {
                        errors.setText("LENGTH CANNOT BE LESS THAN 0");
                    }else{
                        errors.setText(errors.getText() + ", LENGTH CANNOT BE LESS THAN 0");
                    }
                }
                if(lengthT > 10){
                    if(errors.getText().equals("")) {
                        errors.setText("LENGTH CANNOT BE GREATER THAN 10");
                    }else{
                        errors.setText(errors.getText() + ", LENGTH CANNOT BE GREATER THAN 10");
                    }
                }

                try{
                    initAngleT = Double.parseDouble(initAngleTF.getText());
                }catch (NumberFormatException NFE){
                    initAngleT = 45;
                    if(errors.getText().equals("")) {
                        errors.setText("NUMBERS ONLY");
                    }else if(errors.getText().contains("NUMBERS ONLY")){
                    }else{

                        errors.setText(errors.getText() + ", NUMBERS ONLY");
                    }
                }
                if(initAngleT < 0){
                    if(errors.getText().equals("")) {
                        errors.setText("INITIAL ANGLE CANNOT BE LESS THAN 0");
                    }else{
                        errors.setText(errors.getText() + ", INITIAL ANGLE CANNOT BE LESS THAN 0");
                    }
                }
                if(initAngleT > 175){
                    if(errors.getText().equals("")){
                        errors.setText("INITIAL ANGLE CANNOT BE GREATER THAN 175");
                    } else {
                        errors.setText(errors.getText() + ", INITIAL ANGLE CANNOT BE GREATER THAN 175");
                    }
                }

                TFSaved = true;
            }
        }
    }

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

    public static void main(String[] args){
        Pendulum pendulum = new Pendulum();
        pendulum.setVisible(true);
        pendulum.setSize(800,800);
        pendulum.setTitle("Pendulum");
        pendulum.setLocation(100, 50);
    }
}
