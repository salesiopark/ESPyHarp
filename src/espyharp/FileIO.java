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
            TabPy tabPy = TabPaneCode.addTab(selectedFile.getName());
            tabPy.setFile(selectedFile);
            readFile(selectedFile, tabPy.getCodeArea());
        }
    }
    
    public static void saveFile(Node node) {
        // 만약 TabPy에 fileSave가 지정이 되어 있다면 그 파일에 기록한다.
        TabPy tabPySelected = TabPaneCode.getSelectedTab();
        if (tabPySelected == null) {
            System.out.println("No selected tab");
            return;
        }
        File fileToSave = tabPySelected.getFile();
        if (fileToSave == null) {
                saveFileAs(node);
        } else { //만약 이미 지정된 파일이 있다면 그 파일에 저장한다.
            System.out.println("file saving using prev file ...");
            String code = TabPaneCode.getSelectedTabCode();
            storeFile(fileToSave, code);
        }
    }
    
    public static void saveFileAs(Node node) {
            TabPy tabPySelected = TabPaneCode.getSelectedTab();

            // save as 는 무조건 새파일을 지정받는다
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("python script", "*.py"),
                new ExtensionFilter("web scritp", "*.html", "*.js", "*.css"),
                new ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(node.getScene().getWindow());
            if (selectedFile != null) {
                //tabPy의 이름을 새로운 파일명으로 바꾸고 저장한다.
                tabPySelected.setText(selectedFile.getName());
                tabPySelected.setFile(selectedFile);
                String code = TabPaneCode.getSelectedTabCode();
                storeFile(selectedFile, code);
            }
    }
    
     static private void readFile(File file, CodeArea codeArea){
//        if(file != null){
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
//        }
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
