/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

/**
 *
 * @author salesiopark
 */
public class Util {
    
    /**
     * javafx.scene.layout.Region 의 자식 클래스에 css 지정
     * @param region AnchorPane, TreeView 등등
     * @param cssFileName
     * @param className 
     */
    public static void setCss(Region region, String cssFileName, String className) {
        String stylesheet = ESPyHarp.class.getResource(cssFileName).toExternalForm();
        region.getStylesheets().add(stylesheet);
        region.getStyleClass().add(className);
    }

    /**
     * Node의 네 방향anchor 를 지정하는 함수
     * @param node
     * @param top
     * @param bot
     * @param left
     * @param right 
     */
    public static void setAnchors(Node node, Double top, Double bot, Double left, Double right) {
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bot); 
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
    }
    
    public static void setAnchors(Node node, int top, int bot, int left, int right) {
        setAnchors(node, new Double(top), new Double(bot), new Double(left), new Double(right));
    }
}
