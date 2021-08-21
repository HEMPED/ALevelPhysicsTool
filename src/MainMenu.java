import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class MainMenu extends JFrame{
    //asf
    JButton pendulumB, massSpringB, button3, pendulumE, massSpringE, placeholderE;
    JLabel pendulumI, massSpringI, label3;
    JMenuBar menuBar;
    JMenu help;
    JMenuItem info;
    JFrame dialogueFrame;

    public MainMenu(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridBagLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new GridBagLayout());

        final Insets insets = new Insets(1,1,1,1);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        help = new JMenu("File");
        menuBar.add(help);

        info = new JMenuItem("More Information");
        help.add(info);

        infoPressed IP = new infoPressed();
        info.addActionListener(IP);

        String pendulumURL = "pendulum.png";
        ImageIcon pendulumIcon = new ImageIcon(pendulumURL);
        Image pendulumImage = pendulumIcon.getImage();
        Image newPendulumImage = pendulumImage.getScaledInstance(100,84, Image.SCALE_SMOOTH);
        pendulumIcon = new ImageIcon(newPendulumImage);

        pendulumI = new JLabel(pendulumIcon, JLabel.CENTER);
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        imagePanel.add(pendulumI, c);

        String massSpringURL = "massSpring.png";
        ImageIcon massSpringIcon = new ImageIcon(massSpringURL);
        Image massSpringImage = massSpringIcon.getImage();
        Image newMassSpringImage = massSpringImage.getScaledInstance(150,84, Image.SCALE_SMOOTH);
        massSpringIcon = new ImageIcon(newMassSpringImage);

        massSpringI = new JLabel(massSpringIcon, JLabel.CENTER);
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 1;
        c.gridy = 0;
        imagePanel.add(massSpringI, c);

        String placeholderURL = "placeholder.png";
        ImageIcon placeholderIcon = new ImageIcon(placeholderURL);
        Image placeholderImage = placeholderIcon.getImage();
        Image newplaceholderImage = placeholderImage.getScaledInstance(120,84, Image.SCALE_SMOOTH);
        placeholderIcon = new ImageIcon(newplaceholderImage);

        label3 = new JLabel(placeholderIcon, JLabel.CENTER);
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 2;
        c.gridy = 0;
        imagePanel.add(label3, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(imagePanel,c);

        pendulumB = new JButton("Pendulum");
        pendulumB.setPreferredSize(new Dimension(100,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        buttonPanel.add(pendulumB,c);

        massSpringB = new JButton("<HTML>Mass Spring<br>System</html>");
        massSpringB.setPreferredSize(new Dimension(150,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 1;
        c.gridy = 0;
        buttonPanel.add(massSpringB,c);

        button3 = new JButton("Placeholder");
        button3.setPreferredSize(new Dimension(120,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 2;
        c.gridy = 0;
        buttonPanel.add(button3, c);

        pendulumBPressed PBP = new pendulumBPressed();
        pendulumB.addActionListener(PBP);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(buttonPanel,c);

        pendulumE = new JButton("Example");
        pendulumE.setPreferredSize(new Dimension(100,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        extraPanel.add(pendulumE, c);

        massSpringE = new JButton("Example");
        massSpringE.setPreferredSize(new Dimension(150,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 1;
        c.gridy = 0;
        extraPanel.add(massSpringE, c);

        placeholderE = new JButton("Example");
        placeholderE.setPreferredSize(new Dimension(120,30));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 2;
        c.gridy = 0;
        extraPanel.add(placeholderE, c);

        pendulumEPressed PEP = new pendulumEPressed();
        pendulumE.addActionListener(PEP);

        massSpringEPressed MSEP = new massSpringEPressed();
        massSpringE.addActionListener(MSEP);

        placeholderEPressed PHEP = new placeholderEPressed();
        placeholderE.addActionListener(PHEP);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        add(extraPanel,c);
    }

    public class infoPressed implements ActionListener{
        public void actionPerformed(ActionEvent infoPressed){
            JOptionPane.showMessageDialog(dialogueFrame, "Created by Hemanath Peddireddi", "More Information", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public class pendulumBPressed implements ActionListener{
        public void actionPerformed(ActionEvent pendulumBPressed){
            Pendulum p = new Pendulum();
            p.main(null);
        }
    }

    public class pendulumEPressed implements ActionListener{
        public void actionPerformed(ActionEvent pendulumEPressed){
            try {
                Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=G73CSDKFN-g").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class massSpringEPressed implements ActionListener{
        public void actionPerformed(ActionEvent massSpringEPressed){
            try {
                Desktop.getDesktop().browse(new URL("https://www.youtube.com/watch?v=FJBPNJR2QJU").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class placeholderEPressed implements ActionListener{
        public void actionPerformed(ActionEvent placeholderEPressed){
            try {
                Desktop.getDesktop().browse(new URL("https://youtu.be/zuLJZylmJB4").toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        MainMenu mainMenu = new MainMenu();
        mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenu.setVisible(true);
        mainMenu.setSize(420,250);
        mainMenu.setTitle("Main Menu");
        mainMenu.setLocation(540, 200);
    }
}
