import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class MainMenu extends JFrame{
    //The suffix B means that it is the main button that links to the
    // program
    JButton pendulumB, massSpringB;
    JMenuBar menuBar;
    JMenu help;
    JMenuItem info;
    JFrame dialogueFrame;

    public MainMenu(){
        setLayout(new GridBagLayout());
        GridBagConstraints c;

        //Will contain all the buttons that link to the corresponding
        //section of the program
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        final Insets insets = new Insets(1,1,1,1);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        help = new JMenu("File");
        menuBar.add(help);

        info = new JMenuItem("More Information");
        help.add(info);

        infoPressed IP = new infoPressed();
        info.addActionListener(IP);

        pendulumB = new JButton("Pendulum");
        pendulumB.setPreferredSize(new Dimension(100,40));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 0;
        c.gridy = 0;
        buttonPanel.add(pendulumB,c);

        massSpringB = new JButton("<HTML>Mass Spring<br>System</html>");
        massSpringB.setPreferredSize(new Dimension(150,40));
        c = new GridBagConstraints();
        c.insets = insets;
        c.gridx = 1;
        c.gridy = 0;
        buttonPanel.add(massSpringB,c);

        pendulumBPressed PBP = new pendulumBPressed();
        pendulumB.addActionListener(PBP);

        massSpringBPressed MSBP = new massSpringBPressed();
        massSpringB.addActionListener(MSBP);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(buttonPanel,c);
    }

    //Action listener for the info button

    public class infoPressed implements ActionListener{
        public void actionPerformed(ActionEvent infoPressed){
            JOptionPane.showMessageDialog(dialogueFrame,
                    "Created by Hemanath Peddireddi", "More Information",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    //Action listeners for all buttons that open up sections of the program

    public class pendulumBPressed implements ActionListener{
        public void actionPerformed(ActionEvent pendulumBPressed){
            Pendulum p = new Pendulum();
            p.main(null);
        }
    }

    public class massSpringBPressed implements ActionListener{
        public void actionPerformed(ActionEvent massSpringBPressed){
            MassSpring ms = new MassSpring();
            ms.main(null);
        }
    }
    public static void main(String[] args){
        MainMenu mainMenu = new MainMenu();
        mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenu.setVisible(true);
        mainMenu.pack();
        mainMenu.setTitle("Simple Harmonic Motion");
        mainMenu.setLocation(540, 200);
    }
}
