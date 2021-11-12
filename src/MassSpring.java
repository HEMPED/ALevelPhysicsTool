import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.sql.Array;
import java.util.*;
import java.util.Timer;

public class MassSpring extends JFrame {
    JPanel sliderPanel;
    JSlider massS, gravityS, extensionS;
    JLabel massSL, gravitySL, extensionSL;
    MassSpringObj MSO = new MassSpringObj(15,9.81, 3, 7, 2, 3);

    public MassSpring(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3,3,3,3);

        sliderPanel = new JPanel();
        sliderPanel.setLayout(new GridBagLayout());
        sliderPanel.setBackground(Color.white);

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

        gravitySChanged GSC = new gravitySChanged();
        gravityS.addChangeListener(GSC);

        massSChanged MSC = new massSChanged();
        massS.addChangeListener(MSC);

        extensionSChanged ESC = new extensionSChanged();
        extensionS.addChangeListener(ESC);

        c.fill = GridBagConstraints.HORIZONTAL;
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
        double time;

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

        private void calculatePoints(int length){
            int pointX, pointY;
            int anchorX, anchorY;
            int changeInLength = length / 30;

            anchorX = 100;
            anchorY = 30;

            points.clear();

            points.add(new Point((anchorX + 50), (anchorY - 15)));
            points.add(new Point(anchorX, anchorY));

            for(int x = 1; x < 30; x++){
                if(x % 2 == 0){
                    pointX = anchorX;
                } else {
                    pointX = anchorX + 100;
                }

                pointY = anchorY + changeInLength * x;

                points.add(new Point(pointX, pointY));

                if(x == 29){
                    points.add(new Point((pointX - 50), (pointY + 15)));
                }
            }
        }

        private double calculateTimePeriod(){
            double timePeriod = 2 * Math.PI * Math.pow((MSO.getMass() / MSO.getSpringConstant()), 0.5);
            double angularVelocity = (2 * Math.PI) / timePeriod;
            MSO.setAngularVelocity(angularVelocity);

            return timePeriod;
        }

        private void calculateDisplacement(){
            double displacement = MSO.getAmplitude() * Math.cos(MSO.getAngularVelocity() * time);
            MSO.setDisplacement(displacement);
        }

        public void run() {
            long startTime = System.currentTimeMillis();
            long currentTime;

            while(true) {
                currentTime = System.currentTimeMillis();
                time = (double) (currentTime - startTime) / 1000;

                calculateTimePeriod();
                calculateDisplacement();

                calculatePoints((int) ((MSO.getLength() + MSO.getDisplacement()) * 50));

                repaint();
                try{
                    Thread.sleep(5);
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


    public static void main(String[] args){
        MassSpring frame = new MassSpring();
        frame.getContentPane().setBackground(Color.white);
        frame.setSize(new Dimension(800, 800));
        frame.setTitle("Mass Spring System");
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
}
