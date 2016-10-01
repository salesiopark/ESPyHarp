/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package espyharp;

//import static espyharp.REPL.serialPort;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * 시리얼 통신에 관련된 함수들의 모음
 * @author salesiopark
 */
public class Uart {
    
    // 다른 클래스의 PortReader 클래스에 사용하려면 public  이어야 한다.
    private static SerialPort serialPort = null;
    
    // Serial Port들의 이름을 가지고 있는 콤보박스
    private static ComboBox comboBoxPorts = null;
    
    
    /**
     * 시리얼포트의 이름을 알아내서 comboBox에 기록한다.
     * @param comboBox 
     */
    static public void init(ComboBox comboBox) {
        comboBoxPorts = comboBox;
        String[] ports = SerialPortList.getPortNames();
        for(int k=0; k<ports.length; k++) {
             comboBoxPorts.getItems().add(ports[k]);// warning 이유를 모르겠음
             comboBoxPorts.getSelectionModel().select(k);
        }
    }
    
    static public SerialPort getPort() {
        return serialPort;
    }
    
    static public void connect(Button btnConnect) {
        String strPort = comboBoxPorts.getSelectionModel().getSelectedItem().toString();
        serialPort = new SerialPort(strPort);
        try {
            serialPort.openPort();
            serialPort.setParams(115200, 8, 1, 0);//br, databits, stopbits, parity
            serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
            serialPort.purgePort(SerialPort.PURGE_TXCLEAR);
            
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.setEventsMask(mask);
            serialPort.addEventListener(new REPL.PortReader());
            
            serialPort.writeByte((byte)4); // soft-reset
            REPL.setDisable(false);//REPL을 활성화시킨다.
            btnConnect.setDisable(true);
        } catch (SerialPortException ex) {
            System.out.println("serial port open error: " + ex);
        }

    }
    
    /**
     * REPL에는 '\r'문자를 전송해야 명령이 실행된다.
     * @param strComm 전송할 문자열. 만약 null이 넘어오면 
     */
    static public void exec(String strComm){
        if (isNotOpened()) return;
        try{
            if (strComm==null) {
                serialPort.writeString("\r") ;
            } else {
                serialPort.writeString(strComm + '\r') ;
            }
        } catch (SerialPortException ex) {
            System.out.println("serial port open error: " + ex);
        }
    }
    
    static public void exec(){
        if (isNotOpened()) return;
        try{
           serialPort.writeString("\r") ;
        } catch (SerialPortException ex) {
            System.out.println("serial communication error: " + ex);
        }
    }
    
//    static public void sendByte(byte code){
    static public void sendByte(int icode){
        if (isNotOpened()) return;
        byte code = (byte)icode;
        try{
            serialPort.writeByte(code) ;
        } catch (SerialPortException ex) {
            System.out.println("serial port open error: " + ex);
        }
    }

    static public void setListener(SerialPortEventListener callback){
        if (isNotOpened()) return;
        try{
            serialPort.removeEventListener();
            serialPort.addEventListener(callback);
        } catch (SerialPortException ex) {
            System.out.println("serialPort adding event error: " + ex);
        }
    }
    
    public static boolean isNotOpened() {
        return (serialPort == null || !serialPort.isOpened());
    }
    
}
