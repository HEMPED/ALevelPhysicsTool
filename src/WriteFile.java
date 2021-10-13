import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class WriteFile {
    static Scanner in = new Scanner(System.in);
    public WriteFile(){

    }

    public static void start(String directory, String strToWrite){
        try {
            FileWriter myWriter = new FileWriter(directory);
            myWriter.write(strToWrite);
            myWriter.close();
        }catch (IOException e){e.printStackTrace();}
    }

    public static void main(String[] args){

    }
}
