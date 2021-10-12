import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class OpenFile {

    public OpenFile(){
        JFrame frame = new JFrame();
        frame.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        frame.add(fileChooser);
        fileChooser.openExplorer();

        if(fileChooser.getStatus() == false){
            System.exit(0);
        }
    }

    static class FileChooser extends JPanel {
        static JFileChooser chooser;
        boolean status = true;
        File directory;

        public FileChooser(){
        }

        public void openExplorer() {
            chooser = new JFileChooser();

            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select Directory To Read From");

            FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt", "text");
            chooser.setFileFilter(filter);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                directory = chooser.getSelectedFile();
                System.out.println("" + directory);
            }

            status = false;
        }

        public boolean getStatus(){
            return status;
        }

        public File getDirectory(){
            return directory;
        }
    }

    public static void main(String[] args){

    }
}
