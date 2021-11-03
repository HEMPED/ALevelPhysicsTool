import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;

public class Load extends Save {
    int returnValue = 0;
    boolean fileChosen = false;

    public Load(){
        fileChosen = false;
    }

    @Override
    public void openExplorer(){
        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
        chooser.setFileFilter(filter);

        returnValue = chooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            directory = chooser.getSelectedFile();
            System.out.println(directory.getAbsolutePath());
            fileChosen = true;
        }
    }

    public boolean getFileChosen(){
        return fileChosen;
    }

    public static void main(String[] args){
    }
}
