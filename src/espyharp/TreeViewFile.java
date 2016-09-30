/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author salesiopark
 */
public class TreeViewFile {
    
    private static TreeItem<String> root = new TreeItem<String>("device/");
    private static TreeView<String> treeViewFiles;
            
    static private String strRcvd; // serialPort 에서 받은 문자열
    static private String nameSelected; // 더블클릭한 treeItem의 이름
    static private String commSent; // serialPort로 내보낸 명령
    
    public static void init(AnchorPane apParent) {
        root.setExpanded(true);
//        TreeView<String> treeViewFiles = new TreeView<String>(root);
        treeViewFiles = new TreeView<String>(root);
        Util.setCss(treeViewFiles, "default.css", "treeview");
        
        apParent.getChildren().add(treeViewFiles);
        Util.setAnchors(treeViewFiles, 94, 0, 0, 0); //23.0

        // 특정 item을 더블클릭했을 때
        treeViewFiles.setOnMouseClicked((mouseEvent)->{ //MouseEvent Object
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if(mouseEvent.getClickCount() == 2) {
                      readFileSelected();
//                    nameSelected = treeViewFiles.getSelectionModel().getSelectedItem().getValue();
//                    // Tab이 있다면 활성화
//                    if (TabPaneCode.isExist(nameSelected)) {
//                        TabPaneCode.activateTab(nameSelected);
//                    } else { // 없다면 새로 추가한 후 그것을 활성화함.
//                          TabPaneCode.addTab(nameSelected);
//                    }
//                    readFile(nameSelected);
                }
            }
        });
    }
    
    /**
     * 선택한 트리뷰아이템 이름이 탭에 있다면 그것을 활성화하고
     * 없다면 생성한 후 생성된 것을 활성화한다.
     * 그리고 readFile(name)함수를 호출한다.
     */
    public static void readFileSelected() {
        nameSelected = treeViewFiles.getSelectionModel().getSelectedItem().getValue();
        // Tab이 있다면 활성화
        if (TabPaneCode.isExist(nameSelected)) {
            TabPaneCode.activateTab(nameSelected);
        } else { // 없다면 새로 추가한 후 그것을 활성화함.
              TabPaneCode.addTab(nameSelected);
        }
        readFile(nameSelected);
    }
    
    public static void readFile(String name) {
        if (REPL.isIdle()) {
            strRcvd = "";
            Uart.setListener(new FileReader());
            commSent = String.format("print(open('%s').read())", name);
            Uart.exec(commSent);
        }
    }

    public static void readFileList() {
        if (REPL.isIdle()) {
            root.getChildren().clear();
//            System.out.println("readFileLIst() normal");
            strRcvd = "";
            Uart.setListener(new FileListReader());
            Uart.exec("uos.listdir()");
        }
    }
    
    /**
     * 파일의 내용을 디바이스에서 읽어서 tab에 그 내용을 올린다.
     */
    private static class FileReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = Uart.getPort().readString(event.getEventValue());
                    
                    //아래와 같이 하지 않으면 thread 에러가 발생한다.
                    // 현재 thread가 종료된 이후에 실행된다.
                    Platform.runLater( () -> {
                        strRcvd += receivedData;
                        if (receivedData.contains(">>> ")) {
                            REPL.setListener(">>> ");
                            System.out.println(strRcvd);
                            
                            // 보낸 명령이 앞에 붙고 ">>> \n"이 뒤에 붙는다.
                            // 앞뒤를 잘라내서 실제 코드만을 구한 뒤 Tab에 붙인다.
                            String code = strRcvd.substring(commSent.length()+2, strRcvd.length()-6);
                            TabPaneCode.replaceCode(nameSelected, code);
                        }
                    });
                        
                }
                catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
                System.out.println(strRcvd);
            }
        }
    }


    /**
     * 디바이스의 파일 리스트를 읽어 온 후 트리뷰에 등록한다.
     */
    private static class FileListReader implements SerialPortEventListener {

        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = Uart.getPort().readString(event.getEventValue());
                    
                    //아래와 같이 하지 않으면 thread 에러가 발생한다.
                    // 현재 thread가 종료된 이후에 실행된다.
//                    Platform.runLater( () -> {
                        strRcvd += receivedData;
                        if (receivedData.contains(">>> ")) {
                            REPL.setListener(">>> ");
                        }
                        Pattern pattern = Pattern.compile("'(.*?)'");
                        Matcher matcher = pattern.matcher(strRcvd);
                        while (matcher.find()) {
                            System.out.println("filename: "+matcher.group(1));
                            root.getChildren().add(new TreeItem<String>(matcher.group(1)));
                        }
//                    });
                        
                }
                catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
                System.out.println(strRcvd);
            }
        }
    }
    
    public static void removeSelected() {
        TreeItem<String> tiSelected = treeViewFiles.getSelectionModel().getSelectedItem();
        String nameSelected = tiSelected.getValue();
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove file on device");
        alert.setHeaderText(String.format("'%s' on the device will be removed.\nIt CANNOT be restored after removing.",nameSelected));
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            Uart.exec(String.format("uos.remove('%s')", nameSelected));
            root.getChildren().remove(tiSelected);//TreeView에서 제거하고
            TabPaneCode.removeTab(nameSelected);//Tab도 있다면 제거한다.
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    public static void execSelected() {
        String nameSelected = treeViewFiles.getSelectionModel().getSelectedItem().getValue();
        Uart.exec(String.format("exec(open('%s').read(),globals())", nameSelected));
    }    
}
