/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

import java.util.function.IntFunction;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * tabPane에 추가하기 위해 Tab 객체를 상속받아서 TabPy 객체를 재정의한다.
 * CodeArea객체를 멤버로 가지고 있다.
 * @author salesiopark
 */
public class TabPy extends Tab {
    private CodeArea codeArea = new CodeArea();
    private String strSent;
    private String strRcvd;
    
    
    public TabPy(String fileName) {
        super(fileName);
        codeArea.setWrapText(true);
        
        // css 적용
        String stylesheet = ESPyHarp.class.getResource("pyTab.css").toExternalForm();
        codeArea.getStylesheets().add(stylesheet);

        // 줄번호 추가
        IntFunction<String> format = (digits -> " %" + digits + "d ");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea, format));

        /**
         * tab키를 " "*4 로 치환하는 필터 등록
         * addEventFilter함수를 사용해야 한다.
         * @see <a href="http://studymake.tistory.com/582">설명</a> 
         */
        codeArea.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            if (keyEvent.getCode() == KeyCode.TAB) {
                codeArea.insertText(codeArea.getCaretPosition(), "    ");
                keyEvent.consume();
            }
        });

        // 키워드 하이라이팅 (동기방식)
        // RichTextFx 의 java-keword 데모에서 복사함.
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, KWH.computeHighlighting(codeArea.getText()));
        });

        // codeAread를 VirtualizedScrollPane에 붙여서 TabPy에 붙인다.
        this.setContent(new VirtualizedScrollPane<>(codeArea));
    }
    
    public String getCodeText(int paragraph) {
        return codeArea.getText(paragraph);
    }
    
    private String getCommToUpload() {
        String  code="";
        // 각각의 문단을 하나씩 읽어서
        // 그 끝에 '\\n' 기호를 붙여야 한다.('\n'이 아님)
        try{
            int npara = 0;
            String paragraph = null;
            while (true) {
                paragraph = codeArea.getText(npara);

                // 인용부호는 모두 앞에 backslash를 붙여야 한다.
                // 여기에서 치환해야 아래의 "\\n"이 변형되지 않는다.
                paragraph = paragraph.replace("\\", "\\\\") // 먼저 '\' 문자는 '\\'로 바꾼다.
                    .replace("'", "\\'") // 그 다음 '는 \' 로 바꾸고
                    .replace("\"", "\\\"");  // 그리고 " 는 \" 로 바꾼다.

                code += paragraph;
                npara++;
                codeArea.getText(npara);//만약 다음 문단이 없다면(예외가 발생되어 )빠져나간다.
                code += "\\n"; //다음 문단이 있다면 줄바꿈기호를 붙인다.
            }
        } catch (Exception e) {
            //System.out.println("end of file");
        }
        
        // 'w'옵션으로 열면 반드시 닫아주어야 하는 것 같다.
        //  String str = String.format("open('%s','w').write('%s')",fn,code );
        // 따라서 위와 같이 하면 그 다음에 정상적으로 열리지 않는다.
        // 아래와 같이 반드시 닫아주어야 한다.
        return String.format(
                "__f__=open('%s','w');__f__.write('%s');__f__.close();del(__f__)",
                this.getText(),
                code
        );
        
    }

    // 단순히 upload(write)하는 함수
    public void upload() {
        strSent = getCommToUpload();
        strRcvd = "";
        REPL.appendText(String.format("writing '%s' ...", this.getText()));
        Uart.setListener(new FileWriter());
        Uart.exec( strSent );
    }
    
    public void uploadAndExec() {
        strSent = getCommToUpload()+String.format(";exec(open('%s').read(),globals())", this.getText());
        strRcvd = "";
        REPL.appendText(String.format("writing & executing '%s' ...", this.getText()));
        Uart.setListener(new FileWriter());
        Uart.exec( strSent );
    }
    
    public void replaceCode(String code) {
        codeArea.replaceText(code);
    }
    
    /**
     * 파일의 내용을 읽어서 tabPy에 그 내용을 올린다.
     */
    private class FileWriter implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = Uart.getPort().readString(event.getEventValue());
                    
                    //아래와 같이 하지 않으면 thread 에러가 발생한다.
                    // 현재 thread가 종료된 이후에 실행된다.
                    Platform.runLater( () -> {
                        strRcvd += receivedData;
                        // 보낸문자열+"\n"+"nnn" 이 돌아온다.
                        // 여기서 nnn은 파일의 크기
                        if (strRcvd.length() > strSent.length()+2) {
                            REPL.setListener("");
                            System.out.println(strRcvd);
                        }
                    });
                        
                }
                catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
//                System.out.println(strRcvd);
            }
        }
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }
}
