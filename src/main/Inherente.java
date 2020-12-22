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
public class Inherente extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Inherentes.ser");
    
    public Inherente(String linea, String mnemonico){
        super(linea);
        this.mnemonico = mnemonico;
        this.opcode = tabla.get(mnemonico.toLowerCase());        
    }
    
    @Override
    public String toPrintToFile(){
        String aImprimir = Main.getAddress() + " "+opcode;
        updateAddress();
        return aImprimir + Linea.getSpaceFor(aImprimir)+ linea;
    }
        
    private void updateAddress(){
        if(opcode.length() == 2)
            Main.updateAddress(1);
        else if(opcode.length() == 4)
            Main.updateAddress(2);
    }
    
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
}
