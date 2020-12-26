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

public class Inmediato extends Direccionamiento{
    private static final HashMap<String,String> tabla = Serializador.abrirHashMapString2("Inmediatos.ser");
    
    public Inmediato(String linea,String mnemonico, String operando){
        super(linea);
        this.mnemonico = mnemonico;
        this.operando = operando.substring(1);
        this.opcode = tabla.get(mnemonico.toLowerCase()).toUpperCase();
        String format = (opcode.length()<3)? "%02X" : "%04X";
        this.opcode = String.format(format, Integer.parseInt(opcode,16));
    }
    
    @Override
    public String toPrintToFile(){
        int  error = linea.indexOf(operando);
        operando = operandoToHex(operando);
        if(operando.isEmpty()){
            return generarError(linea,1,error); //Constante inexistente
        }
            
        if(operando.length()>4){            
            return generarError(linea,7,error); //Magnitud de operando errónea;
        }
        operando = (operando.length()<3)? String.format("%02X", Integer.parseInt(operando,16)): String.format("%04X", Integer.parseInt(operando,16));
        String aImprimir = Main.getAddress() + " " + opcode +  " " + operando;        
        updateAddress();
        return aImprimir + getSpaceFor(aImprimir) + linea;
    }
    
    private void updateAddress(){
        int add = opcode.length() + operando.length();
 
        if(add == 4)
            Main.updateAddress(2);
        else if(add == 6)
            Main.updateAddress(3);
        else if(add == 8)
            Main.updateAddress(4);
    }
    
    public static boolean contieneMne(String mne){
        return tabla.containsKey(mne.toLowerCase());
    }
    
    public static String obtenOpcode(String mne){
        return tabla.get(mne.toLowerCase());
        
    }
    
}
