import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileChooser extends JPanel {
    static JFileChooser chooser;
    boolean status = true;
    File directory;

    public FileChooser(){
    }

    public void openExplorer() {
        chooser = new JFileChooser();

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select File");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt", "text");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            directory = chooser.getSelectedFile();
        }

        status = false;
    }

    public boolean getStatus(){
        return status;
    }

    public static File getDirectory(){
        return directory;
    }
}