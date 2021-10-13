import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class CreateFile{
    public CreateFile(){
    }

    public static void start(){
        JFrame frame = new JFrame();
        frame.setVisible(false);
        FileCreator fileCreator = new FileCreator();
        frame.add(fileCreator);
        fileCreator.openExplorer();

        if(fileCreator.getStatus() == false){
            System.exit(0);
        }
    }

    static class FileCreator extends FileChooser{
        @Override
        public void openExplorer(){
            chooser = new fileCreatorChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Create File");

            FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt", "text");
            chooser.addChoosableFileFilter(filter);
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
