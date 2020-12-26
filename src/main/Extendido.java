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

public class Extendido extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Extendidos.ser");
    private boolean especial = false;
    
    public Extendido(String linea, String mne, String oper){
        super(linea);
        this.mnemonico = mne;
        this.operando = oper;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
    }
    
    public Extendido(String linea, String mne, String etiqueta, boolean etiq){
        super(linea);
        this.mnemonico = mne;
        this.operando = etiqueta;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
        this.especial = true;
    }
    
    public Extendido(String linea, String etiqueta){
        super(linea);
        this.operando = etiqueta;
    }
    
    @Override
    public String toPrintToFile(){
        if(Main.segunda){
            if(!Etiqueta.contiene(operando))
                return Main.getLineNumber()+linea+"\n"+Main.getLineNumber()+getSpace(linea.indexOf(operando))+getError(3); //Etiqueta inexistente
            operando = Etiqueta.getEtiqueta(operando);
            return Main.getLineNumber()+linea.substring(0, linea.indexOf("J"))+operando+linea.substring(linea.indexOf("J")+4);
        }
        String aImprimir = Main.getAddress();
        if(especial){
            aImprimir += " "+opcode+" "+"JJJJ";
            Main.updateAddress(3);
            return aImprimir + getSpaceFor(aImprimir) + linea;
        }
        aImprimir = Main.getAddress() + " " + opcode+ " " + String.format("%04X", Integer.parseInt(operando,16)); 
        updateAddress();
        return aImprimir + getSpaceFor(aImprimir)+linea;    
    }    
    
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());        
    }
    
    private void updateAddress(){
        if(opcode.length() == 2)
            Main.updateAddress(3);
        else if(opcode.length() == 4)
            Main.updateAddress(4);
    }
    
    
}
