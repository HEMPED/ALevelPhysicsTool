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
    }

    public static void start(){
        JFrame frame = new JFrame();
        frame.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        frame.add(fileChooser);
        fileChooser.openExplorer();

        if(fileChooser.getStatus() == false){
            System.exit(0);
        }
    }

    public static void main(String[] args){
        start();
    }
}
