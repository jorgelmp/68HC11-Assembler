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
    
    public Extendido(String linea, String mne, String oper){
        super(linea);
        this.mnemonico = mne;
        this.operando = oper;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
    }
    
    @Override
    public String toPrintToFile(){
        String aImprimir = Main.getAddress() + " " + opcode + String.format("%04X", Integer.parseInt(operando,16)); 
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
