/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.util.Optional;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import org.fxmisc.richtext.CodeArea;

/**
 *
 * @author salesiopark
 */
public class TabPaneCode {
    
    private static TabPane tabPane;

    static public void init(TabPane tabPaneR) {
        tabPane = tabPaneR;
        
        // 모든 Tab에 x버튼을 보이게 한다.
        tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.ALL_TABS);
        
        //아래는 활성화된 Tab에만 x버튼을 보이게 한다.
        //tabPane.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.SELECTED_TAB);
        
        // main.py 파일탭은 기본으로 열어놓는다.
        // tabPane.getTabs().add(new TabPy("main.py"));
    }
    
    static public boolean isExist(String name) {
        for(Tab tab : tabPane.getTabs() ) {
            if ( tab.getText().equals(name) ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * tab을 생성하고 포커스를 그것으로 변경한다.
     * FileIO에서 CodeArea를 사용하므로 그것을 반환하도록 변경
     * @param name 파일이름을 탭이름으로 사용
     */
    static public CodeArea addTab(String name) {
        TabPy tabPy = new TabPy(name);
        tabPane.getTabs().add(tabPy);
        activateTab(name);
        return tabPy.getCodeArea();
    }
    
    static public void replaceCode(String nameTab, String code) {
        for(Tab tab : tabPane.getTabs() ) {
            if ( tab.getText().equals(nameTab) ) {
                ((TabPy)tab).replaceCode(code);
            }
        }
    }
    
    static public void activateTab(String name) {
       for(Tab tab : tabPane.getTabs() ) {
            if ( tab.getText().equals(name) ) {
                tabPane.getSelectionModel().select(tab);
            }
        }
    }

    /**
     * 주어진 이름의 탭을 TabPane에서 제거한다.
     * @param name 
     */
    static public void removeTab(String name) {
       for(Tab tab : tabPane.getTabs() ) {
            if ( tab.getText().equals(name) ) {
                tabPane.getTabs().remove(tab);
                break;
            }
        }
    }

    static public void addNewFileTab() {
       TextInputDialog tid = new TextInputDialog();
       tid.setTitle("New File");
       tid.setHeaderText("Enter file name");
       Optional<String> result = tid.showAndWait();
       result.ifPresent(filename -> addTab(filename) );
    }
    
    static public String getSelectedTabCode() {
        TabPy tabPy = (TabPy)tabPane.getSelectionModel().getSelectedItem();
        return tabPy.getCodeArea().getText();
    }
}
