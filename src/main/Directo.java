/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;
/**
 *
 * @author jorge
 */
public class Directo extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Directos.ser");
    private boolean especial = false;
    private String operando1;
    private String operando2;
    
    public Directo(String linea, String mne, String ope){
        super(linea);
        this.mnemonico = mne;
        this.operando = ope;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));    
    }
    
    public Directo(String linea, String mne, String operando1, String operando2){
        super(linea);
        this.mnemonico = mne;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.especial = true;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
    }
    
    @Override
    public String toPrintToFile(){
        String aImprimir = "";
        if(especial){
            int error1, error2;
            error1 = linea.indexOf(operando1);
            error2 = linea.indexOf(operando2);
            operando1 = operandoToHex(operando1);
            if(operando1.length()>2)
                return generarError(linea,7,error1);
            operando2 = operandoToHex(operando2.substring(1));
            if(operando2.length()>4)
                return generarError(linea,7,error2);
            operando1 = String.format("%02X", Integer.parseInt(operando1,16));
            String format = (operando2.length()<3)? "%02X" : "%04X";
            operando2 = String.format(format, Integer.parseInt(operando2,16));
            operando = operando1+operando2;
            aImprimir += Main.getAddress()+" "+opcode+ " "+operando;
            updateAddress();
            return aImprimir+getSpaceFor(aImprimir)+linea;
            
        }
   
        aImprimir = Main.getAddress() + " " + opcode +" "+ operando; 
        updateAddress();
        return aImprimir + getSpaceFor(aImprimir)+linea;
    }
    
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase()).toUpperCase();
        
    }
    
    private void updateAddress(){
        if(especial){
            Main.updateAddress(2+operando2.length()/2);
            return;
        }
        int add = opcode.length() + operando.length();
        if(add == 4)
            Main.updateAddress(2);
        else if(add == 6)
            Main.updateAddress(3);
    }
        
    
}
