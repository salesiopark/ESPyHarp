/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author salesiopark
 */
public class FXMLDocController implements Initializable {
    
    @FXML private AnchorPane apCommWnd;
    @FXML private TabPane tpCode;
    @FXML private Button btnUpload;
    @FXML private Button btnWriteAndExec;
    @FXML private Button btnRefreshFiles;
    @FXML private Button btnConnect;
    @FXML private Button btnNewFile;
    @FXML private Button btnRemove;
    @FXML private Button btnExec;
    @FXML private Button btnReadFile;
    
    @FXML private Button btnRunMainPy;
    @FXML private Button btnFiles;
    
    @FXML private MenuItem menuItemOpen;
    @FXML private MenuItem menuItemSave;
    @FXML private MenuItem menuItemAbout;
    @FXML private MenuItem menuItemNewFile;
    @FXML private MenuItem menuItemSaveAs;
    
    @FXML private Label lblMsg;
    
    @FXML private ComboBox cbPort;
    
    @FXML private TreeView<String> treeViewFiles;
    @FXML private AnchorPane apFiles;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Uart.init(cbPort);
        REPL.init(apCommWnd);
        TabPaneCode.init(tpCode);
        TreeViewFile.init(apFiles);
//        TreeViewFile.readFileList();
        
        btnRunMainPy.setOnAction(event -> Uart.exec("exec(open('main.py').read(),globals())"));

        btnFiles.setOnAction(event -> Uart.exec("uos.listdir()"));
        btnRefreshFiles.setOnAction(event -> TreeViewFile.readFileList());
        
        btnUpload.setOnAction((ActionEvent event) -> {
            if (Uart.isNotOpened()) return;
            // 현재 선택된 tab을 뽑아낸 후 download()메서드를 호출한다.
            TabPy tapPySelected = (TabPy)( tpCode.getSelectionModel().getSelectedItem() );
            tapPySelected.upload();
        });

        btnWriteAndExec.setOnAction((ActionEvent event) -> {
            if (Uart.isNotOpened()) return;
            // 현재 선택된 tab을 뽑아낸 후 download()메서드를 호출한다.
            TabPy tapPySelected = (TabPy)( tpCode.getSelectionModel().getSelectedItem() );
            tapPySelected.uploadAndExec();
        });

        btnRemove.setOnAction(event -> TreeViewFile.removeSelected());

        menuItemOpen.setOnAction(event -> FileIO.openFile(tpCode));
        menuItemSave.setOnAction(event -> FileIO.saveFile(tpCode));
        menuItemSaveAs.setOnAction(event -> FileIO.saveFileAs(tpCode));

        menuItemAbout.setOnAction(event -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText(Var.strNameVer);
            alert.setContentText(Var.strAbout);
            alert.showAndWait();
        });
        menuItemNewFile.setOnAction(event->TabPaneCode.addNewFileTab());
        

        btnConnect.setOnAction(event -> Uart.connect(btnConnect));
        
        btnNewFile.setOnAction(event->TabPaneCode.addNewFileTab());
        
        btnReadFile.setOnAction(event->TreeViewFile.readFileSelected());
        
        btnExec.setOnAction((event)->TreeViewFile.execSelected());
        
    } // public void initialize(URL url, ResourceBundle rb) {
}
