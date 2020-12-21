/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import herramientas.Serializador;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jorge
 */
public class Directo extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Directos.ser");
    private String especial = "";
    
    public Directo(String linea, String mne, String ope){
        super(linea);
        this.mnemonico = mne;
        this.operando = ope;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
    }
    
    public Directo(String linea, String mne, String ope, String isSpecial){
        super(linea);
        this.mnemonico = mne;
        this.operando = ope;
        this.especial = isSpecial;
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
    }
    
    @Override
    public String toPrintToFile(){
        String aImprimir = "";
        if(especial == "BR"){
            String partes[] = operando.split(",| ");
            String operando1 = operandoToHex(partes[0]);
            String operando2 = operandoToHex(partes[1].substring(1));
            String etiqueta = partes[2];
            
            if(operando1.length()>2)
                return "Magnitud de operando erronea";
            if(operando2.length()>2)
                return "Magnitud de operando erronea";
            if(!Etiqueta.contiene(etiqueta))
                return "Etiqueta inexistente"; 
            //caso $00,#$200 ETIQUETA?
        }
   
        aImprimir = Main.getAddress() + " " + opcode + operando; 
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
        int add = opcode.length() + operando.length();
        if(add == 4)
            Main.updateAddress(2);
        else if(add == 6)
            Main.updateAddress(3);
    }
        
    
}
