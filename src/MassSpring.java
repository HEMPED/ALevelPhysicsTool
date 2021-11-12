import javax.swing.*;
import java.awt.*;
import java.sql.Array;
import java.util.*;

public class MassSpring extends JFrame {
    JPanel sliderPanel;
    JSlider massS, gravityS, extensionS;
    JLabel massSL, gravitySL, extensionSL;
    int length = 100;

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
        c.gridx = 1;
        c.gridy = 0;
        sliderPanel.add(massS, c);

        extensionS = new JSlider(JSlider.HORIZONTAL, -700, 700, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        sliderPanel.add(extensionS, c);

        gravitySL = new JLabel("Gravity: 9.81m/s");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        sliderPanel.add(gravitySL, c);

        massSL = new JLabel("Mass: 2kg");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        sliderPanel.add(massSL, c);

        extensionSL = new JLabel("Extension: 0m");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        sliderPanel.add(extensionSL, c);

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
        ArrayList<Point> points = new ArrayList<Point>();

        public MassSpringPanel(){
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(100000, 600));
        }

        @Override
        public void paint(Graphics g){
            g.setColor(Color.white);
            g.fillRect(0,0,getWidth(),getHeight());

            g.setColor(Color.black);
            for(int x = 0; x < points.size(); x++){
                Point p = points.get(x);

                int xVal = (int) p.getX();
                int yVal = (int) p.getY();

                if(x != 0) {
                    Point prevPoint = points.get(x - 1);

                    int prevXVal = (int) prevPoint.getX();
                    int prevYVal = (int) prevPoint.getY();

                    g.drawLine(prevXVal, prevYVal, xVal, yVal);
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

            points.add(new Point(anchorX, anchorY));

            for(int x = 1; x < 30; x++){
                if(x % 2 == 0){
                    pointX = anchorX;
                } else {
                    pointX = anchorX + 100;
                }

                pointY = anchorY + changeInLength * x;

                points.add(new Point(pointX, pointY));
            }
        }

        public void run() {
            while(true) {
                repaint();
                calculatePoints(length);

                if(length < 600){
                    while(length < 600) {
                        length = length + 2;
                        calculatePoints(length);
                        repaint();
                        try{
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                        }
                    }
                } else {
                    while(length > 100) {
                        length = length - 2;
                        calculatePoints(length);
                        repaint();
                        try{
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                        }
                    }
                }

                try{
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static void main(String[] args){
        MassSpring frame = new MassSpring();
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Mass Spring System");
        frame.setLocation(0, 0);
        frame.setVisible(true);
    }
}
