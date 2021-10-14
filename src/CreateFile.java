import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class CreateFile{
    static boolean fileCreated = false;
    static boolean canDelete = false;
    static boolean dialogLaunched = false;
    static boolean cancel = false;
    static FileCreator fileCreator = new FileCreator();

    public CreateFile(){
    }

    public static void start(){
        JFrame frame = new JFrame();
        frame.setVisible(false);
        frame.add(fileCreator);
        fileCreator.openExplorer();

        while(true) {
            if(cancel){
                frame.dispose();
                start();
                cancel = false;
            }
            try {
                File file = new File(String.valueOf(FileCreator.getDirectory()));
                if (file.createNewFile()) {
                    fileCreated = true;
                    break;
                } else {
                    if(canDelete){
                        file.delete();
                        System.out.println("can delete");
                        System.out.println(file);
                        canDelete = false;
                    }
                    if (!fileCreated) {
                        if(!dialogLaunched) {
                            JDialog dialog = new JDialog(frame);
                            dialog.setLayout(new GridBagLayout());
                            GridBagConstraints c = new GridBagConstraints();

                            JLabel dialogL = new JLabel("File Already Exists", JLabel.CENTER);
                            c.fill = GridBagConstraints.HORIZONTAL;
                            c.gridx = 0;
                            c.gridy = 0;
                            c.gridwidth = 2;
                            dialog.add(dialogL, c);

                            JButton proceed = new JButton("Proceed");
                            c.fill = GridBagConstraints.HORIZONTAL;
                            c.gridx = 0;
                            c.gridy = 1;
                            c.gridwidth = 1;

                            proceedPressed PP = new proceedPressed();
                            proceed.addActionListener(PP);

                            dialog.add(proceed, c);

                            JButton cancel = new JButton("Cancel");
                            c.fill = GridBagConstraints.HORIZONTAL;
                            c.gridx = 1;
                            c.gridy = 1;

                            cancelPressed CP = new cancelPressed();
                            cancel.addActionListener(CP);

                            dialog.add(cancel, c);

                            dialog.pack();
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                            System.out.println("exists");
                            dialogLaunched = true;
                        }
                    }

                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }

        //if(fileCreator.getStatus() == false){
        //    System.exit(0);
        //}
    }

    public static class cancelPressed implements ActionListener{
        public void actionPerformed(ActionEvent cancelPressed){
            cancel = true;
        }
    }

    public static class proceedPressed implements ActionListener{
        public void actionPerformed(ActionEvent proceedPressed){
            canDelete = true;
        }
    }

    static class FileCreator extends FileChooser{
        @Override
        public void openExplorer(){
            chooser = new fileCreatorChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Create File");

            FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt", "text");
            chooser.setFileFilter(filter);
            chooser.setFileSelectionMode(JFileChooser.APPROVE_OPTION);

            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                directory = chooser.getSelectedFile();
            }

            status = false;
        }

        static class fileCreatorChooser extends JFileChooser{
            public fileCreatorChooser(){
            }

            @Override
            public void approveSelection(){
                File file = getSelectedFile();
                if(file.exists() && getDialogType() == SAVE_DIALOG){

                } else {
                    super.approveSelection();
                }
            }
        }
    }

    public static void main(String[] args){
        start();
    }
}
