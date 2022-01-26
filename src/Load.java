import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Load extends Save {
    //return value allows the program to know if a file is selected
    int returnValue = 0;
    boolean fileChosen;

    public Load(){
        fileChosen = false;
    }

    //opens a JFileChooser instead of a JFileDialog
    @Override
    public void openExplorer(){
        JFileChooser chooser = new JFileChooser(".");

        //Makes it so the user can only select .json files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON", "json");
        chooser.setFileFilter(filter);

        returnValue = chooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            directory = chooser.getSelectedFile();
            fileChosen = true;
        }
    }

    public boolean getFileChosen(){
        return fileChosen;
    }
}
