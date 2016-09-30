/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.fxmisc.richtext.CodeArea;

/**
 *
 * @author salesiopark
 */
public class FileIO {
    public static void openFile(Node node){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("python script", "*.py"),
            new ExtensionFilter("web scritp", "*.html", "*.js", "*.css"),
            new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(node.getScene().getWindow());
        if (selectedFile != null) {
            CodeArea codeArea = TabPaneCode.addTab(selectedFile.getName());
            readFile(selectedFile, codeArea);
//            System.out.println(selectedFile);
        }
    }
    
    public static void saveFile(Node node) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("python script", "*.py"),
            new ExtensionFilter("web scritp", "*.html", "*.js", "*.css"),
            new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(node.getScene().getWindow());
        if (selectedFile != null) {
            String code = TabPaneCode.getSelectedTabCode();
            storeFile(selectedFile, code);
        }
    }
    
     static private void readFile(File file, CodeArea codeArea){
        if(file != null){
            try{
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr); 
                //-------------------------------------
                int ci;
                StringBuilder sb = new StringBuilder();
                while( (ci=br.read()) != -1) {
                    sb.append(Character.toString((char)ci));
                    //System.out.println(ci);
                }
        
                fr.close();
                codeArea.replaceText(sb.toString());
//                Codewnd.refresh_with(sb.toString());
                
            } catch(IOException e) { // file not found
                System.out.println("File not found.");
            }
        }
    }
     
     static private void storeFile(File file, String code) {
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(file);
            fileWriter.write(code);
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
