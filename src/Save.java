import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;



public class Save extends JPanel {
    static File directory;
    String fileName;
    String directoryString = ".";

    public Save(){
    }

    public void openExplorer() {
        JFrame parentWindow = (JFrame) SwingUtilities.getWindowAncestor(this);
        System.setProperty("com.apple.macos.use-file-dialog-packages", "true");
        FileDialog fileChooser = new FileDialog(parentWindow, "Create File", FileDialog.SAVE);
        if (directoryString != null && directoryString != "") {
            fileChooser.setDirectory(directoryString);
        }


        FilenameFilter filter = (dir, name) -> name.endsWith(".json");
        fileChooser.setFilenameFilter(filter);
        fileChooser.setVisible(true);

        fileName =fileChooser.getFile();
        if (fileName == null)
            return;
        directoryString = fileChooser.getDirectory();
        if(directoryString == null) {
            directoryString = "";
        }
        else {
            fileName = directoryString+fileName;
        }

        directory = new File(fileName);
    }

    public static void write(File directory, Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(directory, obj);
    }

    private String getExtension() {
        if(directory!=null) {
            String name = directory.getName();
            int lastIndexOf = name.lastIndexOf(".");
            if (lastIndexOf == -1) {
                return "";
            }
            return name.substring(lastIndexOf);
        } else {
            return "";
        }
    }

    private void renameFile(){
        int i = directory.getName().lastIndexOf(".");
        if(i == -1){
            directoryString = directory.getName();
        } else {
            directoryString = directory.getName().substring(0,i);
        }
        directory = new File(directory.getParent(), directoryString + ".json");
        System.out.println(directory);
    }

    public static File getDirectory(){
        return directory;
    }

    public void start(Object obj){
        if (getExtension().equals("json")) {
            try {
                write(directory, obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            renameFile();
            try {
                write(directory, obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        Save s = new Save();
        s.openExplorer();
    }
}